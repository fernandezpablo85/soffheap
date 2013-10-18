package com.despegar.soffheap.snapshot

import com.despegar.soffheap.map.SoffHeapMapBuilder
import com.despegar.soffheap.SoffHeap
import com.despegar.soffheap.map.SoffHeapMap

class SoffheapSnapshot[Key, Value](soffheapMap: SoffHeapMap[Key, Value]) extends Snapshot[Key, Value] {

  def get(key: Key): Option[Value] = soffheapMap.get(key)
	
  def multiGet(keys: List[Key]): Map[Key, Option[Value]] = soffheapMap.multiGet(keys)
	
  def containsKey(key: Key): Boolean = soffheapMap.containsKey(key)
	
  def size(): Int = soffheapMap.size
  
  def reload(keyValues: Map[Key,Value]) = soffheapMap.reload(keyValues)
  
  def getName() = soffheapMap.getName
  
}