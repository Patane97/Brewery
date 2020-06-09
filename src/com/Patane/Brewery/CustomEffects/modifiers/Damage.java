package com.Patane.Brewery.CustomEffects.modifiers;

import java.util.Map;

import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

import com.Patane.Brewery.CustomEffects.Modifier;
import com.Patane.util.annotations.ClassDescriber;
import com.Patane.util.annotations.FieldDescriber;
import com.Patane.util.general.Check;

@ClassDescriber(
		name="damage",
		desc="Damages a living entity for a certain amount.")
// *** Need to actually implement DamageCause!!!
public class Damage extends Modifier{
	@FieldDescriber(desc="Defines in which way the damage is being caused, such as through fire or poison.")
	public DamageCause cause;
	@FieldDescriber(desc="Amount of damage applied to the living entity.")
	public double amount;
	
	public Damage() {
		super();
	}
	
	public Damage(Map<String, String> fields) {
		super(fields);
	}
	
	@Override
	protected void populateFields(Map<String, String> fields) {
		cause = getEnumValue(DamageCause.class, fields, "cause");
		amount = Check.greaterThan(getDouble(fields, "amount"), 0, "Amount must be greater than 0.");
	}
	
	public Damage(DamageCause cause, double amount) {
		this.cause = cause;
		this.amount = amount;
		construct();
	}

	/* 
	 * ================================================================================
	 */
	
	@Override
	public void modify(ModifierInfo info) {
		if(amount > 0) {
			if(info.getTargeter() != null) {
				damage(info.getTarget(), info.getTargeter(), amount);
			}
			info.getTarget().damage(amount);
		}
	}
}
