package com.despegar.soffheap;

import com.despegar.soffheap.map.SoffHeapMapBuilder;
import com.despegar.soffheap.map.TypedBuilder;

public class SnapshotBuilderFactory {

	public static <Key,Value> SoffHeapMapBuilder<Key,Value> createBuilder(Class<Value> type) {
		SoffHeapMapBuilder<Key, Value> builder = new TypedBuilder<Key, Value>().createBuilder(type);
		return builder;

	}
	
}
