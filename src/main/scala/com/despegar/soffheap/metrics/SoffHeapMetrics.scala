package com.despegar.soffheap.metrics

import com.despegar.soffheap.Heap


trait SoffHeapMetrics extends Heap with Metrics {

  private[this] val MaxSoffHeapMemory = maxSoffHeapMemoryInGB()

  private[this] val allocatedBytes = metrics.counter("allocatedBytes")
  private[this] val allocateTime = metrics.timer("allocateTime")

  private[this] val freedBytes = metrics.counter("freedBytes")
  private[this] val freeTime = metrics.timer("freeTime")

  private[this] val writeBytes = metrics.counter("writeBytes")
  private[this] val putTime = metrics.timer("putTime")

  private[this] val readBytes = metrics.counter("readBytes")
  private[this] val getTime = metrics.timer("getTime")

  private[this] val usedOffMemoryBytes = metrics.counter("usedOffMemoryBytes")
  private[this] val remainingOffMemoryBytes = metrics.gauge("remainingOffMemoryBytes") {calculateRemainingOffMemory()}

  abstract override def allocate(bytes: Long) = {
    allocatedBytes.inc(bytes)
    usedOffMemoryBytes.inc(bytes)
    remainingOffMemoryBytes.value

    allocateTime.time {
      super.allocate(bytes)
    }
  }

  abstract override def free(address: Long, bytes: Int) = {
    freedBytes.inc(bytes)
    usedOffMemoryBytes.dec(bytes)

    freeTime.time {
      super.free(address, bytes)
    }
  }

  abstract override def put(address: Long, buffer: Array[Byte])= {

    putTime.time {
    	writeBytes.inc(buffer.length)
      super.put(address, buffer)
    }
  }

  abstract override def get(address: Long, buffer: Array[Byte])= {
    getTime.time {
    	readBytes.inc(buffer.length)
    	super.get(address, buffer)
    }
  }

  private[this] def calculateRemainingOffMemory():Long = {
    val remainingOffMemory = MaxSoffHeapMemory - usedOffMemoryBytes.count

    if(remainingOffMemory <= 0) throw new OutOfMemoryError(s"Memory limit exceeded -> $MaxSoffHeapMemory")

    remainingOffMemory
  }
}

