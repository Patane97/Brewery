package com.Patane.Brewery.CustomEffects.modifiers;

import java.util.Map;

import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

import com.Patane.Brewery.Namer;
import com.Patane.Brewery.CustomEffects.Modifier;

@Namer(name="DAMAGE")
public class Damage extends Modifier{
	final public DamageCause cause;
	final public double amount;
	
	public Damage(Map<String, String> fields){
		cause = getEnumValue(DamageCause.class, fields, "cause");
		amount = getDouble(fields, "amount");
	}
	
	public Damage(DamageCause cause, double amount){
		this.cause = cause;
		this.amount = amount;
	}
	@Override
	public void modify(ModifierInfo info) {
		if(amount > 0){
			if(info.getTargeter() != null){
				damage(info.getTarget(), info.getTargeter(), amount);
			}
			info.getTarget().damage(amount);
		}
	}
}
