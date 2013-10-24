package com.despegar.soffheap.snapshot

import scala.collection.mutable.ListBuffer
import com.despegar.soffheap.serialization.SerializerFactory
import com.despegar.soffheap.serialization.kryo.KryoSerializerFactory
import com.despegar.soffheap.serialization.fst.FSTSerializerFactory
import java.util.UUID
import com.despegar.soffheap.heapcache.HeapCache
import com.despegar.soffheap.serialization.Serializer
import com.despegar.soffheap.map.j.SoffHeapMapImpl
import com.despegar.soffheap.heapcache.NoHeapCache
import com.despegar.soffheap.heapcache.CacheFactory
import com.despegar.soffheap.map.SoffHeapMap


abstract class TunneableSoffheapMapBuilder[T, K, V] {

  private var maxHeapElements:Option[Int] = None
  private val hintedClasses = new ListBuffer[Class[_]]()
  private var serializerFactory:Option[SerializerFactory] = None
  protected var soffHeapName: String = s"${UUID.randomUUID().toString}."
  
  def self(): T
  
  def withName(name:String) = {
    soffHeapName =  name
    self
  }

  def withMaximumHeapElements(elements: Int) = {
    maxHeapElements = Some(elements)
    self
  }

  def withHintedClass(hintedClass: Class[_]) = {
    hintedClasses += hintedClass
    self
  }
  
  def withKryo = {
    this.serializerFactory = Some(new KryoSerializerFactory())
    self
  }
  
  def withFST = {
    this.serializerFactory = Some(new FSTSerializerFactory())
    self
  }

  protected def innerBuildSoffHeapMap() = {
    val name = soffHeapName

    implicit val cache: HeapCache[K, V] = createHeapCache(name)
    implicit val serializerOfV: Serializer[V] = createSerializer(name)

    new SoffHeapMap[K, V](name)
  }
  
  def createSerializer(name:String): Serializer[V] = {
     val factory = serializerFactory.getOrElse(new KryoSerializerFactory())
     val serializer = factory.create[V](name, hintedClasses.toList)
     serializer
  }
  
  protected def innerBuildSoffHeapMapJ() = {
    new SoffHeapMapImpl[K,V](innerBuildSoffHeapMap())
  }
  
  private def createHeapCache(name:String): HeapCache[K, V] = {
     if (maxHeapElements.isDefined && maxHeapElements.get > 0) CacheFactory.create(name,maxHeapElements.get.toLong)
     else new NoHeapCache[K, V](name)
  }
  
}