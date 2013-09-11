package com.despegar.offheap
import org.scalatest._
import org.scalatest.junit.JUnitRunner
import org.junit.runner.RunWith
import java.util.concurrent.Executors

@RunWith(classOf[JUnitRunner])
class OffheapSnapshotTest extends FlatSpec with Matchers {

 
  it should "store object out of the heap" in {
    val offheapSnapshot = new OffheapSnapshot[String, SnapshotValue]();
    offheapSnapshot.put("key1", SnapshotValue("value",1l))
    
    val snapshotValue = offheapSnapshot.get("key1");
		
	snapshotValue.someString should be ("value")
	snapshotValue.someLong.longValue() should be (1l)
  }
  

}