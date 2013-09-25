package com.despegar.soffheap.heapcache;

import com.google.common.cache.Cache;

public class HeapCache<Key, Value> {

	private final Cache<Key, Value> cache;

	public HeapCache(Cache<Key, Value> cache) {
		this.cache = cache;
	}

	public Value get(Key key) {
		return cache.getIfPresent(key);
	}
	
	public void put(Key key, Value value) {
		cache.put(key, value);
	}

    public void invalidate(Key key) {
        cache.invalidate(key);
    }
	
}
