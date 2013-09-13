package com.despegar.offheap

trait SoffHeapt {

  def allocate(bytes: Long)

  def free(address: Long, bytes: Int)

  def put(address: Long, buffer: Array[Byte])

  def get(address: Long, buffer: Array[Byte])

}