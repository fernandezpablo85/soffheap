package com.despegar.soffheap;

import java.io.Serializable;

public class PojoValue implements Serializable {

	private static final long serialVersionUID = 1L;
	public String someString;
	public Long someLong;
	
	public PojoValue() {
	}

	public PojoValue(String someString , Long someLong) {
		this.someString = someString;
		this.someLong = someLong;
	}
	
}
