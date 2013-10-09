
package com.despegar.soffheap

import java.util.concurrent.atomic.AtomicInteger
import com.despegar.soffheap.serialization.Serializer

class OffheapReference[HeapObject](heapObject: HeapObject)(implicit val serializer: Serializer[HeapObject]) {

  val referenceCount = new AtomicInteger(1)
  doSerialization(heapObject)
  var address: Long = _
  var length: Int = _
  

  private def doSerialization(heapObject: HeapObject) = {
    val serialized = serializer.serialize(heapObject)
    length = serialized.length
    address = SoffHeap.allocate(length)
    SoffHeap.put(address,serialized)
  }

  def get() = {
	val buffer = new Array[Byte](length) 
    SoffHeap.get(address,buffer)
    serializer.deserialize(buffer)
  }

  def reference(): Boolean = {
    while (true) {
      val n = referenceCount.get()
      if (n <= 0)
        return false
      if (referenceCount.compareAndSet(n, n + 1))
        return true
    }
    false
  }

  def unreference() = {
    if (referenceCount.decrementAndGet() == 0) {
      free()
    }
  }

  def free() {
    SoffHeap.free(address, length)
  }
  
}