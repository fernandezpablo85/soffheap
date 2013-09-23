package com.despegar.offheap

import org.junit.runner.RunWith
import org.scalatest._
import org.scalatest.junit.JUnitRunner
import com.despegar.offheap.map.OffheapMapBuilder

@RunWith(classOf[JUnitRunner])
class FreeMemoryTest extends FlatSpec with Matchers {

  
  it should "free memory when replacing references" in {

    val offheapSnapshot = new OffheapMapBuilder[String,SnapshotValue]().withMaximumHeapElements(10).build()

    while(true) {
    	offheapSnapshot.put("key1", SnapshotValue("value", 1l))
    }
  }
}