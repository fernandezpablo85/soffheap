package com.despegar.offheap.metrics

import com.codahale.metrics.{ConsoleReporter, MetricRegistry}
import java.util.concurrent.TimeUnit
import nl.grons.metrics.scala.InstrumentedBuilder

trait Metrics extends InstrumentedBuilder {
   val metricRegistry = Metrics.metrics
}

object Metrics {
  val metrics = new MetricRegistry()

  val reporter = ConsoleReporter.forRegistry(metrics).convertRatesTo(TimeUnit.SECONDS)
                                .convertDurationsTo(TimeUnit.MILLISECONDS)
                                .build()
  reporter.start(5, TimeUnit.SECONDS)
  
}