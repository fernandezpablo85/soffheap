package com.despegar.soffheap

import scala.collection.mutable.Map

trait ScalaSnapshotTrait[Key, Value] {

  def put(key: Key, value: Value): Unit
  
  def get(key: Key): Option[Value]

  def reload(map: Map[Key, Value]): Unit
  
  def reload(iterable: Iterable[(Key, Value)]): Unit
}