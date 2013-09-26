package com.despegar.soffheap.heapcache;

public class NoHeapCache<Key, Value> extends AbstractHeapMap<Key, Value>{

	@Override
	public Value get(Key key) {
		return null;
	}

	@Override
	public void put(Key key, Value value) {
	}

	@Override
	public void invalidate(Key key) {
	}

}
