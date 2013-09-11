package com.despegar.offheap

import java.util.concurrent.locks.ReentrantReadWriteLock

class ReadWriteLockOffheapObject[HeapObject <: Serializable](heapObject: HeapObject) extends OffheapObject[HeapObject](heapObject: HeapObject) {

   val readWriteLock = new ReentrantReadWriteLock()
  
   override def get() = {
    val readLock = readWriteLock.readLock()
    readLock.lock()
    var buffer:Array[Byte] = null
    try {
      buffer = doGet()
    } finally {
      readLock.unlock()
    }
    deserialize(buffer);
  }
    
   override def freeWhenIsSafe(offheapMemory: UnsafeOffHeapMemory) = {
    readWriteLock.writeLock().lock()
    offheapMemory.free()
    readWriteLock.writeLock().unlock()
  }
  
}