package com.despegar.offheap.serialization

trait Serializer[T] {

  def serialize(anObject: T): Array[Byte]

  def deserialize(bytes: Array[Byte]): T
  
}