package com.despegar.offheap;

import com.despegar.offheap.map.OffheapMapBuilder;
import com.despegar.offheap.map.TypedBuilder;

public class SnapshotBuilderFactory {

	public static <Key,Value> OffheapMapBuilder<Key,Value> createBuilder(Class<Value> type) {
		OffheapMapBuilder<Key, Value> builder = new TypedBuilder<Key, Value>().createBuilder(type);
		return builder;

	}
	
}
