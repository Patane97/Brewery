package com.Patane.Brewery.CustomEffects.modifiers;

import java.util.Map;

import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;

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
	public void modify(LivingEntity healee, Entity healer) {
		if(amount > 0)
			healee.setHealth(Math.min(20, healee.getHealth() + amount));
	}
}
