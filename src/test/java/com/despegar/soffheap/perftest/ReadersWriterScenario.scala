package com.despegar.soffheap.perftest

import com.despegar.soffheap.map.{SoffHeapMapBuilder, SoffHeapMap}
import scala.reflect.ClassTag
import java.util.concurrent.Executors

class ReadersWriterScenario[Key, Value: ClassTag](readers: Int, writers: Int, valueFactory: (Unit => Value), keyFactory: (Unit => Key)) {

  val snapshot = new SoffHeapMapBuilder[Key,Value]().build()
  val readersExecutor = Executors.newFixedThreadPool(readers)
  val writersExecutor = Executors.newFixedThreadPool(writers)

  def start() = {
	  startReaders()
	  startWriters()
  }
  
  def startReaders() = {

    (1 to readers) foreach { i =>
      readersExecutor.submit(new MultiGetReader(snapshot, keyFactory))
    }

  }
  
  def startWriters() = {
    (1 to writers) foreach { i =>
      writersExecutor.submit(new Writer(snapshot, keyFactory, valueFactory))
    }
  }
  
  def stopWriters() = {
    writersExecutor.shutdownNow()
  }

}