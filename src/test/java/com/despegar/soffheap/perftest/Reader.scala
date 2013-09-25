package com.despegar.soffheap.perftest

import com.despegar.soffheap.map.SoffHeapMap

class Reader[Key, Value](snapshot: SoffHeapMap[Key, Value], keyFactory: Unit => Key) extends Runnable {

  override def run() = {
     while(!Thread.interrupted()) {
        val option = snapshot.get(keyFactory())
        if (option.isDefined) {
           option.get
        }
     }
  }
  
}