package com.despegar.soffheap

trait OffheapSnapshot[T <: Serializable] {

  def reload(data: T)
  
  def getData(): T
  
}