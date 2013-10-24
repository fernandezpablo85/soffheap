package com.despegar.soffheap.perftest

import com.despegar.soffheap.map.SoffHeapMapBuilder
import java.lang.management.ManagementFactory
import com.despegar.soffheap.SoffHeap
import com.despegar.soffheap.SnapshotValue
import scala.collection.mutable.ListBuffer

object LotOfObjects extends App {

  val arrays = System.getProperty("arrays").toInt
  val elements = System.getProperty("elements").toInt

  val snapshot = new SoffHeapMapBuilder[String,Array[SnapshotValue]]().withMaximumHeapElements(10).build()
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
      
      println(snapshot.get(s"key$index").get)
  }

  while (true) {}
}