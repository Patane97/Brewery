package com.Patane.Brewery.CustomEffects.formations;

import org.bukkit.Location;

import com.Patane.Brewery.CustomEffects.BrEffect;
import com.Patane.Brewery.CustomEffects.Formation;
import com.Patane.util.YML.Namer;

@Namer(name="RADIUS")
public class Radius extends Formation{
	
	public Radius() {
		super(Focus.BLOCK);
	}

	@Override
	public void form(BrEffect effect, Location location) {
		effect.getParticleEffect().spawn(location, effect.getRadius());
	}

}
