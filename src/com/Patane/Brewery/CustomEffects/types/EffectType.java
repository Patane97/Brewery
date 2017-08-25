package com.Patane.Brewery.CustomEffects.types;

import java.util.ArrayList;

import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;

import com.Patane.Brewery.CustomEffects.BrEffect;
import com.Patane.Brewery.util.LocationUtilities;

public abstract class EffectType {
	
	public abstract void execute(BrEffect effect, LivingEntity shooter, Location location, EntityType[] hitableEntities);
	
	protected void executeOnEntities(BrEffect effect, LivingEntity shooter, Location location, EntityType[] hitableEntities) {
		ArrayList<LivingEntity> hitEntities = LocationUtilities.getEntities(location, effect.getRadius(), hitableEntities);
		for(LivingEntity hitEntity : hitEntities){
			hitEntity.addPotionEffects(effect.getPotionEffects());
			effect.getModifier().modify(hitEntity, shooter);
		}
	}
}
