package com.Patane.Brewery.CustomEffects;

import java.util.Map;

import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.metadata.FixedMetadataValue;

import com.Patane.Brewery.Brewery;
import com.Patane.Brewery.YMLParsable;

public abstract class Modifier extends YMLParsable{
	
	protected Modifier(){};
	public Modifier(Map<String, String> fields){}
	
	public abstract void modify(LivingEntity entity1, Entity entity2);
	
	public void damage(LivingEntity damagee, Entity damager, double amount){
		damagee.setMetadata("Brewery_DAMAGE", new FixedMetadataValue(Brewery.getInstance(), null));
		damagee.damage(amount, damager);
	}
}
