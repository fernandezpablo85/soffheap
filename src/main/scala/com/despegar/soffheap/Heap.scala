package com.despegar.soffheap

import scala.concurrent.util.Unsafe
import java.util.concurrent.atomic.AtomicLong

trait Heap {

  val unsafe = Unsafe.instance
  val ByteArrayOffset = unsafe.arrayBaseOffset(classOf[Array[Byte]])
  val allocatedBytes = new AtomicLong(0)

  def allocate(bytes: Long):Long

  def free(address: Long, bytes: Int)

  def put(address: Long, buffer: Array[Byte])

  def get(address: Long, buffer: Array[Byte])
}