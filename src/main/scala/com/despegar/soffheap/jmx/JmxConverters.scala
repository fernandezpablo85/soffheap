package com.despegar.soffheap.jmx

import nl.grons.metrics.scala.Counter
import com.codahale.metrics.{Counter => CHCounter,Timer => CHTimer}
import com.tzavellas.sse.jmx.export.annotation.Managed
import com.codahale.metrics.Snapshot
import java.util.concurrent.TimeUnit
import com.tzavellas.sse.jmx.export.MBeanExporter

object JmxConverters {

  implicit def asJmxCounterConverter(counter: nl.grons.metrics.scala.Counter) = {
    new JmxCounter(counter)
  }
  
  implicit def asJmxTimerConverter(timer: nl.grons.metrics.scala.Timer) = {
    new JmxTimer(timer)
  }
  
}

trait JmxEnabled {
  
  @volatile  @Managed(readOnly=true)
  var enabled:Boolean = false
  
  @Managed
  def enable() = {
    enabled = true
  }
  
  @Managed
  def disable() = {
    enabled = false
  }
  
  val exporter = new MBeanExporter
  exporter.export(JmxEnabled.this)
  
}

class JmxCounter(counter: nl.grons.metrics.scala.Counter) extends nl.grons.metrics.scala.Counter(null.asInstanceOf[CHCounter]) with JmxEnabled  {
  
  def jmxEnabled(): nl.grons.metrics.scala.Counter = {
     JmxCounter.this.asInstanceOf[nl.grons.metrics.scala.Counter]
  }
  
    /**
   * Wraps partial function pf, incrementing counter once for every execution
   */
   override def count[A,B](pf: PartialFunction[A,B]): PartialFunction[A,B] = {
      if (enabled) {
    	  counter.count(pf)
      } else {
        new PartialFunction[A,B] {
	       def apply(a: A): B = {
	          pf.apply(a)
	       }
	
	       def isDefinedAt(a: A) = pf.isDefinedAt(a)
	     }
      }
   }

  /**
   * Increments the counter by delta.
   */
  override def +=(delta: Long) {
    if (enabled) counter.+=(delta)
  }

  /**
   * Decrements the counter by delta.
   */
  override def -=(delta: Long) {
    if (enabled) counter.-=(delta)
  }

  /**
   * Increments the counter by 1.
   */
  override def inc(delta: Long = 1) {
    if (enabled) counter.inc(delta)
  }

  /**
   * Decrements the counter by 1.
   */
  override def dec(delta: Long = 1) {
    if (enabled) counter.dec(delta)
  }

  
  /**
   * The current count.
   */
  override def count: Long = counter.count
  
}

class JmxTimer(timer: nl.grons.metrics.scala.Timer) extends nl.grons.metrics.scala.Timer(null.asInstanceOf[CHTimer]) with JmxEnabled {
  
  def jmxEnabled(): nl.grons.metrics.scala.Timer = {
     JmxTimer.this.asInstanceOf[nl.grons.metrics.scala.Timer]
  }
  
  /**
   * Runs f, recording its duration, and returns its result.
   */
  override def time[A](f: => A): A = {
		if (enabled) {
		  timer.time(f)
		}  
		else f
  }

  /**
   * Wraps partial function pf, timing every execution
   */
   override def timePF[A,B](pf: PartialFunction[A,B]): PartialFunction[A,B] = {
      if (enabled) timer.timePF(pf)
      else new PartialFunction[A,B] {
       def apply(a: A): B = {
             pf.apply(a)
       }

       def isDefinedAt(a: A) = pf.isDefinedAt(a)
     }
   }

  /**
   * Adds a recorded duration.
   */
  override def update(duration: Long, unit: TimeUnit) {
    if (enabled) timer.update(duration, unit)
  }

  /**
   * A timing [[com.codahale.metrics.Timer.Context]],
   * which measures an elapsed time in nanoseconds.
   */
  override def timerContext(): CHTimer.Context = timer.timerContext()

  /**
   * The number of durations recorded.
   */
  override def count: Long = timer.count

  /**
   * The longest recorded duration in nanoseconds.
   */
  override def max: Long = timer.max

  /**
   * The shortest recorded duration in nanoseconds.
   */
  override def min: Long = timer.min

  /**
   * The arithmetic mean of all recorded durations in nanoseconds.
   */
  override def mean: Double = timer.mean

  /**
   * The standard deviation of all recorded durations.
   */
  override def stdDev: Double = timer.stdDev

  /**
   * A snapshot of the values in the timer's sample.
   */
  override def snapshot: Snapshot = timer.snapshot

  /**
   * The fifteen-minute rate of timings.
   */
  override def fifteenMinuteRate: Double = timer.fifteenMinuteRate

  /**
   * The five-minute rate of timings.
   */
  override def fiveMinuteRate: Double = timer.fiveMinuteRate

  /**
   * The mean rate of timings.
   */
  override def meanRate: Double = timer.meanRate

  /**
   * The one-minute rate of timings.
   */
  override def oneMinuteRate: Double = timer.oneMinuteRate
  
}