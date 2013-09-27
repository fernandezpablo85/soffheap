package com.despegar.soffheap

import java.util.HashSet
import java.lang.reflect.Field
import java.lang.reflect.Modifier
import com.despegar.soffheap.metrics.SoffHeapMetrics

trait SoffHeap extends Heap {

  def allocate(bytes: Long):Long = {
    unsafe.allocateMemory(bytes)
  }

  def free(address: Long, bytes: Int) = {
    unsafe.freeMemory(address)
  }

  def put(address: Long, buffer: Array[Byte]) = {
    unsafe.copyMemory(buffer, ByteArrayOffset, null, address, buffer.length)
  }

  def get(address: Long, buffer: Array[Byte]) = {
    unsafe.copyMemory(null, address, buffer, ByteArrayOffset, buffer.length)
  }
}

object SoffHeap extends SoffHeap with SoffHeapMetrics {

  def sizeOf(c: Class[_]): Long = {
    val fields = new HashSet[Field]()
    var theClass = c
    while (theClass != classOf[Object]) {
      for (f <- theClass.getDeclaredFields()) {
        if ((f.getModifiers() & Modifier.STATIC) == 0) {
          fields.add(f)
        }
      }
      theClass = c.getSuperclass()
    }

    // get offset
    var maxSize: Long = 0
    for (f <- fields.toArray()) {
      val offset = unsafe.objectFieldOffset(f.asInstanceOf[Field])
      if (offset > maxSize) {
        maxSize = offset
      }
    }

    ((maxSize / 8) + 1) * 8 // padding
  }

  def sizeOf(o: Any): Long = {
    sizeOf(o.getClass())
  }
}