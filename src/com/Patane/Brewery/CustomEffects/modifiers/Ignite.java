package com.Patane.Brewery.CustomEffects.modifiers;

import java.util.Map;

import com.Patane.Brewery.CustomEffects.Modifier;
import com.Patane.util.YML.Namer;
import com.Patane.util.general.Check;

@Namer(name="IGNITE")
public class Ignite extends Modifier{
	final public int duration;
	
	public Ignite(Map<String, String> fields){
		duration = (Math.round(Check.greaterThan((float) getDouble(fields, "duration")*20, 0, "Duration must be greater than 0.")));
	}
	
	public Ignite(int duration){
		this.duration = duration;
	}
	@Override
	public void modify(ModifierInfo info) {
		info.getTarget().setFireTicks(duration);
	}
}
