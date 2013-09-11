package com.despegar.offheap

import java.io.ByteArrayOutputStream
import java.io.ObjectOutputStream
import java.io.BufferedOutputStream
import java.io.IOException
import java.io.ObjectInputStream
import java.io.BufferedInputStream
import java.io.ByteArrayInputStream
import java.io.Serializable
import java.util.concurrent.atomic.AtomicReference
import java.util.concurrent.locks.ReentrantReadWriteLock

class OffheapObject[HeapObject <: Serializable](heapObject: HeapObject) {

  val offheapMemory = new AtomicReference[UnsafeOffHeapMemory]()
  offheapMemory.set(new UnsafeOffHeapMemory(serialize(heapObject)));

  def get() = {
    val buffer = doGet()
    deserialize(buffer);
  }
  
  def doGet() = {
      val offheapMemory = retrieve()
      val buffer = new Array[Byte](offheapMemory.length);
      offheapMemory.get(buffer);
      buffer
  }

  private def retrieve() = {
    offheapMemory.get()
  }

  def freeWhenIsSafe(offheapMemory: UnsafeOffHeapMemory) = {
    offheapMemory.free()
  }

  def swap(newHeapObject: HeapObject) = {
    val oldOffHeapMemory = offheapMemory.getAndSet(new UnsafeOffHeapMemory(serialize(newHeapObject)))
    freeWhenIsSafe(oldOffHeapMemory)
  }

  def serialize(newObject: HeapObject) = {
    val byteArrayOutputStream = new ByteArrayOutputStream();
    val objectOutputStream = new ObjectOutputStream(new BufferedOutputStream(byteArrayOutputStream));
    objectOutputStream.writeObject(newObject);
    objectOutputStream.flush();
    objectOutputStream.close();
    byteArrayOutputStream.toByteArray();
  }

  def deserialize(buffer: Array[Byte]): HeapObject = {
    val objectInputStream = new ObjectInputStream(new BufferedInputStream(new ByteArrayInputStream(buffer)));
    objectInputStream.readObject().asInstanceOf[HeapObject];
  }

}