package com.Patane.Brewery.CustomEffects.formations;

import java.util.Map;

import org.bukkit.Location;

import com.Patane.Brewery.CustomEffects.BrEffect;
import com.Patane.Brewery.CustomEffects.Formation;
import com.Patane.util.YAML.Namer;

@Namer(name="POINT")
public class Point extends Formation{
	
	public Point() {
		super(Focus.BLOCK);
	}
	
	public Point(Map<String, String> fields) {
		super(fields);
	}

	@Override
	public void form(BrEffect effect, Location location) {
		effect.getParticleEffect().spawn(location, 0f);
	}

}
