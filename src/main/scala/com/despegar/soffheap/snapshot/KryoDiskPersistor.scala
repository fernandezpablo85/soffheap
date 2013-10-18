package com.despegar.soffheap.snapshot

import com.despegar.soffheap.serialization.kryo.KryoSerializer
import java.io.FileOutputStream
import java.io.File


class KryoDiskPersistor(path: String, name: String) extends DiskPersistor {

  val serializer = new KryoSerializer[Map[_,_]](s"DiskPersistor-$name")
  
  override def persist(values: Map[_,_]) = {
    val fileOutputStream = new FileOutputStream(null.asInstanceOf[File])
    val bytes = serializer.serialize(values)
    fileOutputStream.write(bytes)
    fileOutputStream.flush()
    fileOutputStream.close()
  }
  
  override def loadFromDisk: Map[_, _] = {
    Map.empty
  }
  
  override def hasData: Boolean = {
     false
  }
  
}