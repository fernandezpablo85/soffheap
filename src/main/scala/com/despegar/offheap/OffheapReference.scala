package com.despegar.offheap

import java.io.ByteArrayOutputStream
import java.io.ObjectOutputStream
import java.io.BufferedOutputStream
import java.io.IOException
import java.io.ObjectInputStream
import java.io.BufferedInputStream
import java.io.ByteArrayInputStream
import java.io.Serializable
import java.util.concurrent.atomic.AtomicReference
import java.util.concurrent.locks.ReentrantReadWriteLock
import java.util.concurrent.atomic.AtomicLong
import com.esotericsoftware.kryo.Kryo
import com.esotericsoftware.kryo.io.Output
import com.esotericsoftware.kryo.io.Input
import com.despegar.offheap.serialization.Serializer

class OffheapReference[HeapObject](heapObject: HeapObject)(implicit val serializer: Serializer[HeapObject]) {

  val referenceCount = new AtomicLong(1)
  val offheapMemory = new UnsafeOffHeapMemory(serializer.serialize(heapObject))

  def get() = {
    val buffer = new Array[Byte](offheapMemory.length);
    offheapMemory.get(buffer);
    serializer.deserialize(buffer);
  }

  def reference(): Boolean = {
    while (true) {
      val n = referenceCount.get();
      if (n <= 0)
        return false;
      if (referenceCount.compareAndSet(n, n + 1))
        return true;
    }
    false
  }

  def unreference() = {
    if (referenceCount.decrementAndGet() == 0) {
      offheapMemory.free()
    }
  }

}