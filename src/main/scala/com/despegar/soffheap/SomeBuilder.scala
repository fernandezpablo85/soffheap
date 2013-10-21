package com.despegar.soffheap

trait SomeBuilder[T <: SomeBuilder[T]] {

	def self(): T
  
}