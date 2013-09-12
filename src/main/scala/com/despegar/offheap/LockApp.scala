package com.despegar.offheap

import java.util.concurrent.Executors


class LockApp extends App {
     def warmup(offheap: OffheapReference[SnapshotValue]) = {
     for(_<- 1 to 10000) {
        offheap.get
    }
  }
    
     def runTest(offheap: OffheapReference[SnapshotValue]) = {
     val executor = Executors.newFixedThreadPool(4)
    for(_<- 1 to 4) {
          for(_<- 1 to 1000000) {
       executor.submit(new Runnable(){
         override def run() {
        	 offheap.get
         }
       })
    }
    }
     executor.shutdownNow()

  }
   
}