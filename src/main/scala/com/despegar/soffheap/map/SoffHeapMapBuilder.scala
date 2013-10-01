package com.despegar.soffheap.map

import com.despegar.soffheap.heapcache.{CacheFactory, HeapCache, NoHeapCache}
import scala.collection.mutable.ListBuffer
import com.despegar.soffheap.map.j.SoffHeapMapImpl
import com.despegar.soffheap.serialization.Serializer
import com.despegar.soffheap.serialization.kryo.KryoSerializerFactory
import com.despegar.soffheap.serialization.SerializerFactory
import com.despegar.soffheap.serialization.fst.FSTSerializerFactory
import java.util.UUID

class SoffHeapMapBuilder[K, V] {

  private var maxHeapElements:Option[Int] = None
  private val hintedClasses = new ListBuffer[Class[_]]()
  private var serializerFactory:Option[SerializerFactory] = None
  private var soffHeapName:Option[String] = None

  def withName(name:String) = {
    soffHeapName =  Some(name)
    SoffHeapMapBuilder.this
  }

  def withMaximumHeapElements(elements: Int) = {
    maxHeapElements = Some(elements)
    SoffHeapMapBuilder.this
  }

  def withMaxSoffHeapMemoryInGB(size: Long) = {
    System.setProperty("maxSoffHeapMemoryInGB", s"$size")
    SoffHeapMapBuilder.this
  }
  
  def withHintedClass(hintedClass: Class[_]) = {
    hintedClasses += hintedClass
    SoffHeapMapBuilder.this
  }
  
  def withKryo = {
    this.serializerFactory = Some(new KryoSerializerFactory())
    SoffHeapMapBuilder.this
  }
  
  def withFST = {
    this.serializerFactory = Some(new FSTSerializerFactory())
    SoffHeapMapBuilder.this
  }

  def build() = {
    val name = s"${soffHeapName.getOrElse(UUID.randomUUID().toString)}."

    implicit val cache: HeapCache[K, V] = createHeapCache(name)
    implicit val serializerOfV: Serializer[V] = createSerializer(name)

    new SoffHeapMap[K,V](name)
  }
  
  def createSerializer(name:String): Serializer[V] = {
     val factory = serializerFactory.getOrElse(new KryoSerializerFactory())
     val serializer = factory.create[V](name, hintedClasses.toList)
     serializer
  }
  
  
  def buildJ() = {
    new SoffHeapMapImpl[K,V](build())
  }
  

  private def createHeapCache(name:String): HeapCache[K, V] = {
     if (maxHeapElements.isDefined && maxHeapElements.get > 0) CacheFactory.create(name,maxHeapElements.get.toLong)
     else new NoHeapCache[K, V](name)
  }
  
  
}

object SoffHeapMapBuilder {
  def of[Key, Value]() = {
    new SoffHeapMapBuilder[Key, Value]()
  }
  
}