package com.despegar.offheap

import scala.collection.mutable.Map

class OffheapSnapshot[Key, Value <: Serializable] {

  val snapshot: Map[Key, OffheapObject[Value]] = Map.empty
  
  def put(key: Key, value: Value) = {
     if (snapshot.contains(key)) {
    	 val offheapObject = snapshot.get(key)
    	  if (offheapObject.isDefined)  {
    	    offheapObject.get.swap(value)
    	  }
     }
     else {
    	 snapshot.put(key, new OffheapObject[Value](value))
     }
  }
  
  def get(key: Key) = {
    val offheapObject = snapshot.get(key)
    offheapObject.get.get
  }
  
  
}