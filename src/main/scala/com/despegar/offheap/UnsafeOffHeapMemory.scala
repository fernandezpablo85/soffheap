package com.despegar.offheap

import java.util.concurrent.atomic.AtomicBoolean

class UnsafeOffHeapMemory(serialized: Array[Byte])  {

    val address = SoffHeap.allocate(serialized.length);
    val length = serialized.length
    val disposed = new AtomicBoolean(false);
    put(serialized)


    def free() {
        if(!disposed.compareAndSet(false, true)) return;
        SoffHeap.free(address);
    }


    def put(buffer: Array[Byte]) {
//        assert !disposed.get() : "disposed";
//        assert offset >= 0 : offset;
//        assert null != buffer;
//        assert offset <= length - buffer.length : offset;
//        assert buffer.length <= length : buffer.length;
        SoffHeap.UNSAFE.copyMemory(buffer, SoffHeap.BYTE_ARRAY_OFFSET, null, address, buffer.length);
    }


    def get(buffer: Array[Byte]) {
      
//        assert !disposed.get() : "disposed";
//        assert offset >= 0 : offset;
//        assert null != buffer;
//        assert offset <= length - buffer.length : offset;
//        assert buffer.length <= length : buffer.length;
        SoffHeap.UNSAFE.copyMemory(null, address, buffer, SoffHeap.BYTE_ARRAY_OFFSET, buffer.length);
    }

   
    override def toString() = {
        val sb = new StringBuilder()
        sb.append("UnsafeOffHeapMemory");
        sb.append("{address=").append(address);
        sb.append(", length=").append(length);
        sb.append(", disposed=").append(disposed);
        sb.append('}');
        sb.toString();
    }
}