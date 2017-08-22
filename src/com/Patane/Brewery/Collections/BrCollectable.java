package com.Patane.Brewery.Collections;

import com.Patane.Brewery.util.StringUtilities;

public class BrCollectable {
	final private String name;
	final private String identifier;
	protected BrCollectable(String name){
		this.name = name;
		this.identifier = StringUtilities.normalize(name);
	}
	public String getName(){
		return name;
	}
	public String getID(){
		return identifier;
	}
}
