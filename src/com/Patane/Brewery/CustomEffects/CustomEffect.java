package com.Patane.Brewery.CustomEffects;

import java.util.ArrayList;
import java.util.Arrays;

import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.potion.PotionEffect;

import com.Patane.Brewery.BrCollectable;
import com.Patane.Brewery.util.Locations;

public abstract class CustomEffect extends BrCollectable{
	protected final DamageContainer damageContainer;
	protected final int radius; 
	// if radius is 0, projectile must HIT an entity to damage/affect it and only it.
	protected final ArrayList<PotionEffect> potionEffects;
	
	public CustomEffect(String name, DamageContainer damageContainer, int radius, PotionEffect... potionEffects) {
		super(name);
		this.damageContainer = damageContainer;
		this.radius = (radius < 0 ? 0 : radius);
		this.potionEffects = new ArrayList<PotionEffect>(Arrays.asList(potionEffects));
		
	}
	private CustomEffect(CustomEffect customEffect){
		super(customEffect.getName());
		this.damageContainer = customEffect.getDamageContainer();
		this.radius = customEffect.getRadius();
		this.potionEffects = customEffect.getPotionEffects();
	}
	public DamageContainer getDamageContainer() {
		return damageContainer;
	}
	
	public int getRadius() {
		return radius;
	}
	
	public ArrayList<PotionEffect> getPotionEffects(){
		return potionEffects;
	}
	
	public abstract void execute(LivingEntity shooter, Location location);
	
	protected void executeOnEntities(LivingEntity shooter, Location location) {
		ArrayList<LivingEntity> hitEntities = Locations.getLivingEntities(location, radius);
		for(LivingEntity hitEntity : hitEntities){
			hitEntity.addPotionEffects(potionEffects);
			if(damageContainer.getDamage() > 0){
				if(shooter != null){
					hitEntity.damage(damageContainer.getDamage(), shooter);
					continue;
				}
				hitEntity.damage(damageContainer.getDamage());
			}
		}
	}
	public static class DamageContainer{
		final private DamageCause damageCause;
		final private double damage;
		
		public DamageContainer(DamageCause damageCause, double damage){
			this.damageCause = damageCause;
			this.damage = damage;
		}
		public DamageCause getCause(){
			return damageCause;
		}
		public double getDamage(){
			return damage;
		}
	}
}
