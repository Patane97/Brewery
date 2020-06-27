package com.Patane.Brewery.CustomEffects.modifiers;

import java.util.Map;

import com.Patane.Brewery.CustomEffects.Modifier;
import com.Patane.util.annotations.ClassDescriber;
import com.Patane.util.annotations.ParseField;
import com.Patane.util.general.Check;

@ClassDescriber(
		name="smite",
		desc="Smites a living entity with thunder.")
public class Smite extends Modifier{
	@ParseField(desc="Amount to damage the living entity for.")
	public double amount;
	
	public Smite() {
		super();
	}
	
	public Smite(Map<String, String> fields) {
		super(fields);
	}
	

	@Override
	protected void populateFields(Map<String, String> fields) {
		amount = Check.greaterThanEqual(getDouble(fields, "amount"), 0, "Amount must be greater than or equal to 0.");
	}
	
	public Smite(double amount) {
		this.amount = amount;
		construct();
	}

	/* 
	 * ================================================================================
	 */
	
	@Override
	public void modify(ModifierInfo info) {
		info.getTarget().getWorld().strikeLightningEffect(info.getTarget().getLocation());
		damage(info.getTarget(), info.getTargeter(), amount);
	}
}
