package com.despegar.soffheap
import org.scalatest._
import org.scalatest.junit.JUnitRunner
import org.junit.runner.RunWith
import scala.collection.mutable.Map
import scala.collection.mutable.ListBuffer
import scala.reflect._
import com.despegar.soffheap.map.SoffHeapMapBuilder
import com.despegar.soffheap.snapshot.SoffHeapSnapshot
import com.despegar.soffheap.snapshot.SnapshotBuilder


@RunWith(classOf[JUnitRunner])
class SoffHeapMapTest extends FlatSpec with Matchers {

  it should "store object out of the heap" in {

    val offheapSnapshot = SoffHeapMapBuilder.of[String,SnapshotValue]().withMaximumHeapElements(10).build()
    offheapSnapshot.put("key1", SnapshotValue("value", 1l))

    val snapshotValue = offheapSnapshot.get("key1").get

    snapshotValue.someString should be("value")
    snapshotValue.someLong.longValue() should be(1l)
  }

  it should "store array out of the heap" in {
    val offheapSnapshot = SoffHeapMapBuilder.of[String,Array[SnapshotValue]]().withMaximumHeapElements(10).build()

    val listBuffer: ListBuffer[SnapshotValue] = ListBuffer.empty

    val classTagOfArray = classTag[Array[SnapshotValue]].runtimeClass
    
    val elements = 1000
    (1 to elements) foreach { i => listBuffer += SnapshotValue(s"value$i", i) }

    val array = listBuffer.toArray
    
    offheapSnapshot.put("key1", array)

    val arrayFromOffheap = offheapSnapshot.get("key1").get

    arrayFromOffheap.length should be (elements)
  }

  it should "replace references" in {

    val offheapSnapshot = SoffHeapMapBuilder.of[String,SnapshotValue]().withMaximumHeapElements(10).build()
    offheapSnapshot.put("key1", SnapshotValue("value", 1l))

    val snapshotValue1 = offheapSnapshot.get("key1").get

    offheapSnapshot.put("key1", SnapshotValue("valuee", 2l))

    val snapshotValue2 = offheapSnapshot.get("key1").get

    snapshotValue1 should not be snapshotValue2
  }

  it should "full reload a snapshot" in {
    val offheapSnapshot = SoffHeapMapBuilder.of[String,SnapshotValue]().withMaximumHeapElements(10).build()
    offheapSnapshot.put("keyToBeUpdated", SnapshotValue("value1", 1l))
    offheapSnapshot.put("keyToBeRemoved", SnapshotValue("value2", 2l))

    offheapSnapshot.reload(Map("keyToBeUpdated" -> SnapshotValue("value1modified", 1l), "keyToBeInserted" -> SnapshotValue("value3", 3l)))

    offheapSnapshot.size should be(2)
    offheapSnapshot.get("keyToBeRemoved") should be(None)
    offheapSnapshot.get("keyToBeInserted") should be(Some(SnapshotValue("value3", 3l)))
    offheapSnapshot.get("keyToBeUpdated") should be(Some(SnapshotValue("value1modified", 1l)))
  }

  it should "incrementally reload a snapshot" in {
    val offheapSnapshot = SoffHeapMapBuilder.of[String,SnapshotValue]().withMaximumHeapElements(10).build()
    offheapSnapshot.put("keyToBeUpdated", SnapshotValue("value1", 1l))
    offheapSnapshot.put("keyToBeRemoved", SnapshotValue("value2", 2l))

    offheapSnapshot.reload(Seq(("keyToBeUpdated", SnapshotValue("value1modified", 1l)), ("keyToBeInserted", SnapshotValue("value3", 3l))))

    offheapSnapshot.size should be(2)
    offheapSnapshot.get("keyToBeRemoved") should be(None)
    offheapSnapshot.get("keyToBeInserted") should be(Some(SnapshotValue("value3", 3l)))
    offheapSnapshot.get("keyToBeUpdated") should be(Some(SnapshotValue("value1modified", 1l)))
  }

}