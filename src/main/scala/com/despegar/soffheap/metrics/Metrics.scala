package com.despegar.soffheap.metrics

import com.codahale.metrics.MetricRegistry
import java.util.concurrent.TimeUnit
import nl.grons.metrics.scala.InstrumentedBuilder
import com.codahale.metrics.JmxReporter

trait Metrics extends InstrumentedBuilder {

  def metricsPrefix:String

  val metricRegistry = Metrics.metrics

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

  val metrics = new MetricRegistry()

  val reporter = JmxReporter.forRegistry(metrics).convertRatesTo(TimeUnit.SECONDS)
                            .convertDurationsTo(TimeUnit.MILLISECONDS)
                            .build()
  reporter.start()

}

object JMetrics {

  def getMetrics() = {
     Metrics.metrics
   }
}