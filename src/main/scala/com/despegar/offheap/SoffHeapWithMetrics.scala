package com.despegar.offheap

trait SoffHeapWithMetrics extends SoffHeapt{
  
  abstract override def allocate(bytes: Long) = {
    
  } 

  abstract override def free(address: Long, bytes: Int)= {
    
  } 

  abstract override def put(address: Long, buffer: Array[Byte])= {
    
  } 

  abstract override def get(address: Long, buffer: Array[Byte])= {
    
  } 


}