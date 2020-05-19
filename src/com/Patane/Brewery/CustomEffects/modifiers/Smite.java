package com.Patane.Brewery.CustomEffects.modifiers;

import java.util.Map;

import com.Patane.Brewery.CustomEffects.Modifier;
import com.Patane.util.YAML.Namer;
import com.Patane.util.general.Check;

@Namer(name="smite")
public class Smite extends Modifier{
	final public double amount;

	public Smite(Map<String, String> fields){
		amount = Check.greaterThanEqual(getDouble(fields, "amount"), 0, "Amount must be greater than or equal to 0.");
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
