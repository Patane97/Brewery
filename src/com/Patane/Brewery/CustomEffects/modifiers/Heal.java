package com.Patane.Brewery.CustomEffects.modifiers;

import java.util.Map;

import com.Patane.Brewery.CustomEffects.Modifier;
import com.Patane.util.annotations.ClassDescriber;
import com.Patane.util.annotations.ParseField;
import com.Patane.util.general.Check;

@ClassDescriber(
		name="heal",
		desc="Heals a living entity for a certain amount.")
public class Heal extends Modifier{
	@ParseField(desc="Amount of healing applied to the living entity.")
	private double amount;
	
	public Heal() {
		super();
	}
	
	public Heal(Map<String, String> fields) {
		super(fields);
	}
	
	
	@Override
	protected void populateFields(Map<String, String> fields) {
		amount = Check.greaterThan(getDouble(fields, "amount"), 0, "Amount must be greater than 0.");
	}
	
	public Heal(double amount) {
		this.amount = amount;
		construct();
	}

	/* 
	 * ================================================================================
	 */
	
	@Override
	public void modify(ModifierInfo info) {
		if(amount > 0)
			info.getTarget().setHealth(Math.min(20, info.getTarget().getHealth() + amount));
	}
}
