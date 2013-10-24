package com.despegar.soffheap.snapshot

import com.despegar.soffheap.map.SoffHeapMap

class SoffHeapSnapshot[Key, Value](soffHeapMap: SoffHeapMap[Key, Value]) extends Snapshot[Key, Value] {

  def get(key: Key): Option[Value] = soffHeapMap.get(key)
	
  def multiGet(keys: List[Key]): Map[Key, Option[Value]] = soffHeapMap.multiGet(keys)
	
  def containsKey(key: Key): Boolean = soffHeapMap.containsKey(key)
	
  def size(): Int = soffHeapMap.size
  
  def reload(keyValues: Map[Key,Value]) = soffHeapMap.reload(keyValues)
  
  def getName = soffHeapMap.getName
  
}