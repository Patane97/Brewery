package com.Patane.Brewery.CustomEffects.modifiers;

import java.util.Map;

import org.bukkit.entity.Player;

import com.Patane.Brewery.CustomEffects.Modifier;
import com.Patane.util.annotations.ClassDescriber;
import com.Patane.util.annotations.ParseField;
import com.Patane.util.general.Check;

@ClassDescriber(
		name="feed",
		desc="Feeds a living player for a certain amount.")
public class Feed extends Modifier{
	@ParseField(desc="Amount of food given to the living player.")
	private double amount;
	
	public Feed() {
		super();
	}
	
	public Feed(Map<String, String> fields) {
		super(fields);
	}
	

	@Override
	protected void populateFields(Map<String, String> fields) {
		amount = Check.greaterThan(getDouble(fields, "amount"), 0, "Amount must be greater than 0.");
	}
	
	public Feed(double amount) {
		this.amount = amount;
		construct();
	}

	/* 
	 * ================================================================================
	 */
	
	@Override
	public void modify(ModifierInfo info) {
		if(info.getTarget() instanceof Player) {
			Player player = (Player) info.getTarget();
			player.setFoodLevel((int) Math.min(20, (player.getFoodLevel() + amount)));
		}
	}

}
