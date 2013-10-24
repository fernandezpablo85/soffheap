package com.despegar.soffheap.map

import com.despegar.soffheap.heapcache.{CacheFactory, HeapCache, NoHeapCache}
import scala.collection.mutable.ListBuffer
import com.despegar.soffheap.map.j.SoffHeapMapImpl
import com.despegar.soffheap.serialization.Serializer
import com.despegar.soffheap.serialization.kryo.KryoSerializerFactory
import com.despegar.soffheap.serialization.SerializerFactory
import com.despegar.soffheap.serialization.fst.FSTSerializerFactory
import java.util.UUID
import com.despegar.soffheap.snapshot.TunneableSoffheapMapBuilder

class SoffHeapMapBuilder[K, V] extends TunneableSoffheapMapBuilder[SoffHeapMapBuilder[K, V], K, V]{

  def build() = {
	  innerBuildSoffHeapMap
  }
  

  def buildJ() = {
     innerBuildSoffHeapMapJ()
  }
  
  override def self(): SoffHeapMapBuilder[K, V] = {
    this
  }
 
}

object SoffHeapMapBuilder {
  def of[Key, Value]() = {
    new SoffHeapMapBuilder[Key, Value]()
  }
  
}