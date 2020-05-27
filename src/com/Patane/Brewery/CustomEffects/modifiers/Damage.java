package com.Patane.Brewery.CustomEffects.modifiers;

import java.util.Map;

import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

import com.Patane.Brewery.CustomEffects.Modifier;
import com.Patane.util.YAML.Namer;
import com.Patane.util.general.Check;

@Namer(name="damage")
public class Damage extends Modifier{
	public DamageCause cause;
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
	
	public Damage(DamageCause cause, double amount){
		this.cause = cause;
		this.amount = amount;
		construct();
	}

	/* 
	 * ================================================================================
	 */
	
	@Override
	public void modify(ModifierInfo info) {
		if(amount > 0){
			if(info.getTargeter() != null){
				damage(info.getTarget(), info.getTargeter(), amount);
			}
			info.getTarget().damage(amount);
		}
	}
}
