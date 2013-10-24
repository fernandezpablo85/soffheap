package com.despegar.soffheap.perftest

import com.despegar.soffheap.map.SoffHeapMap
import java.util.ArrayList

class MultiGetReader[Key, Value](soffHeapMap: SoffHeapMap[Key, Value], keyFactory: Unit => Key) extends Runnable {

  override def run() = {
    while (!Thread.interrupted()) {
      try {
        val arrayList = new ArrayList[Key]()

        (1 to 1000) foreach {
          i => arrayList.add(keyFactory())
        }

        val list = soffHeapMap.jmultiGet(arrayList)
        if (!list.isEmpty) {
          list.size
        }
      } catch {
        case e: Throwable => e.printStackTrace()
      }
    }
  }

}