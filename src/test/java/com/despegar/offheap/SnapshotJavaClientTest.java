package com.despegar.offheap;

import static org.junit.Assert.*;

import org.junit.Test;

public class SnapshotJavaClientTest {

	@Test
	public void shouldStoreObjectOutOfTheHeap() {
		OffheapMapSnapshot<String, PojoValue> snapshot = new OffheapMapSnapshot<String, PojoValue>();
		
		snapshot.put("key1", new PojoValue("value1",1l));
		
		PojoValue pojoValueInSnapshot = snapshot.javaGet("key1");
		
		assertEquals("value1", pojoValueInSnapshot.someString);
		assertEquals(1l, pojoValueInSnapshot.someLong.longValue());
		
	}
	
}
