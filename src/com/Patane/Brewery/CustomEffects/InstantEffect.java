package com.Patane.Brewery.CustomEffects;

import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.potion.PotionEffect;

public class InstantEffect extends CustomEffect{
	
	public InstantEffect(String name, DamageContainer damageContainer, int radius, PotionEffect... potionEffects) {
		super(name, damageContainer, radius, potionEffects);
	}

	@Override
	public void execute(LivingEntity shooter, Location location) {
		executeOnEntities(shooter, location);
	}


	
}
