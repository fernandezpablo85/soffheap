package com.despegar.offheap.serialization

import com.esotericsoftware.kryo.Kryo
import com.esotericsoftware.kryo.io.Output
import java.io.ByteArrayOutputStream
import com.esotericsoftware.kryo.io.Input

class KryoSerializer[T] extends Serializer[T] {

  override def serialize(anObject: T): Array[Byte] = {
    var output: Output = null
    try {
      val kryo = new Kryo()
      val output = new Output(new ByteArrayOutputStream())
      kryo.writeClassAndObject(output, anObject)
      output.getBuffer()
    } finally {
    	if (output != null) {
    		output.close()
    	}
    }
  }

  override def deserialize(bytes: Array[Byte]): T = {
    val kryo = new Kryo()
    val input = new Input(bytes)
    kryo.readClassAndObject(input).asInstanceOf[T]
  }

}