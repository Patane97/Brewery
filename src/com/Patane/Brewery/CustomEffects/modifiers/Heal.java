package com.Patane.Brewery.CustomEffects.modifiers;

import java.util.Map;

import com.Patane.Brewery.CustomEffects.Modifier;
import com.Patane.util.YML.Namer;
import com.Patane.util.general.Check;

@Namer(name="HEAL")
public class Heal extends Modifier{
	final public double amount;
	
	public Heal(Map<String, String> fields){
		amount = Check.greaterThan(getDouble(fields, "amount"), 0, "Amount must be greater than 0.");
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
