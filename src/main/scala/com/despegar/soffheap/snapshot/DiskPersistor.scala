package com.despegar.soffheap.snapshot

import java.io.File

trait DiskPersistor {

  def persist(values: Map[_, _])
  
  def loadFromDisk: Map[_, _]
  
  def hasData: Boolean
  
}

object DiskPersistor {
  
  def createPathTo(file: File) {
        val parent = file.getParentFile();
        if (parent != null && !parent.exists()) {
            createPathTo(parent)
        }
        file.mkdir();
    }
  
  
}