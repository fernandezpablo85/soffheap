package com.despegar.soffheap.map.j;

import com.despegar.soffheap.map.TypedBuilder;

@SuppressWarnings("rawtypes")
public class SoffHeapMapBuilder  {
	
	private Class valueClass;
	private int maxHeapElements;
	
	public static SoffHeapMapBuilder newBuilder() {
		return new SoffHeapMapBuilder();
	}

	public SoffHeapMapBuilder withKeyClass(Class keyClass) {
		return this;
	}
	
	public SoffHeapMapBuilder withValueClass(Class valueClass) {
		this.valueClass = valueClass;
		return this;
	}
	
	public SoffHeapMapBuilder withMaxHeapElements(int maxHeapElements) {
		this.maxHeapElements = maxHeapElements;
		return this;
	}
	
	public <Key, Value> SoffHeapMap<Key, Value> build() {
		com.despegar.soffheap.map.SoffHeapMapBuilder<Key, Value> builder = new TypedBuilder<Key, Value>().createBuilder(valueClass);
		builder.withMaximumHeapElements(maxHeapElements);
		return new SoffHeapMapImpl<Key, Value>(builder.build());
	}
}
