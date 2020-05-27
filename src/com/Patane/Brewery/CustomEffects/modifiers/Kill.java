package com.Patane.Brewery.CustomEffects.modifiers;

import java.util.Map;

import com.Patane.Brewery.CustomEffects.Modifier;
import com.Patane.util.YAML.Namer;

@Namer(name="kill")
public class Kill extends Modifier{
	
	public Kill() {
		super();
	}	

	@Override
	protected void populateFields(Map<String, String> fields) {}

	/* 
	 * ================================================================================
	 */

	@Override
	public void modify(ModifierInfo info) {
		damage(info.getTarget(), info.getTargeter(), info.getTarget().getHealth());
	}
}
