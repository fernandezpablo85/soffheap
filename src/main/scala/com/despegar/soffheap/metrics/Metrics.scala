package com.despegar.soffheap.metrics

import com.codahale.metrics.MetricRegistry
import java.util.concurrent.TimeUnit
import nl.grons.metrics.scala.InstrumentedBuilder
import com.codahale.metrics.JmxReporter
import nl.grons.metrics.scala.Gauge
import nl.grons.metrics.scala.MetricBuilder
import com.codahale.metrics.{Gauge => CHGauge}

trait Metrics extends InstrumentedBuilder {

  def metricsPrefix:String

  val metricRegistry = Metrics.registry
  
  def safeGauge[A](name: String, scope: String = null)(f: => A): Gauge[A] = {
    metricRegistry.synchronized {
      val gaugeName = MetricBuilder.metricName(getClass, Seq(name, scope))
    	var aGauge = metricRegistry.getGauges().get(gaugeName).asInstanceOf[CHGauge[A]]

      if (aGauge == null) {
    		aGauge = metricRegistry.register(gaugeName, new CHGauge[A] { def getValue: A = f })
    	}
    	new Gauge[A](aGauge)
    }
  }

  val K:Long = 1024
  val M:Long = K * K
  val G:Long = M * K

  val toKB = (bytes: Long) => bytes / K
  val toMB = (bytes: Long) => bytes / M

  protected[this] def maxSoffHeapMemoryInGB():Long = {

    def toGB = (v:Long) => v * G

    Option(System.getProperty("maxSoffHeapMemoryInGB")) match  {
      case None => toGB(2)
      case Some(amount) => toGB(amount.toLong)
    }
  }
}

object Metrics {
  val registry = new MetricRegistry()
  val reporter = JmxReporter.forRegistry(registry)
                            .inDomain("SoffHeap")
                            .convertRatesTo(TimeUnit.SECONDS)
                            .convertDurationsTo(TimeUnit.MILLISECONDS)
                            .build()
  reporter.start()

}

object SoffHeapMetricsRegistryHolder {
  def getMetricsRegistry() = {
     Metrics.registry
   }
}