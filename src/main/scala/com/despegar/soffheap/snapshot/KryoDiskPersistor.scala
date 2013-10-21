package com.despegar.soffheap.snapshot

import com.despegar.soffheap.serialization.kryo.KryoSerializer
import java.io.FileOutputStream
import java.io.File
import java.io.FileInputStream


class KryoDiskPersistor(path: String, name: String) extends DiskPersistor {

  val serializer = new KryoSerializer[Map[_,_]](s"DiskPersistor-$name")
  
  override def persist(values: Map[_,_]) = {
    val fileOutputStream = new FileOutputStream(file)
    val bytes = serializer.serialize(values)
    fileOutputStream.write(bytes)
    fileOutputStream.flush()
    fileOutputStream.close()
  }
  
  private def file(): File = {
    val snapshotFile = new File(s"$path/$name.bin")
    DiskPersistor.createPathTo(snapshotFile)
    snapshotFile
  }
  
  override def loadFromDisk: Map[_, _] = {
    val fileInputStream = new FileInputStream(file)
    val values = serializer.deserialize(fileInputStream)
    fileInputStream.close()
    values.asInstanceOf[Map[_, _]]
  }
  
  override def hasData: Boolean = {
     file().exists()
  }
  
}