package com.Patane.Brewery.CustomEffects.types;

import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;

import com.Patane.Brewery.CustomEffects.BrEffect;

@EffectTypeInfo(
	name="INSTANT"
)
public class Instant extends EffectType{

	@Override
	public void execute(BrEffect effect, LivingEntity shooter, Location location, EntityType[] hitableEntities) {
		executeOnEntities(effect, shooter, location, hitableEntities);
	}
	
}
