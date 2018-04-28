package com.Patane.Brewery.CustomEffects.modifiers;

import java.util.Map;

import com.Patane.Brewery.CustomEffects.Modifier;
import com.Patane.util.YML.Namer;

@Namer(name="IGNITE")
public class Ignite extends Modifier{
	final public int duration;
	
	public Ignite(Map<String, String> fields){
		duration = Math.round((float) getDouble(fields, "duration")*20);
	}
	
	public Ignite(int duration){
		this.duration = duration;
	}
	@Override
	public void modify(ModifierInfo info) {
		info.getTarget().setFireTicks(duration);
	}
	
	@Override
	public String[] stringValues() {
		String[] values = {Double.toString(duration)};
		return values;
	}
}
