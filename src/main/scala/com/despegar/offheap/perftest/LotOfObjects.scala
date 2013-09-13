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

  val objectsCount = System.getProperty("objectsCount").toInt

  val snapshot = new OffheapMapSnapshot[String, Array[SnapshotValue]]
  def nonHeapMemoryUsage = ManagementFactory.getMemoryMXBean().getNonHeapMemoryUsage()
  def neapMemoryUsage = ManagementFactory.getMemoryMXBean().getHeapMemoryUsage()

  //  val snapshotSize = SoffHeap.sizeOf(classOf[OffheapMapSnapshot[_, _]])
  //  val atomicReferenceSize = SoffHeap.sizeOf(classOf[AtomicReference[_]])
  //  val offheapReferenceSize = SoffHeap.sizeOf(classOf[OffheapReference[_]])
  //  
  //  val size = snapshotSize + objectsCount*(atomicReferenceSize + offheapReferenceSize)

  //  println(s"Heap size >= $size")

  val heapBefore = neapMemoryUsage.getUsed()
  val overheadPerObject = 24 + 16

  (1 to objectsCount) foreach {
    index =>

      val l: ListBuffer[SnapshotValue] = ListBuffer.empty
      
      val elements = 1000
      (1 to elements) foreach { i =>  l += SnapshotValue(s"value$i", i)  }
      val elementSize = SoffHeap.sizeOf(classOf[SnapshotValue])
      
      val obj =   l.toArray
      if (index % 100000 == 0) {
//        val allocatedBytes = SoffHeap.allocatedBytes.get() / 1024 / 1024
        println(s"storing object $index of size ${SoffHeap.sizeOf(obj)}, allocatedMB=$allocatedBytes, $nonHeapMemoryUsage")
        val heapAveragePerObject = (neapMemoryUsage.getUsed() - heapBefore) / index
        println(s"heap average per object = $heapAveragePerObject")
      }
      snapshot.put(s"key$index", obj)
  }

  while (true) {}

}