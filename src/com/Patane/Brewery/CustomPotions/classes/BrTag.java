package com.Patane.Brewery.CustomPotions.classes;

public class BrTag {
	String prefix;
	String fullTag;
	String shortTag;
	
	public BrTag(String potionName){
		prefix = "BR";
		shortTag = potionName.toUpperCase().replace(" ", "_");
		fullTag = prefix+"{"+shortTag+"}";
	}
	public String fullTag(){
		return fullTag;
	}
	public String shortTag(){
		return shortTag;
	}
	public boolean equals(BrTag other){
		if(shortTag == other.shortTag())
			return true;
		return false;
	}
	public static String toItemName(String str) {
		return str.replace("_", " ");
	}
}
