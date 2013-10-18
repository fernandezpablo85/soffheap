package com.despegar.soffheap.snapshot

trait DiskPersistor {

  def persist(values: Map[_, _])
  
  def loadFromDisk: Map[_, _]
  
  def hasData: Boolean
  
}