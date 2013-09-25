package com.despegar.offheap.map

import com.despegar.offheap.{CacheFactory, HeapCache}
import scala.reflect.ClassTag


class OffheapMapBuilder[K, V: ClassTag] {

  private var theElements:Option[Int] = None

  def withMaximumHeapElements(elements: Int) = {
    theElements = Some(elements)
    this
  }

  def withMaxSoffHeapMemoryInGB(size: Long) = {
    System.setProperty("maxSoffHeapMemoryInGB", s"$size")
    this
  }

  def build() = {
    implicit val cache: HeapCache[K, V] = CacheFactory.create(theElements.getOrElse(100).toLong)
    new OffheapMapSnapshot[K,V]()
  }
  
}