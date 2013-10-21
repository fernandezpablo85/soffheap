package com.despegar.soffheap;

public abstract class AbstractBuilder<T extends AbstractBuilder<T>> {
	
	abstract protected T self();
	
	public T withName(String name) {
		return self();
	}

}
