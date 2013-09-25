package com.despegar.offheap;

import com.despegar.offheap.map.OffheapMapBuilder;
import com.despegar.offheap.map.OffheapMapSnapshot;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class SnapshotJavaClientTest {

	@Test
	public void shouldStoreObjectOutOfTheHeap() {

		
		OffheapMapBuilder<String, PojoValue> builder = SnapshotBuilderFactory.createBuilder(PojoValue.class);

		OffheapMapSnapshot<String, PojoValue> snapshot = builder.withMaximumHeapElements(100).build();

		snapshot.put("key1", new PojoValue("value1",1l));

		PojoValue pojoValueInSnapshot = snapshot.javaGet("key1");

		assertEquals("value1", pojoValueInSnapshot.someString);
		assertEquals(1l, pojoValueInSnapshot.someLong.longValue());
	}
	
}
