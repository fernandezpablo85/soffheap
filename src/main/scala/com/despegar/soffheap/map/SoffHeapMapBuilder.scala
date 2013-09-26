package com.despegar.soffheap.map

import com.despegar.soffheap.heapcache.{CacheFactory, HeapCache, NoHeapCache}
import scala.reflect.ClassTag
import scala.collection.mutable.ListBuffer
import com.despegar.soffheap.map.j.SoffHeapMapImpl
import com.despegar.soffheap.serialization.Serializer
import com.despegar.soffheap.serialization.fst.FSTSerializer
import com.despegar.soffheap.serialization.kryo.KryoSerializer
import com.despegar.soffheap.serialization.fst.FSTSerializer
import com.despegar.soffheap.serialization.fst.FSTSerializer
import com.despegar.soffheap.serialization.kryo.KryoSerializerFactory
import com.despegar.soffheap.serialization.SerializerFactory
import com.despegar.soffheap.serialization.fst.FSTSerializerFactory
import com.despegar.soffheap.serialization.fst.FSTSerializerFactory

class SoffHeapMapBuilder[K, V] {

  private var maxHeapElements:Option[Int] = None
  private val hintedClasses = new ListBuffer[Class[_]]()
  private var serializerFactory:Option[SerializerFactory] = None	
  
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
    implicit val cache: HeapCache[K, V] = createHeapCache()
    implicit val serializerOfV: Serializer[V] = createSerializer()
    new SoffHeapMap[K,V]()
  }
  
  def createSerializer(): Serializer[V] = {
     val factory = serializerFactory.getOrElse(new KryoSerializerFactory())
     val serializer = factory.create[V](hintedClasses.toList)
     serializer
  }
  
  
  def buildJ() = {
    new SoffHeapMapImpl[K,V](build())
  }
  
  
  private def createHeapCache(): HeapCache[K, V] = {
     if (maxHeapElements.isDefined && maxHeapElements.get > 0) CacheFactory.create(maxHeapElements.get.toLong)
     else new NoHeapCache[K, V]()
  }
  
  
}

object SoffHeapMapBuilder {
  
  def of[Key, Value]() = {
    new SoffHeapMapBuilder[Key, Value]()
  }
  
}