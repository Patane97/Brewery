package com.Patane.Brewery.CustomEffects.triggers;

import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;

import com.Patane.Brewery.CustomEffects.BrEffect;
import com.Patane.Brewery.CustomEffects.Trigger;
import com.Patane.util.YML.Namer;
import com.Patane.util.ingame.Focusable.Focus;

@Namer(name="INSTANT")
public class Instant extends Trigger{
	@Override
	public void execute(BrEffect effect, Location impact, LivingEntity executor) {
		applyByFocus(effect, impact, Focus.BLOCK);
		executeOnEntities(effect, impact, executor);
	}
	
}
