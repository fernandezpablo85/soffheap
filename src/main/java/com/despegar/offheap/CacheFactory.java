package com.despegar.offheap;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

public class CacheFactory {

	public static <Key,Value> HeapCache<Key, Value> create() {
		Cache<Key, Value> cache = CacheBuilder.newBuilder().maximumSize(10).build();
		return new HeapCache<Key,Value>(cache);
	}
	
}
