package com.despegar.soffheap.serialization.kryo

import com.esotericsoftware.kryo.Kryo
import com.despegar.soffheap.serialization.Serializer
import com.despegar.soffheap.metrics.Metrics
import java.io.ByteArrayOutputStream
import com.esotericsoftware.kryo.io.UnsafeOutput
import com.esotericsoftware.kryo.io.UnsafeInput
import java.util.concurrent.ConcurrentLinkedQueue
import com.despegar.soffheap.serialization.SerializerFactory
import com.despegar.soffheap.LongAdder
import java.io.InputStream

class KryoSerializer[T](name:String, hintedClasses: List[Class[_]] = List.empty) extends Serializer[T] with Metrics {

  val kryoFactory = new Factory[Kryo] {
    def newInstance(): Kryo = {
      val kryo = new Kryo()
      kryo.setReferences(false)
      hintedClasses.foreach( hintedClass => kryo.register(hintedClass))
      kryo
    }
  }

  val pool = new KryoPool(name, kryoFactory, 10)

  override def serialize(anObject: T): Array[Byte] = {
    val kryoHolder = pool.take()
    try {
      val kryo = kryoHolder
      val outputStream = new ByteArrayOutputStream()
      val output = new UnsafeOutput(outputStream)
      kryo.writeClassAndObject(output, anObject)
      output.flush()
      val bytes = outputStream.toByteArray()
      output.close()
      bytes
    } finally {
      pool.release(kryoHolder)
    }
  }

  override def deserialize(bytes: Array[Byte]): T = {
     deserialize(new UnsafeInput(bytes))
  }

  override def deserialize(inputStream: InputStream): T = {
    deserialize(new UnsafeInput(inputStream))
  }

  private def deserialize(input: UnsafeInput): T = {
    val kryo = pool.take()
    try {
      kryo.readClassAndObject(input).asInstanceOf[T]
    }  finally {
      pool.release(kryo)
    }
  }

  def metricsPrefix: String = name
}

trait Factory[T] {
  def newInstance(): T
}

class KryoPool(name: String, factory: Factory[Kryo], initInstances: Int) extends Metrics {
  val instances = new LongAdder()
  instances.add(initInstances)
  val maxInstances = initInstances * 2
  val objects = new ConcurrentLinkedQueue[Kryo]()
  val kryoInstancesGauge = safeGauge(s"${name}kryoInstances") {
     instances.intValue()
  }

  (1 to initInstances) foreach { _ =>  objects.offer(factory.newInstance())}

  def take(): Kryo = {
    val pooledKryo = objects.poll()
    if (pooledKryo == null) {
      return factory.newInstance()
    }
    instances.decrement()
    return pooledKryo
  }

  def release(kh: Kryo) = {
    if (instances.intValue() < maxInstances) {
      instances.increment()
      objects.offer(kh)
    }
  }

  def close() = {
    objects.clear()
  }

  override def metricsPrefix = name

}


class KryoSerializerFactory extends SerializerFactory {

  override def create[T](name:String, hintedClasses: List[Class[_]]) = {
     new KryoSerializer[T](name, hintedClasses)
  }
}

