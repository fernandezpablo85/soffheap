package com.despegar.soffheap;

public abstract class ABuilder<T> {

	abstract protected  T me();
	
	abstract T withName(String name);

	abstract T withMaximumHeapElements(int elements);

	abstract T withMaxSoffHeapMemoryInGB(long size);
	
	abstract T withHintedClass(Class hintedClass);
			  
	abstract T withKryo();
			  
	abstract T withFST();
	
	public void lala() {
		
	}
}
