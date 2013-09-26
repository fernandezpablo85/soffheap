package com.despegar.soffheap.map.j;


@SuppressWarnings("rawtypes")
public class SoffHeapMapBuilder<Key, Value> extends com.despegar.soffheap.map.SoffHeapMapBuilder<Key, Value> {
	
	public static SoffHeapMapBuilder newBuilder() {
		return new SoffHeapMapBuilder();
	}

	public <Key, Value> SoffHeapMap<Key, Value> buildj() {
		com.despegar.soffheap.map.SoffHeapMap<Key, Value> map = (com.despegar.soffheap.map.SoffHeapMap<Key, Value>) super.build();
		return new SoffHeapMapImpl<Key, Value>(map);
	}
}
