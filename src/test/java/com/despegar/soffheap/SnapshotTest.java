package com.despegar.soffheap;

import com.despegar.soffheap.snapshot.DataSource;
import com.despegar.soffheap.snapshot.Snapshot;
import com.despegar.soffheap.snapshot.SnapshotBuilder;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

public class SnapshotTest {

	@Test
	public void testSaveAndLoadFromDisk() {
		createSnapshot();
		
		Snapshot<String, PojoValue> snapshotLoadedFromDisk = createSnapshot(); //this snapshot must load data stored on disk by the first snapshot
	
		assertEquals(1000, snapshotLoadedFromDisk.size());
	}
	
	@Test
	public void testSnapshotScheduler() throws InterruptedException {
        DataSource<String, PojoValue> mockDs = mock(DataSource.class);

        Map<String, PojoValue> snapshotData =  new HashMap<String, PojoValue>();
        snapshotData.put("Test", new PojoValue("Pojo", 1l));

        Mockito.when(mockDs.get()).thenReturn(snapshotData);

		new SnapshotBuilder<String, PojoValue>()
                .withDiskPersistence()
                .withName("SnapshotScheduler")
                .withDataSource(mockDs)
                .withReloadsAt("0/2 * * ? * *")
                .build();

        Thread.sleep(6000L);

        verify(mockDs, atLeast(3)).get();

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