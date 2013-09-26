package com.despegar.soffheap;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.despegar.soffheap.map.j.SoffHeapMap;
import com.despegar.soffheap.map.j.SoffHeapMapBuilder;

public class SoffHeapMapJavaClientTest {

	@Test
	public void shouldStoreObjectOutOfTheHeap() {

		SoffHeapMap<String, PojoValue> soffheapMap = SoffHeapMapBuilder.newBuilder()
															.withValueClass(PojoValue.class)
															.withMaxHeapElements(10).build();

		soffheapMap.put("key1", new PojoValue("value1",1l));

		PojoValue pojoValueInSnapshot = soffheapMap.get("key1");

		assertEquals("value1", pojoValueInSnapshot.someString);
		assertEquals(1l, pojoValueInSnapshot.someLong.longValue());
	}
	
	@Test
	public void shouldCreateSoffHeapMapWithoutHeapCache() {

		SoffHeapMap<String, PojoValue> soffheapMap = SoffHeapMapBuilder.newBuilder()
															.withValueClass(PojoValue.class)
															.build();

		soffheapMap.put("key1", new PojoValue("value1",1l));

		PojoValue pojoValueInSnapshot = soffheapMap.get("key1");

		assertEquals("value1", pojoValueInSnapshot.someString);
		assertEquals(1l, pojoValueInSnapshot.someLong.longValue());
	}
	
}
