package com.despegar.offheap.metrics

import com.despegar.offheap.Heap

trait SoffHeapMetrics extends Heap with Metrics {

  private[this] val allocatedKB = metrics.counter("allocatedKB")
  private[this] val allocateTime = metrics.timer("allocateTime")

  private[this] val freedKB = metrics.counter("freedKB")
  private[this] val freeTime = metrics.timer("freeTime")

  private[this] val writeBytes = metrics.counter("writeBytes")
  private[this] val putTime = metrics.timer("putTime")

  private[this] val readBytes = metrics.counter("readBytes")
  private[this] val getTime = metrics.timer("getTime")

  abstract override def allocate(bytes: Long) = {
    allocatedKB.inc(toKB(bytes))
    allocateTime.time {
      super.allocate(bytes)
    }
  }

  abstract override def free(address: Long, bytes: Int) = {
    freedKB.inc(toKB(bytes))
    freeTime.time {
      super.free(address, bytes)
    }
  }

  abstract override def put(address: Long, buffer: Array[Byte])= {
    writeBytes.inc(toKB(buffer.length))
    putTime.time {
      super.put(address, buffer)
    }
  }

  abstract override def get(address: Long, buffer: Array[Byte])= {
    readBytes.inc(toKB(buffer.length))
    getTime.time {
      super.get(address, buffer)
    }
  }
  
  val toKB = (bytes: Long) => bytes / 1024
  val toMB =  (bytes: Long) => toKB(bytes) / 1024
}