package com.despegar.offheap.perftest

import com.despegar.offheap.map.OffheapMapSnapshot

class Reader[Key, Value](snapshot: OffheapMapSnapshot[Key, Value], keyFactory: Unit => Key) extends Runnable {

  override def run() = {
     while(!Thread.interrupted()) {
        val option = snapshot.get(keyFactory())
        if (option.isDefined) {
           option.get
        }
     }
  }
  
}