package com.Patane.Brewery.CustomEffects.modifiers;

import java.util.Map;

import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import com.Patane.Brewery.Namer;
import com.Patane.Brewery.CustomEffects.Modifier;

@Namer(name="FEED")
public class Feed extends Modifier{
	final private double amount;

	public Feed(Map<String, String> fields){
		amount = getDouble(fields, "amount");
	}
	
	public Feed(double amount){
		this.amount = amount;
	}
	@Override
	public void modify(LivingEntity feedee, Entity feeder) {
		if(feedee instanceof Player){
			Player player = (Player) feedee;
			player.setFoodLevel((int) Math.min(20, (player.getFoodLevel() + amount)));
		}
	}
}
