package com.Patane.Brewery.CustomEffects.modifiers;

import java.util.Map;

import com.Patane.Brewery.Namer;
import com.Patane.Brewery.CustomEffects.Modifier;

@Namer(name="HEAL")
public class Heal extends Modifier{
	final private double amount;
	
	public Heal(Map<String, String> fields){
		amount = getDouble(fields, "amount");
	}
	
	public Heal(double amount){
		this.amount = amount;
	}
	@Override
	public void modify(ModifierInfo info) {
		if(amount > 0)
			info.getTarget().setHealth(Math.min(20, info.getTarget().getHealth() + amount));
	}
}
