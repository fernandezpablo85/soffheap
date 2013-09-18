package com.despegar.offheap.serialization

import scala.reflect.ClassTag

trait Serializer[T] {

  def serialize(anObject: T): Array[Byte]
  
  def deserialize(bytes: Array[Byte]): T
  
}