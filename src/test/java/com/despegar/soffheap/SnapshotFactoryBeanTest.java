package com.despegar.soffheap;

import com.despegar.soffheap.snapshot.Snapshot;
import junit.framework.Assert;
import org.junit.Test;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class SnapshotFactoryBeanTest {

	@Test
	public void createSnapshotWithFactoryBean() {
        ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext("ApplicationContextTest.xml");
        Snapshot someSnapshot = (Snapshot) ctx.getBean("someSnapshot");

        Assert.assertNotNull(someSnapshot);
        Assert.assertEquals(1, someSnapshot.size());
    }
}