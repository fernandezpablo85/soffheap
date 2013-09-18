package com.despegar.offheap.metrics

import com.despegar.offheap.SoffHeapt

trait SoffHeapWithMetrics extends SoffHeapt with Metrics {
  
  abstract override def allocate(bytes: Long) = {
    metrics.counter("allocatedKB").inc(toKB(bytes))
    val timer = metrics.timer("allocateTime").time()
    val address = super.allocate(bytes)
    timer.stop()
    address
  } 

  abstract override def free(address: Long, bytes: Int)= {
    metrics.counter("freedKB").inc(toKB(bytes))
    val timer = metrics.timer("freeTime").time()
    super.free(address, bytes)
    timer.stop()
  } 

  abstract override def put(address: Long, buffer: Array[Byte])= {
    metrics.counter("writeBytes").inc(toKB(buffer.length))
    val timer = metrics.timer("putTime").time()
    super.put(address, buffer)
    timer.stop()
  } 

  abstract override def get(address: Long, buffer: Array[Byte])= {
    metrics.counter("readBytes").inc(toKB(buffer.length))
    val timer = metrics.timer("getTime").time()
    super.get(address, buffer)
    timer.stop()
  }
  
  private def toKB(bytes: Long) = {
    bytes / 1024
  }
  
  private def toMB(bytes: Long) = {
    toKB(bytes) / 1024
  }


}