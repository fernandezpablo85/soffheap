package com.despegar.offheap

import java.io.Serializable
import java.util.concurrent.atomic.AtomicReference

case class SnapshotValue(someString: String, someLong: Long) {
  
  def this() = { this(null,0l)}
  
}