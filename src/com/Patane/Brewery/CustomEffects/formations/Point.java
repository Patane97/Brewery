package com.Patane.Brewery.CustomEffects.formations;

import org.bukkit.Location;

import com.Patane.Brewery.CustomEffects.BrEffect;
import com.Patane.Brewery.CustomEffects.Formation;
import com.Patane.util.YML.Namer;

@Namer(name="POINT")
public class Point extends Formation{
	
	public Point() {
		super(Focus.BLOCK);
	}

	@Override
	public void form(BrEffect effect, Location location) {
		effect.getParticleEffect().spawn(location, 0);
	}

}
