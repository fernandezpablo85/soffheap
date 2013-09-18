package com.despegar.offheap

case class SnapshotValue(someString: String, someLong: Long) {
  
  def this() = { this(null,0l)}
  
}