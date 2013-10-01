package com.despegar.soffheap.perftest

import com.despegar.soffheap.map.{SoffHeapMapBuilder, SoffHeapMap}
import scala.reflect.ClassTag
import java.util.concurrent.Executors
import com.despegar.soffheap.ProviderMappingInfo

class ReadersWriterScenario[Key, Value](readers: Int, writers: Int, valueFactory: (Unit => Value), keyFactory: (Unit => Key)) {

  val soffheapMap = SoffHeapMapBuilder.of[Key,Value]().withKryo.withHintedClass(classOf[ProviderMappingInfo]).withName("offHeap1").build()
  val readersExecutor = Executors.newFixedThreadPool(readers)
  val writersExecutor = Executors.newFixedThreadPool(writers)

  def start() = {
	  startReaders()
	  startWriters()
  }
  
  def startReaders() = {

    (1 to readers) foreach { i =>
      readersExecutor.submit(new MultiGetReader(soffheapMap, keyFactory))
    }

  }
  
  def startWriters() = {
    (1 to writers) foreach { i =>
      writersExecutor.submit(new Writer(soffheapMap, keyFactory, valueFactory))
    }
  }
  
  def stopWriters() = {
    writersExecutor.shutdownNow()
  }

}