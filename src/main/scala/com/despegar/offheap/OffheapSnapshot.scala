package com.despegar.offheap

trait OffheapSnapshot[T <: Serializable] {

  def reload(data: T)
  
  def getData(): T
  
}