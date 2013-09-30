package com.despegar.soffheap.serialization

trait Serializer[T] {

  def serialize(anObject: T): Array[Byte]

  def deserialize(bytes: Array[Byte]): T
  
}

trait SerializerFactory {
  
  def create[T](name:String, hintedClasses: List[Class[_]]): Serializer[T]
}