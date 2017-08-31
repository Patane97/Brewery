package com.Patane.Brewery.CustomEffects.types;

import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;

import com.Patane.Brewery.Namer;
import com.Patane.Brewery.CustomEffects.EffectType;
import com.Patane.Brewery.CustomItems.BrItem.EffectContainer;

@Namer(name="INSTANT")
public class Instant extends EffectType{
	@Override
	public void execute(EffectContainer container, LivingEntity shooter, Location location) {
		particles(container, location);
		sounds(container, location);
		executeOnEntities(container, shooter, location);
	}
	
}
