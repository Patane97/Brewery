package com.Patane.Brewery.CustomEffects.formations;

import java.util.Map;

import org.bukkit.Location;

import com.Patane.Brewery.CustomEffects.BrEffect;
import com.Patane.Brewery.CustomEffects.Formation;
import com.Patane.util.YAML.Namer;
import com.Patane.util.general.Messenger;
import com.Patane.util.general.Messenger.Msg;

@Namer(name="RADIUS")
public class Radius extends Formation{
	
	public Radius() {
		super(Focus.BLOCK);
	}
	
	public Radius(Map<String, String> fields) {
		super(fields);
	}

	@Override
	public void form(BrEffect effect, Location location) {
		if(!effect.hasRadius()) {
			Messenger.send(Msg.WARNING, "'"+className()+"' Formation needs a radius to be formed.");
			return;
		}
		effect.getParticleEffect().spawn(location, effect.getRadius());
	}

}
