package com.despegar.soffheap.snapshot

trait DataSource[Key, Value] {

  def get(): Map[Key, Value]
  
}