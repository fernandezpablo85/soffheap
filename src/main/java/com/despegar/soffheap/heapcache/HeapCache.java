package com.despegar.soffheap.heapcache;


public interface HeapCache<Key, Value> {

	Value get(Key key);
	
	void put(Key key, Value value);

	void invalidate(Key key);

}
