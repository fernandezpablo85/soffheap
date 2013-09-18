package com.despegar.offheap.perftest

import com.despegar.offheap.OffheapMapSnapshot
import com.despegar.offheap.SnapshotValue
import javax.management.MXBean
import java.lang.management.ManagementFactory
import com.despegar.offheap.SoffHeap
import com.despegar.offheap.PojoValue
import java.util.concurrent.atomic.AtomicReference
import com.despegar.offheap.OffheapReference
import com.despegar.offheap.SnapshotValue
import scala.collection.mutable.ListBuffer

object LotOfObjects extends App {

  val arrays = System.getProperty("arrays").toInt
  val elements = System.getProperty("elements").toInt

  val snapshot = new OffheapMapSnapshot[String, Array[SnapshotValue]]
  def nonHeapMemoryUsage = ManagementFactory.getMemoryMXBean().getNonHeapMemoryUsage()
  def neapMemoryUsage = ManagementFactory.getMemoryMXBean().getHeapMemoryUsage()

  val heapBefore = neapMemoryUsage.getUsed()
  val overheadPerObject = 24 + 16

  (1 to arrays) foreach {
    index =>

      val l: ListBuffer[SnapshotValue] = ListBuffer.empty
      
      (1 to elements) foreach { i =>  l += SnapshotValue(s"value$i", i)  }
      
      val elementSize = SoffHeap.sizeOf(classOf[SnapshotValue])
      println(s"el objeto en la heap ocupa ${elements * elementSize / 1024 / 1024} MB")
      
      val obj =   l.toArray

      snapshot.put(s"key$index", obj)
      
      val arrayFromOffheap = snapshot.get(s"key$index").get
  }

  while (true) {}

}