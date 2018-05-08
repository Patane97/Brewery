package com.Patane.Brewery.CustomEffects.formations;

import org.bukkit.Location;

import com.Patane.Brewery.CustomEffects.BrEffect;
import com.Patane.Brewery.CustomEffects.Formation;
import com.Patane.util.YML.Namer;

@Namer(name="ENTITY")
public class Entity extends Formation{
	
	public Entity() {
		super(Focus.ENTITY);
	}
	
	@Override
	public void form(BrEffect effect, Location location) {
		// Location will always be 'entity.getEyeLocation()' as Focus system does not allow a non-entity location to be passed.
		effect.getParticleEffect().spawn(location, 1);
	}

}
