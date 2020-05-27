package com.Patane.Brewery.CustomEffects.modifiers;

import java.util.Map;

import com.Patane.Brewery.CustomEffects.Modifier;
import com.Patane.util.YAML.Namer;

@Namer(name="none")
public class None extends Modifier{
	
	public None() {
		super();
	}	

	@Override
	protected void populateFields(Map<String, String> fields) {}

	/* 
	 * ================================================================================
	 */
	
	@Override
	public void modify(ModifierInfo info) {}

}
