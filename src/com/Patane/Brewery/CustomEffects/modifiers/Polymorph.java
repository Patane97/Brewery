package com.Patane.Brewery.CustomEffects.modifiers;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.attribute.Attributable;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.Patane.Brewery.Brewery;
import com.Patane.Brewery.CustomEffects.Modifier;
import com.Patane.util.annotations.ClassDescriber;
import com.Patane.util.annotations.ParseField;
import com.Patane.util.general.Check;
import com.Patane.util.general.Messenger;
/**
 * Extremely experimental, needs work
 * Maybe using NMS somehow? (without ever removing the original entity to ensure compatibility eg. Battlegrounds)
 * @author Stephen
 *
 */

@ClassDescriber(
		name="polymorph",
		desc="Transforms a living entity into a specified mob for a certain duration.")
public class Polymorph extends Modifier{
	private HashMap<LivingEntity, Entity> currentlyMorphed = new HashMap<LivingEntity, Entity>();
	
	@ParseField(desc="Mob to transform the living entity into.")
	public EntityType entity;
	@ParseField(desc="Duration the living entity is transformed for. Measured in server ticks (1s = 20ticks).")
	public int duration;
	
	public Polymorph() {
		super();
	}
	
	public Polymorph(Map<String, String> fields) {
		super(fields);
	}
	

	@Override
	protected void populateFields(Map<String, String> fields) {
		entity = getEnumValue(EntityType.class, fields, "entity");
		duration = Math.round(Check.greaterThan((float) getDouble(fields, "duration"), 0, "Duration must be greater than 0."));
	}
	
	public Polymorph(EntityType entity, int duration) {
		this.entity = entity;
		this.duration = duration;
		construct();
	}

	/* 
	 * ================================================================================
	 */
	@Override
	protected void valueConverts() {
		// duration is converted from MC ticks to seconds (20 ticks = 1 second)
		customValueConverter.put("duration", i -> (int)i+" ticks");
	}
	/* 
	 * ================================================================================
	 */
	
	@Override
	public void modify(ModifierInfo info) {
		if(currentlyMorphed.containsKey(info.getTarget()) || currentlyMorphed.containsValue(info.getTarget()))
			return;
		LivingEntity targetEntity = info.getTarget();
		try {
			Entity morphed = targetEntity.getWorld().spawnEntity(targetEntity.getLocation(), entity);

			Collection<PotionEffect> currentEffects = targetEntity.getActivePotionEffects();
			for(PotionEffect effect : currentEffects)
				targetEntity.removePotionEffect(effect.getType());
			PotionEffect potion = new PotionEffect(PotionEffectType.INVISIBILITY, (int) (99999), 0, false, false);
			targetEntity.addPotionEffect(potion);
			targetEntity.setInvulnerable(true);
			targetEntity.setCollidable(false);
			targetEntity.setSilent(true);
			targetEntity.setAI(false);

			targetEntity.getWorld().spawnParticle(Particle.CLOUD, targetEntity.getLocation(), 50, morphed.getWidth()*.75,morphed.getHeight()*.75,morphed.getWidth()*.75, 0.05);
		
			if(morphed instanceof Attributable)
				((Attributable) morphed).getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(targetEntity.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue());
			if(morphed instanceof LivingEntity) {
				((LivingEntity) morphed).setCollidable(false);
				((LivingEntity) morphed).addPotionEffects(currentEffects);
				((LivingEntity) morphed).setFireTicks(targetEntity.getFireTicks());
			}
			currentlyMorphed.put(targetEntity, morphed);
			Brewery.getInstance().getServer().getScheduler().scheduleSyncDelayedTask(Brewery.getInstance(), new Runnable() {
				@Override
				public void run() {
					Location location = morphed.getLocation();
					if(morphed instanceof LivingEntity) {
						targetEntity.addPotionEffects(((LivingEntity) morphed).getActivePotionEffects());
						targetEntity.setFireTicks(((LivingEntity) morphed).getFireTicks());
					}
					targetEntity.getWorld().spawnParticle(Particle.CLOUD, morphed.getLocation(), 50, targetEntity.getWidth()*.75,targetEntity.getHeight()*.75,targetEntity.getWidth()*.75, 0.05);
					
					morphed.remove();
					targetEntity.teleport(location);
					Brewery.getInstance().getServer().getScheduler().scheduleSyncDelayedTask(Brewery.getInstance(), new Runnable() {
						@Override
						public void run() {
							targetEntity.removePotionEffect(PotionEffectType.INVISIBILITY);
							targetEntity.setInvulnerable(false);
							targetEntity.setCollidable(true);
							targetEntity.setSilent(false);
							targetEntity.setAI(true);
							currentlyMorphed.remove(targetEntity);
						}
					}, (long) 5);
					morphed.remove();
				}
			}, (long) duration-5);
			
		} catch (IllegalArgumentException e) {
			Messenger.severe("Failed to polymorph '"+targetEntity.getName()+"' entity into '"+entity.name()+"' entity.");
			throw e;
		}
	}
}
