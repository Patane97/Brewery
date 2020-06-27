package com.Patane.Brewery.CustomEffects.triggers;

import java.util.Map;

import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;

import com.Patane.Brewery.CustomEffects.BrEffect;
import com.Patane.Brewery.CustomEffects.Trigger;
import com.Patane.util.annotations.ClassDescriber;
import com.Patane.util.ingame.Focus;

@ClassDescriber(
		name="instant",
		desc="Instantly applies the Modifier to all living entities hit.")
public class Instant extends Trigger{
	
	public Instant() {
		super();
	}
	
	@Override
	protected void populateFields(Map<String, String> fields) {}

	/* 
	 * ================================================================================
	 */
	
	@Override
	public void execute(BrEffect effect, Location impact, LivingEntity executor) {
		applyByFocus(effect, impact, Focus.BLOCK);
		executeMany(effect, impact, executor);
	}
	@Override
	public void execute(BrEffect effect, LivingEntity executor, LivingEntity target) {
		applyByFocus(effect, target.getLocation(), Focus.BLOCK);
		executeMany(effect, executor, target);
	}
	
}
