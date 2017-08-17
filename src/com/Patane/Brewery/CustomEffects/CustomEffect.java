package com.Patane.Brewery.CustomEffects;

import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

import com.Patane.Brewery.BrCollectable;

public class CustomEffect extends BrCollectable{
	final private EffectType effectType;
	final private int radius;
	final private DamageCause damageCause;
	public CustomEffect(String name, EffectType effectType, DamageCause damageCause){
		this(name, effectType, damageCause, 0);
	}
	
	public CustomEffect(String name, EffectType effectType, DamageCause damageCause, int radius) {
		super(name);
		this.effectType = effectType;
		this.radius = radius;
		this.damageCause = damageCause;
	}

	public EffectType getEffectType() {
		return effectType;
	}
	
	public int getRadius() {
		return radius;
	}

	public DamageCause getDamageCause() {
		return damageCause;
	}

	public void execute(LivingEntity entity) {
		entity.setLastDamageCause(new EntityDamageEvent());
	}
	
	
}
