package com.Patane.Brewery.CustomEffects;

import java.util.List;
import java.util.Map;

import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;

import com.Patane.Brewery.Messenger;
import com.Patane.Brewery.YMLParsable;
import com.Patane.Brewery.CustomEffects.Modifier.ModifierInfo;
import com.Patane.Brewery.CustomItems.BrItem.EffectContainer;
import com.Patane.Brewery.util.LocationUtilities;

public abstract class EffectType extends YMLParsable{

	protected EffectType(){};
	public EffectType(Map<String, String> fields){}
	
	public abstract void execute(EffectContainer container, LivingEntity shooter, Location location);

	protected void applyModifiers(EffectContainer container, Location location, LivingEntity hitEntity, LivingEntity shooter){
		if(container.getEffect().hasModifier())
			container.getEffect().getModifier().modify(new ModifierInfo(hitEntity, shooter, location));
	}
	protected void applyParticles(EffectContainer container, Location location){
		if(container.getEffect().hasParticleEffect())
			container.getEffect().getParticleEffect().spawn(location, container.getRadius());
	}
	protected void applySounds(EffectContainer container, Location location){
		if(container.getEffect().hasSoundEffect())
			container.getEffect().getSoundEffect().spawn(location);
	}
	protected void executeOnEntities(EffectContainer container, LivingEntity shooter, Location location) {
		List<LivingEntity> hitEntities = LocationUtilities.getEntities(location, container.getRadius(), container.getEntities());
		for(LivingEntity hitEntity : hitEntities){
			hitEntity.addPotionEffects(container.getEffect().getPotionEffects());
			applyModifiers(container, location, hitEntity, shooter);
			// If they want particles/sounds at each entity hit
//			applyParticles(container, hitEntity.getEyeLocation());
//			applySounds(container, hitEntity.getEyeLocation());
			Messenger.debug(hitEntity, "&cAffected by &7"+container.getEffect().getName()+"&c effect.");
		}
	}
}
