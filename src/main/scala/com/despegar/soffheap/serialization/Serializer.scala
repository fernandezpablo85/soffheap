package com.despegar.soffheap.serialization

trait Serializer[T] {

  def serialize(anObject: T): Array[Byte]

  def deserialize(bytes: Array[Byte]): T
  
}

trait SerializerFactory {
  
  def create[T](hintedClasses: List[Class[_]]): Serializer[T]
}