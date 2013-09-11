package com.despegar.offheap

object LockFreeMain extends LockApp {

  val offheap = new OffheapObject[SnapshotValue](SnapshotValue("value", 1l))
  warmup(offheap)
  val start = System.currentTimeMillis()
  runTest(offheap)
  val end = System.currentTimeMillis()
  println(s"lock free took ${end - start} ms")

}