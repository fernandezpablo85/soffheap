package com.despegar.offheap.map

import scala.collection.mutable.Map
import java.util.concurrent.atomic.AtomicReference
import com.despegar.offheap.serialization.KryoSerializer
import scala.collection.JavaConversions._
import scala.collection.Iterable
import java.util.concurrent.ConcurrentHashMap
import scala.reflect.ClassTag
import com.despegar.offheap.metrics.Metrics
import com.despegar.offheap.CacheFactory
import com.despegar.offheap.HeapCache
import com.despegar.offheap.OffheapReference

class OffheapMapSnapshot[Key, Value: ClassTag] extends Metrics {

  val cache: HeapCache[Key, Value] = CacheFactory.create()
  val snapshot: Map[Key, AtomicReference[OffheapReference[Value]]] = new ConcurrentHashMap[Key, AtomicReference[OffheapReference[Value]]]()
  implicit val serializer = new KryoSerializer[Value]

  def put(key: Key, value: Value): Unit = {
    if (snapshot.contains(key)) {
      val option = snapshot.get(key)
      if (option.isDefined) {
        val oldReference = option.get.getAndSet(new OffheapReference(value))
        oldReference.unreference()
      }
    } else {
      snapshot.put(key, new AtomicReference[OffheapReference[Value]](new OffheapReference[Value](value)))
    }
  }

  def size() = {
    snapshot.size
  }

  def javaGet(key: Key): Value = {
    get(key).getOrElse(null.asInstanceOf[Value])
  }

  def get(key: Key): Option[Value] = metrics.timer("get").time {
    val cachedValue = cache.get(key)
    if (cachedValue != null) return Some(cachedValue)
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