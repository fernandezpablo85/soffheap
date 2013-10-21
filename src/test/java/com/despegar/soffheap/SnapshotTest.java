package com.despegar.soffheap;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import com.despegar.soffheap.snapshot.DataSource;
import com.despegar.soffheap.snapshot.Snapshot;
import com.despegar.soffheap.snapshot.SnapshotBuilder;

public class SnapshotTest {

	@Test
	public void snapshotBuilderTest() {
		SnapshotBuilder<String, PojoValue> builder = new SnapshotBuilder<String, PojoValue>();
		Snapshot<String, PojoValue> snapshot = builder.withDiskPersistence().withDataSource(new DataSource<String, PojoValue>() {
			@Override
			public Map<String, PojoValue> get() {
				Map<String, PojoValue> map = new HashMap<String, PojoValue>();
				for(int i = 0; i < 1000; i++) {
					map.put("Test " + i, new PojoValue("Pojo" + i, 1L));
				}	
				return map;
			}
		}).withName("").build();
	
		new ConcreteBuilder().withName("lala").withSpecificConcrete();
		
	}
	
}