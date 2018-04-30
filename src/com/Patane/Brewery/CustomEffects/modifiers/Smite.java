package com.Patane.Brewery.CustomEffects.modifiers;

import java.util.Map;

import com.Patane.Brewery.CustomEffects.Modifier;
import com.Patane.util.YML.Namer;

@Namer(name="SMITE")
public class Smite extends Modifier{
	final public double amount;

	public Smite(Map<String, String> fields){
		amount = getDouble(fields, "amount");
	}
	public Smite(double amount){
		this.amount = amount;
	}
	@Override
	public void modify(ModifierInfo info) {
		info.getTarget().getWorld().strikeLightningEffect(info.getTarget().getLocation());
		damage(info.getTarget(), info.getTargeter(), amount);
	}
}
