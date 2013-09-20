package com.despegar.offheap.perftest

import com.despegar.offheap.map.OffheapMapSnapshot

class Writer[Key, Value](snapshot: OffheapMapSnapshot[Key, Value], keyFactory: Unit => Key, valueFactory: Unit => Value) extends Runnable {

  override def run() = {
     while(!Thread.interrupted()) {
        snapshot.put(keyFactory(), valueFactory())
     }
  }
  
}