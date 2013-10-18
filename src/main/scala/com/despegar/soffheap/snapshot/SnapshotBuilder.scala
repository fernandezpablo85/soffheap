package com.despegar.soffheap.snapshot

import com.despegar.soffheap.map.TunneableSoffheapMapBuilder

class SnapshotBuilder[Key, Value] extends TunneableSoffheapMapBuilder[SnapshotBuilder[Key, Value], Key, Value] {

  var dataSource: DataSource[Key, Value] = _
  var cronExpression: Option[String] = None
  var diskPersistencePath: Option[String] = None
  
  def withDiskPersistence(path: String): SnapshotBuilder[Key, Value] = {
     diskPersistencePath = Some(path)
     this
  }
  
  def withReloadsAt(cronExpression: String): SnapshotBuilder[Key, Value] = {
    this.cronExpression = Some(cronExpression)
    this
  }
  
  def withDataSource(dataSource: DataSource[Key, Value]): SnapshotBuilder[Key, Value] = {
    this.dataSource = dataSource
    this
  }
  
  private def createDiskPersistor() = {
     diskPersistencePath.map( path => new KryoDiskPersistor(path, soffHeapName))
  }
  
  def build(): Snapshot[Key, Value] = {
	val snapshot = new SoffheapSnapshot[Key, Value](innerBuildSoffHeapMap)
	val loader = new Loader(snapshot, dataSource, createDiskPersistor())
	loader.load()
    loader.scheduleReloadAt(cronExpression)
	snapshot
  }
  
}

object SnapshotBuilder {
  
  def apply[Key, Value]() = {
    new SnapshotBuilder[Key, Value]()
  }
  
}