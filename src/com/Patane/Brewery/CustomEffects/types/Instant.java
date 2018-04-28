package com.Patane.Brewery.CustomEffects.types;

import java.util.List;

import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;

import com.Patane.Brewery.CustomEffects.BrEffect;
import com.Patane.Brewery.CustomEffects.EffectType;
import com.Patane.util.YML.Namer;

@Namer(name="INSTANT")
public class Instant extends EffectType{
	@Override
	public void execute(BrEffect effect, LivingEntity shooter, Location location) {
		List<LivingEntity> entitiesHit = executeOnEntities(effect, shooter, location);
		applyParticles(effect, location, entitiesHit);
		applySounds(effect, location);
	}
	
}
