package com.despegar.soffheap.perftest

import com.despegar.soffheap.map.SoffHeapMap

class MultiGetReader[Key, Value](soffheapMap: SoffHeapMap[Key, Value], keyFactory: Unit => Key) extends Runnable {

  override def run() = {
     while(!Thread.interrupted()) { 
       try {
    	val keys = (1 to 1000) map { _ => keyFactory() }
        val list = soffheapMap.multiGet(keys.toList)
        if (!list.isEmpty) {
           list.size
        }
       } catch {
         case e: Throwable => e.printStackTrace()
       }
     }
  }
  
}