package com.despegar.soffheap.map.j;

import java.util.List;
import java.util.Map;


public interface SoffHeapMap<Key, Value> {

	Value get(Key key);
	
	Map<Key, Value> multiGet(List<Key> keys);
	
	void put(Key key, Value value);
	
	int size();
	 
	boolean containsKey(Key key);
	
}
