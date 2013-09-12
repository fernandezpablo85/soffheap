package com.despegar.offheap

import org.junit.runner.RunWith
import org.scalatest._
import org.scalatest.junit.JUnitRunner
import java.util.concurrent.atomic.AtomicReference

@RunWith(classOf[JUnitRunner])
class FreeMemoryTest extends FlatSpec with Matchers {

  
  it should "free memory when replacing references" in {
    val offheapSnapshot = new OffheapMapSnapshot[String, SnapshotValue]();
    
    while(true) {
    	offheapSnapshot.put("key1", SnapshotValue("value", 1l))
    }
    
  }
  
}