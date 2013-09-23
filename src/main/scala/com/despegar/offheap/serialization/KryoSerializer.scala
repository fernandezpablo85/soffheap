package com.despegar.offheap.serialization

import com.esotericsoftware.kryo.Kryo
import com.esotericsoftware.kryo.io.Output
import java.io.ByteArrayOutputStream
import com.esotericsoftware.kryo.io.Input
import com.despegar.offheap.metrics.Metrics
import org.objenesis.strategy.StdInstantiatorStrategy

class KryoSerializer[T] extends Serializer[T] with Metrics {

  override def serialize(anObject: T): Array[Byte] = metrics.timer("serialize").time {
    val kryo = kryoFactory.newInstance()
    val outputStream = new ByteArrayOutputStream()
    val output = new Output(outputStream)
    kryo.writeClassAndObject(output, anObject)
    output.flush()
    val bytes = outputStream.toByteArray()
    output.close()
    bytes
  }

  override def deserialize(bytes: Array[Byte]): T = metrics.timer("deserialize").time {
    val kryo = kryoFactory.newInstance()
    kryo.readClassAndObject(new Input(bytes)).asInstanceOf[T]
  }

  val kryoFactory = new Factory[Kryo] {
    def newInstance(): Kryo = {
      val kryo = new Kryo()
      kryo.setInstantiatorStrategy(new StdInstantiatorStrategy())
      kryo.setReferences(false)
      kryo
    }
  }

  trait Factory[T] {
    def newInstance(): T
  }
}