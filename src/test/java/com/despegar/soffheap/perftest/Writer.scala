package com.despegar.soffheap.perftest

import com.despegar.soffheap.map.SoffHeapMap

class Writer[Key, Value](snapshot: SoffHeapMap[Key, Value], keyFactory: Unit => Key, valueFactory: Unit => Value) extends Runnable {

  override def run() = {
     while(!Thread.interrupted()) {
       try {
    	   snapshot.put(keyFactory(), valueFactory())
       } catch {
         case e: Throwable => e.printStackTrace()
       }
     }
  }
  
}