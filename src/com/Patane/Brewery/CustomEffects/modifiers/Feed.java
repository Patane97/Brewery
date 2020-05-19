package com.Patane.Brewery.CustomEffects.modifiers;

import java.util.Map;

import org.bukkit.entity.Player;

import com.Patane.Brewery.CustomEffects.Modifier;
import com.Patane.util.YAML.Namer;
import com.Patane.util.general.Check;

@Namer(name="feed")
public class Feed extends Modifier{
	final public double amount;

	public Feed(Map<String, String> fields){
		amount = Check.greaterThan(getDouble(fields, "amount"), 0, "Amount must be greater than 0.");
	}
	
	public Feed(double amount){
		this.amount = amount;
	}
	@Override
	public void modify(ModifierInfo info) {
		if(info.getTarget() instanceof Player){
			Player player = (Player) info.getTarget();
			player.setFoodLevel((int) Math.min(20, (player.getFoodLevel() + amount)));
		}
	}
}
