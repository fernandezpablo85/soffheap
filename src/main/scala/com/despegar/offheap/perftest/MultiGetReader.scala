package com.despegar.offheap.perftest

import com.despegar.offheap.map.OffheapMapSnapshot

class MultiGetReader[Key, Value](snapshot: OffheapMapSnapshot[Key, Value], keyFactory: Unit => Key) extends Runnable {

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