package com.despegar.offheap;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

public class CacheFactory {

	public static <Key,Value> HeapCache<Key, Value> create(Long maximunSize) {
		Cache<Key, Value> cache = CacheBuilder.newBuilder().maximumSize(maximunSize).build();
		return new HeapCache<Key,Value>(cache);
	}

}
