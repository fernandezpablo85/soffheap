package com.despegar.offheap.perftest

import annotation.tailrec
import com.google.caliper.Param
import com.despegar.offheap.map.OffheapMapSnapshot
import com.despegar.offheap.PojoValue
import scala.collection.mutable.ListBuffer
import java.util.HashMap
import com.despegar.offheap.serialization.KryoSerializer
import com.despegar.offheap.map.OffheapMapBuilder

// a caliper benchmark is a class that extends com.google.caliper.Benchmark
// the SimpleScalaBenchmark trait does it and also adds some convenience functionality
class OffheapBenchmark extends SimpleScalaBenchmark {
  // to make your benchmark depend on one or more parameterized values, create fields with the name you want
  // the parameter to be known by, and add this annotation (see @Param javadocs for more details)
  // caliper will inject the respective value at runtime and make sure to run all combinations 
//  @Param(Array("10", "100", "1000", "10000"))
  @Param(Array("10000"))
  val arrays: Int = 0
  
  @Param(Array("100"))
  val elements: Int = 0
  
  var snapshot: OffheapMapSnapshot[String, Array[PojoValue]] = _
  var fstSnapshot: OffheapMapSnapshot[String, Array[PojoValue]] = _
  var hashmap: HashMap[String, Array[PojoValue]] = _
  
  
  override def setUp() {
    // set up all your benchmark data here
    if (snapshot != null) {
      snapshot.clear()
    }
    snapshot = new OffheapMapBuilder[String,Array[PojoValue]]().withMaximumHeapElements(10).build()
    hashmap = new HashMap[String, Array[PojoValue]]()
    
    (1 to arrays) foreach {
       arrayIndex => 
       val listBuffer: ListBuffer[PojoValue] = ListBuffer.empty
		     (1 to elements) foreach { (i => listBuffer += new PojoValue(s"value$i", i)) }
       val value = listBuffer.toArray
       val key = s"key-$arrayIndex"
         	 snapshot.put(key, value)
         	 hashmap.put(key, value)
         	 fstSnapshot.put(key, value)
    }
  }
  
  def timeHashMap(reps: Int) = repeat(reps) {
         (1 to arrays) map { arrayIndex => 
           val key = s"key-$arrayIndex"
           val obj =   hashmap.get(key)
         }
  }
  
  def timeOffheapKryoSnapshot(reps: Int) = repeat(reps) {
         (1 to arrays) map { arrayIndex => 
           val key = s"key-$arrayIndex"
           val obj =   snapshot.get(key)
         }
  }
  
  
  override def tearDown() {
    // clean up after yourself if required
  }
  
}

