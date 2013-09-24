package com.despegar.offheap.serialization

import com.esotericsoftware.kryo.Kryo
import com.esotericsoftware.kryo.io.Output
import java.io.ByteArrayOutputStream
import com.esotericsoftware.kryo.io.Input
import scala.reflect._
import com.despegar.offheap.metrics.Metrics
import org.objenesis.strategy.StdInstantiatorStrategy
import com.despegar.offheap.FastObjectPool
import com.despegar.offheap.FastObjectPool.PoolFactory
import com.esotericsoftware.kryo.io.UnsafeOutput
import com.esotericsoftware.kryo.io.UnsafeInput
import java.lang.reflect.ParameterizedType
import com.despegar.offheap.Hack
import com.despegar.offheap.PibeHack

class KryoSerializer[T: ClassTag] extends Serializer[T] with Metrics {

  val classOfT = classTag[T].runtimeClass
  
  val kryoFactory = new Factory[Kryo] {
    def newInstance(): Kryo = {
      val kryo = new Kryo()
      kryo.register(classOfT)
      kryo.setReferences(false)
      kryo
    }
  }

  
  val pool = new FastObjectPool[Kryo](new PoolFactory[Kryo]() {
    override def create(): Kryo = {
      kryoFactory.newInstance()
    }
  }, 10)

  
  override def serialize(anObject: T): Array[Byte] = metrics.timer("serialize").time {
    val holder = pool.take()
    val kryo = holder.getValue()
    kryo.register(anObject.getClass)
    val outputStream = new ByteArrayOutputStream()
    val output = new UnsafeOutput(outputStream)
    kryo.writeClassAndObject(output, anObject)
    output.flush()
    val bytes = outputStream.toByteArray()
    output.close()
    pool.release(holder)
    bytes
  }

  override def deserialize(bytes: Array[Byte]): T = metrics.timer("deserialize").time {
    val holder = pool.take()
    try {
      val kryo = holder.getValue()
      val input = new UnsafeInput(bytes)
      val deserializedObject = kryo.readClassAndObject(input).asInstanceOf[T]
      deserializedObject
    } finally {
      pool.release(holder)
    }
  }

  trait Factory[T] {
    def newInstance(): T
  }
}