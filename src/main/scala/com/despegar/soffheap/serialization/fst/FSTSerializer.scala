package com.despegar.soffheap.serialization.fst

import scala.reflect.ClassTag
import com.despegar.soffheap.serialization.Serializer
import com.despegar.soffheap.metrics.Metrics
import java.io.ByteArrayOutputStream
import java.io.ByteArrayInputStream
import java.io.InputStream
import java.io.OutputStream
import de.ruedigermoeller.serialization.FSTConfiguration
import com.despegar.soffheap.ProviderMappingInfo

class FSTSerializer[T: ClassTag] extends Serializer[T] with Metrics  {

  private[this] val serializeTimer = metrics.timer("serialize")
  private[this] val deserializeTimer = metrics.timer("deserialize")

  override def serialize(anObject: T): Array[Byte] = serializeTimer.time {
    val stream = new ByteArrayOutputStream()
    FastSerializer.mywriteMethod[T](stream, anObject)
    stream.toByteArray
  }

  override def deserialize(bytes: Array[Byte]): T = deserializeTimer.time {
    val stream = new ByteArrayInputStream(bytes)
    FastSerializer.myreadMethod[T](stream)
  }
}

object FastSerializer {

  System.setProperty("fst.unsafe","true")

  val conf = FSTConfiguration.createDefaultConfiguration()
  
  conf.setPreferSpeed(true)
  conf.setShareReferences(false)
  conf.setCrossLanguage(false)
//  conf.getClassRegistry().registerClass(classOf[ProviderMappingInfo])

  def myreadMethod[T](stream:InputStream):T = {
    val in = conf.getObjectInput(stream);
    val result = in.readObject();
    // DON'T: in.close(); prevents reuse and will result in an exception
    stream.close();
    result.asInstanceOf[T]
  }

  def mywriteMethod[T](stream: OutputStream , toWrite:Any ) = {
    val out = conf.getObjectOutput(stream);
    out.writeObject( toWrite, classOf[Any]);
    // DON'T out.close();
    out.flush();
    stream.close();
  }
}