package com.despegar.soffheap;

import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import com.despegar.soffheap.snapshot.DataSource;
import com.despegar.soffheap.snapshot.Snapshot;
import com.despegar.soffheap.snapshot.SnapshotBuilder;

public class SnapshotTest {

	@Test
	public void testSaveAndLoadFromDisk() {
		Snapshot<String, PojoValue> firstSnapshot = createSnapshot();
		
		Snapshot<String, PojoValue> snapshotLoadedFromDisk = createSnapshot(); //this snapshot must load data stored on disk by the first snapshot
	
		assertEquals(1000, snapshotLoadedFromDisk.size());
	}
	
	@Test
	public void testSnapshotScheduler() throws InterruptedException {
		Snapshot<String, PojoValue> snapshot = new SnapshotBuilder<String, PojoValue>().withDiskPersistence().withName("SnapshotScheduler").withDataSource(snapshotData()).withReloadsAt("0/30 * * ? * *").build();
		
		Thread.sleep(5000L);
	}
	
	private Snapshot<String, PojoValue> createSnapshot() {
		return new SnapshotBuilder<String, PojoValue>().withDiskPersistence().withDataSource(snapshotData()).withName("PojoValueSnapshot").build();
	}

	private DataSource<String, PojoValue> snapshotData() {
		return new DataSource<String, PojoValue>() {
			@Override
			public Map<String, PojoValue> get() {
				Map<String, PojoValue> map = new HashMap<String, PojoValue>();
				for(int i = 0; i < 1000; i++) {
					map.put("Test " + i, new PojoValue("Pojo" + i, 1L));
				}	
				return map;
			}
		};
	}
	
}