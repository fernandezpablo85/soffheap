package com.despegar.soffheap.serialization.kryo

import com.esotericsoftware.kryo.Kryo
import scala.reflect._
import com.despegar.soffheap.serialization.Serializer
import com.despegar.soffheap.metrics.Metrics
import java.io.ByteArrayOutputStream
import com.esotericsoftware.kryo.io.UnsafeOutput
import com.esotericsoftware.kryo.io.UnsafeInput
import java.util.concurrent.ConcurrentLinkedQueue

class KryoSerializer[T: ClassTag] extends Serializer[T] with Metrics {

  private[this] val serializeTimer = metrics.timer("serialize")
  private[this] val deserializeTimer = metrics.timer("deserialize")

  val classOfT = classTag[T].runtimeClass

  val kryoFactory = new Factory[Kryo] {
    def newInstance(): Kryo = {
      val kryo = new Kryo()
      kryo.register(classOfT)
      kryo.setReferences(false)
      kryo
    }
  }

  val pool = new KryoPool(kryoFactory, 10)

  override def serialize(anObject: T): Array[Byte] = serializeTimer.time {
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
    } catch {
      case e: Exception => e.printStackTrace(); throw new RuntimeException(e)
    } finally {
      pool.release(kryoHolder)
    }
  }

  override def deserialize(bytes: Array[Byte]): T = deserializeTimer.time {
    val kryoHolder = pool.take()
    try {
      val kryo = kryoHolder
      val input = new UnsafeInput(bytes)
      val deserializedObject = kryo.readClassAndObject(input).asInstanceOf[T]
      deserializedObject
    } catch {
      case e: Exception => e.printStackTrace(); throw new RuntimeException(e)
    } finally {
      pool.release(kryoHolder)
    }
  }

}

trait Factory[T] {
  def newInstance(): T
}

class KryoPool(factory: Factory[Kryo], kryoInstances: Int) {

  val objects = new ConcurrentLinkedQueue[Kryo]();

  (1 to kryoInstances) foreach { _ =>  objects.offer(factory.newInstance())}
  
  def take(): Kryo = {
    val pooledKryo = objects.poll()
    if (pooledKryo == null) {
      return factory.newInstance()
    }
    return pooledKryo
  }

  def release(kh: Kryo) = {
    objects.offer(kh)
  }

  def close() = {
    objects.clear()
  }

}