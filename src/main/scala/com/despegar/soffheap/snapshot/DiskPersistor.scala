package com.despegar.soffheap.snapshot

import java.io.File

trait DiskPersistor {

  def persist(values: java.util.Map[_, _])
  
  def loadFromDisk: java.util.Map[_, _]
  
  def hasData: Boolean
  
}

object DiskPersistor {
  
  def createPathTo(file: File) {
        val parent = file.getParentFile();
        if (parent != null && !parent.exists()) {
        	parent.mkdir()
            createPathTo(parent)
        }
    }
  
  
}