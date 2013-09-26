package com.despegar.soffheap;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.despegar.soffheap.map.SoffHeapMapBuilder;
import com.despegar.soffheap.map.j.SoffHeapMap;

public class SoffHeapMapJavaClientTest {

	@Test
	public void shouldStoreObjectOutOfTheHeap() {

		SoffHeapMap<String, PojoValue> soffheapMap = new SoffHeapMapBuilder<String, PojoValue>()
				.withHintedClass(PojoValue.class).withMaximumHeapElements(10).withKryo()
				.buildJ();

		soffheapMap.put("key1", new PojoValue("value1", 1l));

		PojoValue pojoValueInSnapshot = soffheapMap.get("key1");

		assertEquals("value1", pojoValueInSnapshot.someString);
		assertEquals(1l, pojoValueInSnapshot.someLong.longValue());
	}

	@Test
	public void shouldCreateSoffHeapMapWithoutHeapCache() {

		SoffHeapMap<String, PojoValue> soffheapMap = new SoffHeapMapBuilder<String, PojoValue>()
				.withHintedClass(PojoValue.class).withMaximumHeapElements(10)
				.buildJ();

		soffheapMap.put("key1", new PojoValue("value1", 1l));

		PojoValue pojoValueInSnapshot = soffheapMap.get("key1");

		assertEquals("value1", pojoValueInSnapshot.someString);
		assertEquals(1l, pojoValueInSnapshot.someLong.longValue());
	}

}
