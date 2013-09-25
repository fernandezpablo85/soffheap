package com.despegar.soffheap.map

import com.despegar.soffheap.heapcache.{CacheFactory, HeapCache}

import scala.reflect.ClassTag

class SoffHeapMapBuilder[K, V: ClassTag] {

  private var theElements:Option[Int] = None

  def withMaximumHeapElements(elements: Int) = {
    theElements = Some(elements)
    SoffHeapMapBuilder.this
  }

  def withMaxSoffHeapMemoryInGB(size: Long) = {
    System.setProperty("maxSoffHeapMemoryInGB", s"$size")
    SoffHeapMapBuilder.this
  }

  def build() = {
    implicit val cache: HeapCache[K, V] = CacheFactory.create(theElements.getOrElse(100).toLong)
    new SoffHeapMap[K,V]()
  }
  
  
}