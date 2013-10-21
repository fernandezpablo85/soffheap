package com.despegar.soffheap;

public class ConcreteBuilder extends AbstractBuilder<ConcreteBuilder> {

	@Override
	protected ConcreteBuilder self() {
		return this;
	}
	
	public ConcreteBuilder withSpecificConcrete() {
		return this;
	}

}
