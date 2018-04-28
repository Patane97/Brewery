package com.Patane.Brewery.Collections;

import com.Patane.util.general.Check;
import com.Patane.util.general.StringsUtil;

public class BrCollectable {
	final private String name;
	final private String identifier;
	protected BrCollectable(String name){
		this.name = Check.nulled(name, "Name is missing for BrCollectable");
		this.identifier = StringsUtil.normalize(name);
	}
	public String getName(){
		return name;
	}
	public String getID(){
		return identifier;
	}
}
