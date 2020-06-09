package com.Patane.Brewery.CustomEffects.modifiers;

import java.util.Map;

import com.Patane.Brewery.CustomEffects.Modifier;
import com.Patane.util.annotations.ClassDescriber;

@ClassDescriber(
		name="none",
		desc="Does absolutely nothing.")
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
