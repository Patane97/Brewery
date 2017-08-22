package com.Patane.Brewery.CustomEffects;

import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.potion.PotionEffect;

public class InstantEffect extends CustomEffect{
	
	public InstantEffect(String name, PlayerModifier modifier, int radius, PotionEffect... potionEffects) {
		super(name, modifier, radius, potionEffects);
	}

	@Override
	public void execute(LivingEntity shooter, Location location, EntityType[] hitableEntities) {
		executeOnEntities(shooter, location, hitableEntities);
	}


	
}
