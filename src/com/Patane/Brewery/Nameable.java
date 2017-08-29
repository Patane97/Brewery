package com.Patane.Brewery;

public class Nameable {
	public String name(){
		String name = this.getClass().getAnnotation(Namer.class).name();
		return (name == null ? "<ERROR>" : name);
	}
}
