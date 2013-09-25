package com.despegar.soffheap.map.j;

import java.util.List;

import com.despegar.soffheap.map.SoffHeapMap;

public class SoffHeapMapImpl<Key, Value> implements com.despegar.soffheap.map.j.SoffHeapMap<Key, Value> {

	private final SoffHeapMap<Key, Value> offheapMapSnapshot;

	public SoffHeapMapImpl(SoffHeapMap<Key, Value> offheapMapSnapshot) {
		this.offheapMapSnapshot = offheapMapSnapshot;
	}

	@Override
	public Value get(Key key) {
		return offheapMapSnapshot.jget(key);
	}

	@Override
	public List<Value> multiGet(List<Key> keys) {
		return offheapMapSnapshot.jmultiGet(keys);
	}

	@Override
	public void put(Key key, Value value) {
		offheapMapSnapshot.put(key, value);
	}

	@Override
	public int size() {
		return offheapMapSnapshot.size();
	}

	@Override
	public boolean containsKey(Key key) {
		return offheapMapSnapshot.containsKey(key);
	}


}
