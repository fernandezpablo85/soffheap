package com.despegar.soffheap.snapshot

trait DataSource[Key, Value] {

  def get(): java.util.Map[Key, Value]
  
}