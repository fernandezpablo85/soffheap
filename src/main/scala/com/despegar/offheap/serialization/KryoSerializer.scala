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
import scala.util.Try
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

  val pool = new KryoPool(kryoFactory)
//  val poolFactory = new PoolFactory[Kryo]() {
//    override def create() = {
//       kryoFactory.newInstance()
//    }
//  }
//  val pool = new FastObjectPool[Kryo](poolFactory, 100)

  override def serialize(anObject: T): Array[Byte] = serializeTimer.time {
    val kryoHolder = pool.take()
    try {
//              val kryo =  kryoHolder.getValue()
                            val kryo =  kryoHolder

      kryo.register(anObject.getClass)
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
//              val kryo =  kryoHolder.getValue()
                            val kryo =  kryoHolder
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

class KryoPool(factory: Factory[Kryo]) {

  val objects = new ConcurrentLinkedQueue[Kryo]();

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