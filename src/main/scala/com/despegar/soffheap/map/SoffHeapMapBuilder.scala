package com.despegar.soffheap.map

import com.despegar.soffheap.heapcache.{CacheFactory, HeapCache, NoHeapCache}
import scala.reflect.ClassTag

class SoffHeapMapBuilder[K, V: ClassTag] {

  private var maxHeapElements:Option[Int] = None

  def withMaximumHeapElements(elements: Int) = {
    maxHeapElements = Some(elements)
    SoffHeapMapBuilder.this
  }

  def withMaxSoffHeapMemoryInGB(size: Long) = {
    System.setProperty("maxSoffHeapMemoryInGB", s"$size")
    SoffHeapMapBuilder.this
  }

  def build() = {
    implicit val cache: HeapCache[K, V] = createHeapCache()
    new SoffHeapMap[K,V]()
  }
  
  private def createHeapCache(): HeapCache[K, V] = {
     if (maxHeapElements.isDefined && maxHeapElements.get > 0) CacheFactory.create(maxHeapElements.get.toLong)
     else new NoHeapCache[K, V]()
  }
  
  
}