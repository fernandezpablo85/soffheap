package com.despegar.soffheap.serialization.fst

import com.despegar.soffheap.serialization.Serializer
import com.despegar.soffheap.metrics.Metrics
import java.io.ByteArrayOutputStream
import java.io.ByteArrayInputStream
import java.io.InputStream
import java.io.OutputStream
import de.ruedigermoeller.serialization.FSTConfiguration
import com.despegar.soffheap.serialization.SerializerFactory

class FSTSerializer[T](name:String, hintedClasses: List[Class[_]] = List.empty) extends Serializer[T] with Metrics  {

  private[this] val serializeTimer = metrics.timer(s"${metricsPrefix}serialize")
  private[this] val deserializeTimer = metrics.timer(s"${metricsPrefix}deserialize")

  System.setProperty("fst.unsafe","true")
  
  val conf = FSTConfiguration.createDefaultConfiguration()
  
  conf.setPreferSpeed(true)
  conf.setShareReferences(false)
  conf.setCrossLanguage(false)
  hintedClasses.foreach( hintedClass => conf.getClassRegistry().registerClass(hintedClass))

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
  
  override def serialize(anObject: T): Array[Byte] = serializeTimer.time {
    val stream = new ByteArrayOutputStream()
    mywriteMethod[T](stream, anObject)
    stream.toByteArray
  }

  override def deserialize(bytes: Array[Byte]): T = {
//    deserializeTimer.time {
    val stream = new ByteArrayInputStream(bytes)
    myreadMethod[T](stream)
  }

  def metricsPrefix: String = name
}

class FSTSerializerFactory extends SerializerFactory {
  
  override def create[T](name:String, hintedClasses: List[Class[_]]) = {
     new FSTSerializer[T](name, hintedClasses)
  }
}
