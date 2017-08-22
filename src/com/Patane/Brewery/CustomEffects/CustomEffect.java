package com.Patane.Brewery.CustomEffects;

import java.util.ArrayList;
import java.util.Arrays;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.potion.PotionEffect;

import com.Patane.Brewery.Collections.BrCollectable;
import com.Patane.Brewery.util.LocationUtilities;

public abstract class CustomEffect extends BrCollectable{
	protected final PlayerModifier modifier;
	protected final int radius; 
	// if radius is 0, projectile must HIT an entity to damage/affect it and only it.
	protected final ArrayList<PotionEffect> potionEffects;
	
	public CustomEffect(String name, PlayerModifier modifier, int radius, PotionEffect... potionEffects) {
		super(name);
		this.modifier = modifier;
		this.radius = (radius < 0 ? 0 : radius);
		this.potionEffects = new ArrayList<PotionEffect>(Arrays.asList(potionEffects));
		
	}
	public PlayerModifier getModifier() {
		return modifier;
	}
	
	public int getRadius() {
		return radius;
	}
	
	public ArrayList<PotionEffect> getPotionEffects(){
		return potionEffects;
	}
	
	public abstract void execute(LivingEntity shooter, Location location, EntityType[] hitableEntities);
	
	protected void executeOnEntities(LivingEntity shooter, Location location, EntityType[] hitableEntities) {
		ArrayList<LivingEntity> hitEntities = LocationUtilities.getEntities(location, radius, hitableEntities);
		for(LivingEntity hitEntity : hitEntities){
			hitEntity.addPotionEffects(potionEffects);
			modifier.modify(hitEntity, shooter);
		}
	}
	public static interface PlayerModifier {
		public abstract void modify(LivingEntity entity1, Entity entity2);
	}
	public static class Damage implements PlayerModifier{
		@SuppressWarnings("unused")
		final private DamageCause damageCause;
		final private double damage;
		
		public Damage(DamageCause damageCause, double damage){
			this.damageCause = damageCause;
			this.damage = damage;
		}
		@Override
		public void modify(LivingEntity damagee, Entity damager) {
			if(damage > 0){
				if(damager != null){
					damagee.damage(damage, damager);
				}
				damagee.damage(damage);
			}
		}
	}
	public static class Heal implements PlayerModifier{
		final private double amount;
		
		public Heal(double amount){
			this.amount = amount;
		}
		@Override
		public void modify(LivingEntity healee, Entity nullEntity) {
			if(amount > 0)
				healee.setHealth(Math.min(20, healee.getHealth() + amount));
		}
	}
	public static class Feed implements PlayerModifier{
		final private double amount;
		
		public Feed(double amount){
			this.amount = amount;
		}
		@Override
		public void modify(LivingEntity entity, Entity nullEntity) {
			if(entity instanceof Player){
				Player player = (Player) entity;
				player.setFoodLevel((int) Math.min(20, (player.getFoodLevel() + amount)));
			}
		}
	}
}
