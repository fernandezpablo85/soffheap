package com.despegar.soffheap.map

import scala.collection.mutable.Map
import java.util.concurrent.atomic.AtomicReference
import com.despegar.soffheap.serialization.kryo.KryoSerializer
import scala.collection.JavaConversions._
import scala.collection.Iterable
import java.util.concurrent.ConcurrentHashMap
import com.despegar.soffheap.metrics.Metrics
import com.despegar.soffheap.heapcache.HeapCache
import com.despegar.soffheap.OffheapReference
import scala.reflect.ClassTag
import java.util.concurrent.Executors
import scala.collection.parallel.ThreadPoolTaskSupport
import java.util.concurrent.ThreadPoolExecutor
import com.despegar.soffheap.serialization.fst.FSTSerializer

class SoffHeapMap[Key, Value: ClassTag]( implicit cache: HeapCache[Key, Value]) extends Metrics {

  private[this] val multiGetTimer = metrics.timer("multiGet")
  private[this] val cacheHits = metrics.counter("cacheHits")
  private[this] val cacheMisses = metrics.counter("cacheMisses")
  val snapshot: Map[Key, AtomicReference[OffheapReference[Value]]] = new ConcurrentHashMap[Key, AtomicReference[OffheapReference[Value]]]()
  implicit val serializer = new FSTSerializer[Value]
  //  val taskSupport = new ThreadPoolTaskSupport()

  def put(key: Key, value: Value): Unit = {
    if (snapshot.contains(key)) {
      val option = snapshot.get(key)
      if (option.isDefined) {
        val oldReference = option.get.getAndSet(new OffheapReference(value))
        oldReference.unreference()
        cache.invalidate(key)
      }
    } else {
      snapshot.put(key, new AtomicReference[OffheapReference[Value]](new OffheapReference[Value](value)))
    }
  }
  
  def containsKey(key: Key) = {
    snapshot.contains(key)
  }

  def size() = {
    snapshot.size
  }

  def clear() = {
    snapshot.foreach { entry => entry._2.get().unreference }
    snapshot.clear()
  }

  def jget(key: Key): Value = {
    get(key).getOrElse(null.asInstanceOf[Value])
  }

  def get(key: Key): Option[Value] = {
//    val cachedValue = cache.get(key)
//    if (cachedValue != null) {
//      cacheHits.inc(1)
//      return Some(cachedValue)
//    }
//    cacheMisses.inc(1)
    while (true) {
      val option = snapshot.get(key)
      if (!option.isDefined) return None
      val offheapReference = option.get.get()
      if (offheapReference.reference()) {
        try {
          val value = offheapReference.get()
          cache.put(key, value)
          return Some(value)
        } catch { case e: Throwable => e.printStackTrace() }
        finally {
          offheapReference.unreference()
        }

      }
    }
    None
  }

  def multiGet(keys: List[Key]): Seq[Value] = multiGetTimer.time {
    //    val parallelKeys = keys.par
    //    val split = keys.splitAt(keys.size / 2)
    //    val splitZipped = split.
    //    parallelKeys.tasksupport = taskSupport
    keys.map { key => get(key) }.flatMap { option => option }
  }
  
   def jmultiGet(keys: java.util.List[Key]): java.util.List[Value] = multiGetTimer.time {
    //    val parallelKeys = keys.par
    //    val split = keys.splitAt(keys.size / 2)
    //    val splitZipped = split.
    //    parallelKeys.tasksupport = taskSupport
    keys.map { key => get(key) }.flatMap { option => option }
  }

  def reload(map: Map[Key, Value]) = {
    removeNotIn(map.keys)
    map.foreach(tuple => put(tuple._1, tuple._2))
  }

  def reload(iterable: Iterable[(Key, Value)]) = {
    val insertedKeys = iterable.map {
      tuple =>
        put(tuple._1, tuple._2)
        tuple._1
    }
    removeNotIn(insertedKeys)
  }

  private[this] def removeNotIn(iterable: Iterable[Key]) = {
    snapshot.keySet.filterNot(key => iterable.contains(key)).foreach(key => remove(key))
  }

  private[this] def remove(key: Key) = {
    val option = snapshot.get(key)
    if (option.isDefined) {
      val atomicReference = option.get
      atomicReference.get().unreference
      snapshot.remove(key)
    }
  }

}