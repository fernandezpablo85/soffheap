package com.despegar.soffheap.perftest

import com.despegar.soffheap.map.SoffHeapMap

class MultiGetReader[Key, Value](snapshot: SoffHeapMap[Key, Value], keyFactory: Unit => Key) extends Runnable {

  override def run() = {
     while(!Thread.interrupted()) { 
       try {
    	val keys = (1 to 1000) map { _ => keyFactory() }
        val option = snapshot.multiGet(keys.toList)
        if (!option.isEmpty) {
           option(0)
        }
       } catch {
         case e: Exception => e.printStackTrace()
       }
     }
  }
  
}