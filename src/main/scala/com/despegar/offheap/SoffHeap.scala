package com.despegar.offheap

import java.util.concurrent.atomic.AtomicLong
import scala.concurrent.util.Unsafe
import java.util.HashSet
import java.lang.reflect.Field
import java.lang.reflect.Modifier

class SoftHeap extends SoffHeapt {
  val UNSAFE = Unsafe.instance
  val BYTE_ARRAY_OFFSET = UNSAFE.arrayBaseOffset(classOf[Array[Byte]])
  val allocatedBytes = new AtomicLong(0)
  
  
  def allocate(bytes: Long) = {
    allocatedBytes.addAndGet(bytes);
    UNSAFE.allocateMemory(bytes);
  }

  def free(address: Long, bytes: Int) = {
    UNSAFE.freeMemory(address);
  }

  def put(address: Long, buffer: Array[Byte]) = {
    //        assert !disposed.get() : "disposed";
    //        assert offset >= 0 : offset;
    //        assert null != buffer;
    //        assert offset <= length - buffer.length : offset;
    //        assert buffer.length <= length : buffer.length;
    UNSAFE.copyMemory(buffer, BYTE_ARRAY_OFFSET, null, address, buffer length);
  }

  def get(address: Long, buffer: Array[Byte]) = {
    //        assert !disposed.get() : "disposed";
    //        assert offset >= 0 : offset;
    //        assert null != buffer;
    //        assert offset <= length - buffer.length : offset;
    //        assert buffer.length <= length : buffer.length;
    UNSAFE.copyMemory(null, address, buffer, BYTE_ARRAY_OFFSET, buffer length);
  }

}

object SoffHeap {

  val UNSAFE = Unsafe.instance
 
  val instance = new SoftHeap with SoffHeapWithMetrics
  
  def allocate(bytes: Long) = {
   instance.allocate(bytes)
  }

  def free(address: Long, bytes: Int) = {
    instance.free(address, bytes)
  }

  def put(address: Long, buffer: Array[Byte]) = {
    instance.put(address, buffer)
  }

  def get(address: Long, buffer: Array[Byte]) = {
    instance.get(address, buffer)
  }

  def sizeOf(c: Class[_]): Long = {
    val fields = new HashSet[Field]()
    var theClass = c
    while (theClass != classOf[Object]) {
      for (f <- theClass.getDeclaredFields()) {
        if ((f.getModifiers() & Modifier.STATIC) == 0) {
          fields.add(f);
        }
      }
      theClass = c.getSuperclass();
    }

    // get offset
    var maxSize: Long = 0;
    for (f <- fields.toArray()) {
      val offset = UNSAFE.objectFieldOffset(f.asInstanceOf[Field])
      if (offset > maxSize) {
        maxSize = offset;
      }
    }

    ((maxSize / 8) + 1) * 8 // padding
  }

  def sizeOf(o: Any): Long = {
    sizeOf(o.getClass())
  }

}