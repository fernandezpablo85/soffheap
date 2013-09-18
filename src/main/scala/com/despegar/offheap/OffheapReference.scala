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
import com.esotericsoftware.kryo.Kryo
import com.esotericsoftware.kryo.io.Output
import com.esotericsoftware.kryo.io.Input
import com.despegar.offheap.serialization.Serializer
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicInteger
import scala.reflect.ClassTag
import com.despegar.offheap.metrics.Metrics

class OffheapReference[HeapObject](heapObject: HeapObject)(implicit val serializer: Serializer[HeapObject]) extends Metrics {

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
    val timer = metrics.timer("materialize").time()
    val buffer = new Array[Byte](length)
    SoffHeap.get(address,buffer)
    val deserializedObject = serializer.deserialize(buffer)
    timer.stop()
    deserializedObject
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
      free()
    }
  }

  def free() {
    SoffHeap.free(address, length);
  }


  override def toString() = {
    val sb = new StringBuilder()
    sb.append("UnsafeOffHeapMemory");
    sb.append("{address=").append(address);
    sb.append(", length=").append(length);
    sb.append('}');
    sb.toString();
  }

}