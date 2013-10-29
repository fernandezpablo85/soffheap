package com.despegar.soffheap.perftest

import com.despegar.soffheap.ProviderMappingInfo
import java.util.Random
import com.despegar.soffheap.metrics.SoffHeapMetricsRegistryHolder

object DbsProviderMappingTest extends App {

  val elements = System.getProperty("elements").toInt
  val readers = System.getProperty("readers").toInt
  val writers = System.getProperty("writers").toInt

  val registry =  SoffHeapMetricsRegistryHolder.getMetricsRegistry()
  val reporter = GangliaReporter.forRegistry(registry)
                                .convertRatesTo(TimeUnit.SECONDS)
                                .convertDurationsTo(TimeUnit.MILLISECONDS)
                                .build(ganglia)

  reporter.start(1, TimeUnit.MINUTES)

  val arrayWriter: (Unit => java.util.ArrayList[ProviderMappingInfo]) = { Unit =>
    val list =  new java.util.ArrayList[ProviderMappingInfo]()
    (1 to elements) foreach { i => 
      val mapping = new ProviderMappingInfo()
      mapping.setSupplierCode(s"ABC$i")
      mapping.setHotelQuantity(600+i)
      mapping.setExternalHotelCode(s"ABCDEFGHIJK$i")
      mapping.setExternalCityCode(s"qwertyuiop$i")
      list.add(mapping) }
    list
  }

  val rand = new Random()
  val scenario = new ReadersWriterScenario[Long, java.util.ArrayList[ProviderMappingInfo]](readers, writers, arrayWriter, { Unit => 
    	rand.nextInt(200001)
  })

  scenario.start()
  
  Thread.sleep(60000)
  
  scenario.stopWriters()
  
}