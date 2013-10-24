package com.despegar.soffheap.perftest

import com.despegar.soffheap.PojoValue
import scala.collection.mutable.ListBuffer

class HeapVsSoffheap {

  val arrays = System.getProperty("arrays").toInt
  val elements = System.getProperty("elements").toInt
  val readers = System.getProperty("readers").toInt
  val writers = System.getProperty("writers").toInt

  val arrayWriter: (Unit => Array[PojoValue]) = { Unit =>
    val listBuffer: ListBuffer[PojoValue] = ListBuffer.empty
    (1 to elements) foreach { i => listBuffer += new PojoValue(s"value$i", i) }
    listBuffer.toArray
  }
    
  val scenario = new ReadersWriterScenario[String, Array[PojoValue]](readers, writers, arrayWriter, { Unit => "alwaysSameKey" })

  scenario.start()
  
  Thread.sleep(60000)
  
  scenario.stopWriters()
  
}