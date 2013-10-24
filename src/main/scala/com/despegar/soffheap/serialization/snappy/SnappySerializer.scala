package com.despegar.soffheap.serialization.snappy

import scala.reflect.ClassTag
import com.despegar.soffheap.serialization.Serializer
import java.io._
import org.xerial.snappy.{SnappyInputStream, SnappyOutputStream}
import com.despegar.soffheap.util.Managed._

class SnappySerializer[T: ClassTag] extends Serializer[T]  {

  def serialize(anObject: T): Array[Byte] =  {
    var bytes:Array[Byte] = Array.empty

    for {
      outputStream <- managed(new ByteArrayOutputStream())
      sos <- managed(new SnappyOutputStream(outputStream))
      oos <- managed(new ObjectOutputStream(sos))
    } {
      oos.writeObject(anObject)
      sos.flush()
      bytes = outputStream.toByteArray
    }
    bytes
  }

  def deserialize(bytes: Array[Byte]): T =  {
    deserialize(new ByteArrayInputStream(bytes))
  }

  def deserialize(inputStream: InputStream): T = {
    var obj:Any = null

    for {
      sis <- managed(new SnappyInputStream(inputStream))
      ois <- managed(new ObjectInputStream(sis))
    } {
     obj = ois.readObject
    }
    obj.asInstanceOf[T]
  }
}

object SnappySerializer {
  def apply[T: ClassTag]() = new SnappySerializer[T]
}

