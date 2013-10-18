package com.despegar.soffheap.snapshot

trait Snapshot[Key, Value] {

	def get(key: Key): Option[Value]
	
	def multiGet(keys: List[Key]): Map[Key, Option[Value]]
	
	def containsKey(key: Key): Boolean
	
	def size(): Int

}