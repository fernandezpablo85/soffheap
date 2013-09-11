package com.despegar.offheap;

import java.lang.reflect.Field;

import sun.misc.Unsafe;


public class SoffHeap {

    public static final Unsafe UNSAFE;
    public static final int BYTE_ARRAY_OFFSET;

    static {
        try {
            Field theUnsafe = Unsafe.class.getDeclaredField("theUnsafe");
            theUnsafe.setAccessible(true);
            UNSAFE = (Unsafe) theUnsafe.get(null);
            int boo = UNSAFE.arrayBaseOffset(byte[].class);
            // It seems not all Unsafe implementations implement the following method.
            UNSAFE.copyMemory(new byte[1], boo, new byte[1], boo, 1);
            BYTE_ARRAY_OFFSET = UNSAFE.arrayBaseOffset(byte[].class);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    
    public static Long allocate(Long bytes) {
    	return UNSAFE.allocateMemory(bytes);
    }
    
    public static void free(Long address) {
    	UNSAFE.freeMemory(address);
    }
}
