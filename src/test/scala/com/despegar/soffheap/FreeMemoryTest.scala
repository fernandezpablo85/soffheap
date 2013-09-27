package com.despegar.soffheap

import org.junit.runner.RunWith
import org.scalatest._
import org.scalatest.junit.JUnitRunner
import com.despegar.soffheap.map.SoffHeapMapBuilder
import com.despegar.soffheap.SnapshotValue

@RunWith(classOf[JUnitRunner])
class FreeMemoryTest extends FlatSpec with Matchers {

  
  it should "free memory when replacing references" in {

    val offheapSnapshot = new SoffHeapMapBuilder[String,SnapshotValue]().withMaximumHeapElements(10).build()

    while(true) {
    	offheapSnapshot.put("key1", SnapshotValue("value", 1l))
    }
  }
}