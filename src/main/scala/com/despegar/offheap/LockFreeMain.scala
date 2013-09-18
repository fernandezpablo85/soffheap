package com.despegar.offheap

import com.despegar.offheap.serialization.KryoSerializer
import java.util.concurrent.atomic.AtomicReference
import com.despegar.offheap.SnapshotValue

object LockFreeMain extends LockApp {

  implicit val serializer = new KryoSerializer[SnapshotValue]

  val offheap = new OffheapReference[SnapshotValue](SnapshotValue("value", 1l))
  warmup(offheap)
  val start = System.currentTimeMillis()
  runTest(offheap)
  val end = System.currentTimeMillis()
  println(s"lock free took ${end - start} ms")

}