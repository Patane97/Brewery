package com.Patane.Brewery;

public class BrCollectable {
	private String name;
	final private String identifier;
	protected BrCollectable(String name){
		this.name = name;
		// change identifier to be all caps and spaces = _
		this.identifier = name;
	}
	public String getName(){
		return name;
	}
	public String getID(){
		return identifier;
	}
}
