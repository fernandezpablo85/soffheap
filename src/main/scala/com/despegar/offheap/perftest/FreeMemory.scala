package com.despegar.offheap.perftest

import com.despegar.offheap.OffheapMapSnapshot
import com.despegar.offheap.SnapshotValue
import scala.collection.mutable.ListBuffer
import com.despegar.offheap.SoffHeap

object FreeMemory extends App {

  val snapshot = new OffheapMapSnapshot[String, Array[SnapshotValue]]

  val arrays = System.getProperty("arrays").toInt
  val elements = System.getProperty("elements").toInt

  (1 to arrays) foreach {
    index =>

      val array: ListBuffer[SnapshotValue] = ListBuffer.empty

      (1 to elements) foreach { i => array += SnapshotValue(s"value$i", i) }
      val elementSize = SoffHeap.sizeOf(classOf[SnapshotValue])

      val obj = array.toArray

      snapshot.put("alwaysSameKey", obj)
      
  }
  
  while (true) {}

}