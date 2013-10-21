package com.despegar.soffheap.serialization.j

import scala.reflect.ClassTag
import com.despegar.soffheap.serialization.Serializer
import java.io.ByteArrayOutputStream
import java.io.ObjectOutputStream
import java.io.BufferedOutputStream
import java.io.ObjectInputStream
import java.io.BufferedInputStream
import java.io.ByteArrayInputStream
import java.io.InputStream

class JavaSerializer[T: ClassTag] extends Serializer[T] {

   override def serialize(anObject: T): Array[Byte] = {
		val byteArrayOutputStream = new ByteArrayOutputStream()
		var objectOutputStream: ObjectOutputStream = null
		try {
			objectOutputStream = new ObjectOutputStream(new BufferedOutputStream(byteArrayOutputStream))
			objectOutputStream.writeObject(anObject)
		}
		finally {
				objectOutputStream.flush()
				objectOutputStream.close()
		}
		byteArrayOutputStream.toByteArray()
  }

  override def deserialize(bytes: Array[Byte]): T = {
		val objectInputStream = new ObjectInputStream(new BufferedInputStream(new ByteArrayInputStream(bytes)))
		 objectInputStream.readObject().asInstanceOf[T]
  }
  
  override def deserialize(inputStream: InputStream): T = {
    		val objectInputStream = new ObjectInputStream(inputStream)
		 objectInputStream.readObject().asInstanceOf[T]
  }
  
}