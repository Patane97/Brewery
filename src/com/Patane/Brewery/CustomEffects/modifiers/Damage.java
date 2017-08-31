package com.Patane.Brewery.CustomEffects.modifiers;

import java.util.Map;

import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

import com.Patane.Brewery.Namer;
import com.Patane.Brewery.CustomEffects.Modifier;

@Namer(name="DAMAGE")
public class Damage extends Modifier{
	@SuppressWarnings("unused")
	final private DamageCause cause;
	final private double amount;
	
	public Damage(Map<String, String> fields){
		cause = getDamageCause(fields, "cause");
		amount = getDouble(fields, "amount");
	}
	
	public Damage(DamageCause cause, double amount){
		this.cause = cause;
		this.amount = amount;
	}
	@Override
	public void modify(LivingEntity damagee, Entity damager) {
		if(amount > 0){
			if(damager != null){
				damage(damagee, damager, amount);
			}
			damagee.damage(amount);
		}
	}
}
