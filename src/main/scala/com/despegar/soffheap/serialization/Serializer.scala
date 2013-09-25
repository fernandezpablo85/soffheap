package com.despegar.soffheap.serialization

trait Serializer[T] {

  def serialize(anObject: T): Array[Byte]

  def deserialize(bytes: Array[Byte]): T
  
}