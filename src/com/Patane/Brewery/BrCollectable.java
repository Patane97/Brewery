package com.Patane.Brewery;

public class BrCollectable {
	private String name;
	final private String identifier;
	protected BrCollectable(String name){
		this.name = name;
		this.identifier = name.replace(" ", "_").toUpperCase();
	}
	public String getName(){
		return name;
	}
	public String getID(){
		return identifier;
	}
}
