package com.despegar.soffheap.serialization

trait Serializer[T] {

  def serialize(anObject: T): Array[Byte]

  def deserialize(bytes: Array[Byte]): T
  
  def deserialize(inputStream: java.io.InputStream): T
  
}

trait SerializerFactory {
  
  def create[T](name:String, hintedClasses: List[Class[_]]): Serializer[T]
}