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
import com.Patane.util.YML.Namer;
/**
 * Extremely experimental, needs work
 * Maybe using NMS somehow? (without ever removing the original entity to ensure compatibility eg. Battlegrounds)
 * @author Stephen
 *
 */
@Namer(name="POLYMORPH")
public class Polymorph extends Modifier{
	private HashMap<LivingEntity, Entity> currentlyMorphed = new HashMap<LivingEntity, Entity>();
	
	final public EntityType morph;
	final public double duration;
	
	public Polymorph(Map<String, String> fields){
		morph = getEnumValue(EntityType.class, fields, "morph");
		duration = getDouble(fields, "duration");
	}
	
	public Polymorph(EntityType morph, double duration){
		this.morph = morph;
		this.duration = duration;
	}
	@Override
	public void modify(ModifierInfo info) {
		if(currentlyMorphed.containsKey(info.getTarget()) || currentlyMorphed.containsValue(info.getTarget()))
			return;
		LivingEntity entity = info.getTarget();
		Collection<PotionEffect> currentEffects = entity.getActivePotionEffects();
		for(PotionEffect effect : currentEffects)
			entity.removePotionEffect(effect.getType());
		PotionEffect potion = new PotionEffect(PotionEffectType.INVISIBILITY, (int) (99999), 0, false, false);
		entity.addPotionEffect(potion);
		entity.setInvulnerable(true);
		entity.setCollidable(false);
		entity.setSilent(true);
		entity.setAI(false);
		Entity morphed = entity.getWorld().spawnEntity(entity.getLocation(), morph);

		entity.getWorld().spawnParticle(Particle.CLOUD, entity.getLocation(), 50, morphed.getWidth()*.75,morphed.getHeight()*.75,morphed.getWidth()*.75, 0.05);
	
		if(morphed instanceof Attributable)
			((Attributable) morphed).getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(entity.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue());
		if(morphed instanceof LivingEntity){
			((LivingEntity) morphed).setCollidable(false);
			((LivingEntity) morphed).addPotionEffects(currentEffects);
			((LivingEntity) morphed).setFireTicks(entity.getFireTicks());
		}
		currentlyMorphed.put(entity, morphed);
		Brewery.getInstance().getServer().getScheduler().scheduleSyncDelayedTask(Brewery.getInstance(), new Runnable(){
			@Override
			public void run(){
				Location location = morphed.getLocation();
				if(morphed instanceof LivingEntity){
					entity.addPotionEffects(((LivingEntity) morphed).getActivePotionEffects());
					entity.setFireTicks(((LivingEntity) morphed).getFireTicks());
				}
				entity.getWorld().spawnParticle(Particle.CLOUD, morphed.getLocation(), 50, entity.getWidth()*.75,entity.getHeight()*.75,entity.getWidth()*.75, 0.05);
				
				morphed.remove();
				entity.teleport(location);
				Brewery.getInstance().getServer().getScheduler().scheduleSyncDelayedTask(Brewery.getInstance(), new Runnable(){
					@Override
					public void run(){
						entity.removePotionEffect(PotionEffectType.INVISIBILITY);
						entity.setInvulnerable(false);
						entity.setCollidable(true);
						entity.setSilent(false);
						entity.setAI(true);
						currentlyMorphed.remove(entity);
					}
				}, (long) 5);
				morphed.remove();
			}
		}, (long) (duration*20)-5);
	}
	
	@Override
	public String[] stringValues() {
		String[] values = {morph.name(), Double.toString(duration)};
		return values;
	}
}
