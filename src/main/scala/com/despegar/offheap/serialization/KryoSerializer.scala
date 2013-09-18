package com.despegar.offheap.serialization

import com.esotericsoftware.kryo.Kryo
import com.esotericsoftware.kryo.io.Output
import java.io.ByteArrayOutputStream
import com.esotericsoftware.kryo.io.Input
import scala.reflect._
import com.despegar.offheap.metrics.Metrics

class KryoSerializer[T: ClassTag] extends Serializer[T] with Metrics {

  override def serialize(anObject: T): Array[Byte] = {
    val timer = metrics.timer("serialize").time()
    var output: Output = null
    try {
      val kryo = new Kryo()
      kryo.setReferences(false)
      val outputStream = new ByteArrayOutputStream()
      output = new Output(outputStream)
      kryo.writeObject(output, anObject)
      output.flush()
      outputStream.toByteArray()
    } finally {
      timer.stop()
      if (output != null) {
        output.close()
      }
    }
  }

  override def deserialize(bytes: Array[Byte]): T = {
    val timer = metrics.timer("deserialize").time()
    try {
      val kryo = new Kryo()
      kryo.setReferences(false)
      val input = new Input(bytes)
      val classOfT = classTag[T].erasure
      println(s"the class of T is $classOfT")
      kryo.readObject(input, classOfT).asInstanceOf[T]
    } finally {
      timer.stop()
    }
  }

}