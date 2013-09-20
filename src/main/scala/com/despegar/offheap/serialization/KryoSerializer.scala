package com.despegar.offheap.serialization

import com.esotericsoftware.kryo.Kryo
import com.esotericsoftware.kryo.io.Output
import java.io.ByteArrayOutputStream
import com.esotericsoftware.kryo.io.Input
import scala.reflect._
import com.despegar.offheap.metrics.Metrics
import org.objenesis.strategy.StdInstantiatorStrategy

class KryoSerializer[T: ClassTag] extends Serializer[T] with Metrics {

  val classOfT = classTag[T].runtimeClass

  override def serialize(anObject: T): Array[Byte] = metrics.timer("serialize").time {
    val kryo = kryoFactory.newInstance()
    val outputStream = new ByteArrayOutputStream()
    val output = new Output(outputStream)
    kryo.writeObject(output, anObject)
    output.flush()
    val bytes = outputStream.toByteArray()
    output.close()
    bytes
  }

  override def deserialize(bytes: Array[Byte]): T = metrics.timer("deserialize").time {
    val kryo = kryoFactory.newInstance()
    val input = new Input(bytes)
    kryo.readObject(input, classOfT).asInstanceOf[T]
  }

  val kryoFactory = new Factory[Kryo] {
    def newInstance(): Kryo = {
      val kryo = new Kryo()
//      kryo.setInstantiatorStrategy(new StdInstantiatorStrategy())
      kryo.setReferences(false)
      kryo
    }
  }

  trait Factory[T] {
    def newInstance(): T
  }
}