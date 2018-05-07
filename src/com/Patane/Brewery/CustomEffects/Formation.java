package com.Patane.Brewery.CustomEffects;

import org.bukkit.Location;

import com.Patane.util.ingame.Focusable;

public abstract class Formation extends Focusable{
	
	public Formation(Focus focus) {
		super(focus);
	}

	public abstract void form(BrEffect effect, Location location);

	
}
