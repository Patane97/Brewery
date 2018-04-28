package com.Patane.Brewery.CustomEffects.modifiers;

import com.Patane.Brewery.CustomEffects.Modifier;
import com.Patane.util.YML.Namer;

@Namer(name="KILL")
public class Kill extends Modifier{
	
	@Override
	public void modify(ModifierInfo info) {
		damage(info.getTarget(), info.getTargeter(), info.getTarget().getHealth());
	}

	@Override
	public String[] stringValues() {
		String[] values = {"N/A"};
		return values;
	}
}
