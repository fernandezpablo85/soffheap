package com.despegar.soffheap.map

import scala.reflect.ClassTag

class TypedBuilder[Key, Value] {

  def createBuilder[Value](aType: Class[_]) = {
     implicit val classTag = new ClassTag[Value](){ def runtimeClass = aType }
     new SoffHeapMapBuilder[Key, Value]()
  }
  
}