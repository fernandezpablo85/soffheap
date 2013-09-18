package com.despegar.offheap.metrics

import com.codahale.metrics.MetricRegistry
import com.codahale.metrics.ConsoleReporter
import java.util.concurrent.TimeUnit
import com.codahale.metrics.JmxReporter

trait Metrics {

	val metrics = Metrics.metrics

}

object Metrics {
  
  val metrics = new MetricRegistry()

  val reporter = JmxReporter.forRegistry(metrics).convertRatesTo(TimeUnit.SECONDS)
    .convertDurationsTo(TimeUnit.MILLISECONDS)
    .build()
  reporter.start()
  
}