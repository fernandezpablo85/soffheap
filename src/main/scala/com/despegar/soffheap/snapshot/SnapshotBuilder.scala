package com.despegar.soffheap.snapshot

import java.io.File

class SnapshotBuilder[Key, Value] extends TunneableSoffheapMapBuilder[SnapshotBuilder[Key, Value], Key, Value] {

  var dataSource: DataSource[Key, Value] = _
  var cronExpression: Option[String] = None
  var diskPersistencePath: Option[String] = None
  
  override def self(): SnapshotBuilder[Key, Value]  = {
    this
  }
  
  def withDiskPersistence(path: String): SnapshotBuilder[Key, Value] = {
    if (path == null || path.trim().length() == 0) {
      withDiskPersistence()
    }else{
      val trimmed = path.trim()
      diskPersistencePath = Some(
        if (trimmed.endsWith(String.valueOf(File.separatorChar)))
        trimmed.substring(0, trimmed.length() - 1)
      else trimmed)
    }

    this
  }
  
  def withDiskPersistence(): SnapshotBuilder[Key, Value] = {
    withDiskPersistence(System.getProperty("java.io.tmpdir"))
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
    diskPersistencePath.map(path => new SnappyDiskPersistor(path, soffHeapName))
  }

  def build(): Snapshot[Key, Value] = {
    val snapshot = new SoffHeapSnapshot[Key, Value](innerBuildSoffHeapMap())
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