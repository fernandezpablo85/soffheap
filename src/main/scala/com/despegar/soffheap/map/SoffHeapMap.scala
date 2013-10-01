package com.despegar.soffheap.map

import java.util.concurrent.atomic.AtomicReference
import scala.collection.Iterable
import com.despegar.soffheap.metrics.{ Metrics }
import com.despegar.soffheap.heapcache.HeapCache
import com.despegar.soffheap.OffheapReference
import com.despegar.soffheap.serialization.Serializer
import scala.collection.JavaConverters._
import scala.collection.concurrent.TrieMap
import java.util.HashMap
import java.util.concurrent.ConcurrentHashMap

class SoffHeapMap[Key, Value](name: String)(implicit heapCache: HeapCache[Key, Value], serializer: Serializer[Value]) extends Metrics {

  private[this] val multiGetTimer = metrics.timer(s"${metricsPrefix}multiGet")
  private[this] val getTimer = metrics.timer(s"${metricsPrefix}get")

  val map: ConcurrentHashMap[Key, OffheapReference[Value]] = new ConcurrentHashMap[Key, OffheapReference[Value]]()

  def put(key: Key, value: Value): Unit = {
    val soffheapRef = asSoffHeapReference(value)
    val oldReference = map.putIfAbsent(key, soffheapRef)
    if (oldReference != null) {
      map.put(key, soffheapRef)
      oldReference.unreference()
      heapCache.invalidate(key)
    }
  }

  private def asSoffHeapReference(value: Value) = {
    new OffheapReference[Value](value)
  }

  def jget(key: Key): Value = {
    get(key).getOrElse(null.asInstanceOf[Value])
  }

  private def innerGet(key: Key): Option[Value] = {
    val cachedValue = heapCache.get(key)
    if (cachedValue != null) return Some(cachedValue)
    while (true) {
      val soffheapRef = map.get(key)
      if (soffheapRef == null) return None
      if (soffheapRef.reference()) {
        try {
          val value = soffheapRef.get()
          heapCache.put(key, value)
          return Some(value)
        }
        finally {
          soffheapRef.unreference()
        }
      }
    }
    None
  }

  def get(key: Key): Option[Value] = getTimer.time {
    innerGet(key)
  }

  def multiGet(keys: List[Key]): Map[Key, Option[Value]] = multiGetTimer.time {
    keys.map { key => (key, innerGet(key)) }.toMap
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
    map.asScala.foreach { entry => entry._2.unreference }
    map.clear()
  }

  private[this] def removeNotIn(iterable: Seq[Key]) = {
    map.asScala.keySet.filterNot(key => iterable.contains(key)).foreach(key => remove(key))
  }

  private[this] def remove(key: Key) = {
    val soffheapRef = map.get(key)
    if (soffheapRef != null) {
      soffheapRef.unreference
      map.remove(key)
    }
  }

  def metricsPrefix: String = name

  override def finalize() = {
    clear()
  }
}