package com.despegar.soffheap.snapshot

import com.despegar.soffheap.concurrent.CronThreadPoolExecutor
import java.util.concurrent.ThreadFactory
import java.util.concurrent.Executors
import com.despegar.soffheap.CronExpression
import scala.collection.JavaConverters._


class Loader[Key,Value](snapshot: SoffheapSnapshot[Key, Value], dataSource: DataSource[Key,Value], diskPersistor: Option[DiskPersistor]) {
  
  val cronExpressionPool = new CronThreadPoolExecutor(1, new ThreadFactory() {
    override def newThread(runnable: Runnable) = {
       val thread = Executors.defaultThreadFactory().newThread(runnable)
       thread.setDaemon(true)
       thread.setName(s"SoffheapSnapshotReloader-${snapshot.getName}")
       thread
    }
  })
  
  def load(shouldCheckDisk: Boolean = true) = {
	 val values = getValues(shouldCheckDisk)
     snapshot.reload(values.asScala.toMap)
     diskPersistor.foreach( p => p.persist(values))
  }
  
  def reload() = {
	  load(false)
  }
  
  private def getValues(shouldCheckDisk: Boolean): java.util.Map[Key, Value] = {
	  if (shouldCheckDisk && diskPersistor.isDefined && diskPersistor.get.hasData ) {
	    diskPersistor.get.loadFromDisk.asInstanceOf[java.util.Map[Key, Value]]
	 } else {
	   dataSource.get
	 }
  }

  
  def scheduleReloadAt(cronExpression: Option[String]) = {
    cronExpression foreach { expression =>
      cronExpressionPool.schedule(new Runnable() {
        override def run() = {
          reload()
        }
      }, new CronExpression(expression))
    }
  }
}