package com.despegar.soffheap.heapcache;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

public class CacheFactory {

	public static <Key,Value> GoogleHeapCache<Key, Value> create(String name, Long maximunSize) {
		Cache<Key, Value> cache = CacheBuilder.newBuilder().maximumSize(maximunSize).build();
		return new GoogleHeapCache<Key,Value>(name, cache);
	}

}
