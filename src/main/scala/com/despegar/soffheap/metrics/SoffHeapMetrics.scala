package com.despegar.soffheap.metrics

import com.despegar.soffheap.Heap

trait SoffHeapMetrics extends Heap with Metrics {

  private[this] val MaxSoffHeapMemory = maxSoffHeapMemoryInGB()

  private[this] val allocatedKB = metrics.counter("allocatedKB")
  private[this] val allocateTime = metrics.timer("allocateTime")

  private[this] val freedKB = metrics.counter("freedKB")
  private[this] val freeTime = metrics.timer("freeTime")

  private[this] val writeBytes = metrics.counter("writeBytes")
  private[this] val putTime = metrics.timer("putTime")

  private[this] val readBytes = metrics.counter("readBytes")
  private[this] val getTime = metrics.timer("getTime")

  private[this] val usedOffMemoryKB = metrics.counter("usedOffMemoryKB")
  private[this] val remainingOffMemoryKB = metrics.gauge("remainingOffMemoryKB") {calculateRemainingOffMemory()}

  abstract override def allocate(bytes: Long) = {
    allocatedKB.inc(toKB(bytes))
    usedOffMemoryKB.inc(toKB(bytes))
    remainingOffMemoryKB.value

    allocateTime.time {
      super.allocate(bytes)
    }
  }

  abstract override def free(address: Long, bytes: Int) = {
    freedKB.inc(toKB(bytes))
    usedOffMemoryKB.dec(toKB(bytes))

    freeTime.time {
      super.free(address, bytes)
    }
  }

  abstract override def put(address: Long, buffer: Array[Byte])= {

    putTime.time {
    	writeBytes.inc(toKB(buffer.length))
      super.put(address, buffer)
    }
  }

  abstract override def get(address: Long, buffer: Array[Byte])= {
//    getTime.time {
//    	readBytes.inc(toKB(buffer.length))
      unsafe.copyMemory(null, address, buffer, ByteArrayOffset, buffer.length)
//    }
  }

  private[this] def calculateRemainingOffMemory():Long = {
    val remainingOffMemory = MaxSoffHeapMemory - usedOffMemoryKB.count

    if(remainingOffMemory <= 0) throw new OutOfMemoryError(s"Memory limit exceeded -> $MaxSoffHeapMemory")

    remainingOffMemory
  }
}

