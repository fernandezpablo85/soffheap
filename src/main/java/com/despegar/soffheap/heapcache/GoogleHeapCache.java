package com.despegar.soffheap.heapcache;

import com.google.common.cache.Cache;

public class GoogleHeapCache<Key, Value> extends AbstractHeapMap<Key, Value>{

	private final Cache<Key, Value> cache;

	public GoogleHeapCache(Cache<Key, Value> cache) {
		this.cache = cache;
	}

	public Value get(Key key) {
		Value value = cache.getIfPresent(key);
		if (value != null) hitsCounter.inc(); else missesCounter.inc();
		return value;
	}
	
	public void put(Key key, Value value) {
		cache.put(key, value);
	}

    public void invalidate(Key key) {
    	invalidatesCounter.inc();
        cache.invalidate(key);
    }
	
}
