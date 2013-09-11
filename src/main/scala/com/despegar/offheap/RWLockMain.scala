package com.despegar.offheap

object RWLockMain extends LockApp {

  val offheap = new ReadWriteLockOffheapObject[SnapshotValue](SnapshotValue("value", 1l))
  warmup(offheap)
  val start = System.currentTimeMillis()
  runTest(offheap)
  val end = System.currentTimeMillis()
  println(s"rw lock took ${end - start} ms")

}