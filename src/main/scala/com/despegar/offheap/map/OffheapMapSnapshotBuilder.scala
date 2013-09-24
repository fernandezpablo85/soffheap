package com.despegar.offheap.map

import com.despegar.offheap.{CacheFactory, HeapCache}
import scala.reflect.ClassTag


class OffheapMapBuilder[K, V: ClassTag] {

  private var theElements:Option[Int] = None
  private var theSize:Option[Long] = None

  def withMaximumHeapElements(elements: Int) = {
    theElements = Some(elements)
    this
  }

  def withMaximumOffheapSize(size: Long) = {
    theSize = Some(size)
    this
  }

  def build() = {
    implicit val cache: HeapCache[K, V] = CacheFactory.create(theSize.getOrElse(100l).toLong)
    new OffheapMapSnapshot[K,V]()
  }
  
}