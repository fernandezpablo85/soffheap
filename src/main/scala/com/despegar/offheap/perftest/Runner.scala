package com.despegar.offheap.perftest

import com.google.caliper.{Runner => CaliperRunner}

object Runner {

  // main method for IDEs, from the CLI you can also run the caliper Runner directly
  // or simply use SBTs "run" action
  def main(args: Array[String]) {
    System.setProperty("fst.unsafe","true")

    
    // we simply pass in the CLI args,
    // we could of course also just pass hardcoded arguments to the caliper Runner
    CaliperRunner.main(classOf[OffheapBenchmark], args: _*)
  }
  
}