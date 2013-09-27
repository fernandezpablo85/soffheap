package com.despegar.soffheap.map

import java.util.concurrent.atomic.AtomicReference
import com.despegar.soffheap.serialization.kryo.KryoSerializer
import scala.collection.Iterable
import java.util.concurrent.ConcurrentHashMap
import scala.collection.JavaConverters._
import com.despegar.soffheap.metrics.Metrics
import com.despegar.soffheap.heapcache.HeapCache
import com.despegar.soffheap.OffheapReference
import scala.reflect.ClassTag
import java.util.concurrent.Executors
import scala.collection.parallel.ThreadPoolTaskSupport
import java.util.concurrent.ThreadPoolExecutor
import com.despegar.soffheap.serialization.fst.FSTSerializer
import com.despegar.soffheap.serialization.Serializer
import java.util.ArrayList
import scala.collection.concurrent.TrieMap
import java.util.HashMap

class SoffHeapMap[Key, Value](implicit heapCache: HeapCache[Key, Value], serializer: Serializer[Value]) extends Metrics {

  private[this] val multiGetTimer = metrics.timer("multiGet")
  private[this] val getTimer = metrics.timer("get")
  val map: TrieMap[Key, AtomicReference[OffheapReference[Value]]] = TrieMap[Key, AtomicReference[OffheapReference[Value]]]()

  def put(key: Key, value: Value): Unit = {
    if (containsKey(key)) {
      val atomicReferenceOption = map.get(key)
      val oldReference = atomicReferenceOption.get.getAndSet(new OffheapReference[Value](value))
      oldReference.unreference()
      heapCache.invalidate(key)
    } else {
      map.put(key, new AtomicReference[OffheapReference[Value]](new OffheapReference[Value](value)))
    }
  }

  def jget(key: Key): Value = {
    get(key).getOrElse(null.asInstanceOf[Value])
  }

  private def innerGet(key: Key): Option[Value] = {
    val cachedValue = heapCache.get(key)
    if (cachedValue != null) return Some(cachedValue)
    while (true) {
      val atomicReferenceOption = map.get(key)
      if (!atomicReferenceOption.isDefined) return None
      val offheapReference = atomicReferenceOption.get.get()
      if (offheapReference.reference()) {
        try {
          val value = offheapReference.get()
          heapCache.put(key, value)
          return Some(value)
        } catch { case e: Throwable => e.printStackTrace() }
        finally {
          offheapReference.unreference()
        }
      }
    }
    None
  }

  def get(key: Key): Option[Value] = getTimer.time {
    innerGet(key)
  }

  def multiGet(keys: List[Key]): Map[Key, Option[Value]] = multiGetTimer.time {
    keys.map { key => (key,innerGet(key)) }.toMap
  }

  def jmultiGet(keys: java.util.List[Key]): java.util.Map[Key, Value] = multiGetTimer.time {
    val result: java.util.Map[Key, Value] = new HashMap[Key, Value]()
    val iterator = keys.iterator()
    while (iterator.hasNext()) {
      val key = iterator.next()
      val option = innerGet(key)
      result.put(key, option.getOrElse(null.asInstanceOf[Value]))
    }
    result
  }

  def reload(map: Map[Key, Value]) = {
    removeNotIn(map.keys.toSeq)
    map.foreach(tuple => put(tuple._1, tuple._2))
  }

  def reload(iterable: Iterable[(Key, Value)]) = {
    val insertedKeys = iterable.map {
      tuple =>
        put(tuple._1, tuple._2)
        tuple._1
    }
    removeNotIn(insertedKeys.toSeq)
  }

  def containsKey(key: Key) = {
    map.contains(key)
  }

  def size() = {
    map.size
  }

  def clear() = {
    map.foreach { entry => entry._2.get().unreference }
    map.clear()
  }

  private[this] def removeNotIn(iterable: Seq[Key]) = {
    map.keySet.filterNot(key => iterable.contains(key)).foreach(key => remove(key))
  }

  private[this] def remove(key: Key) = {
    val atomicReferenceOption = map.get(key)
    if (atomicReferenceOption.isDefined) {
      atomicReferenceOption.get.get().unreference
      map.remove(key)
    }
  }

}