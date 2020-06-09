package com.Patane.Brewery.CustomEffects.modifiers;

import java.util.Map;

import com.Patane.Brewery.CustomEffects.Modifier;
import com.Patane.util.annotations.ClassDescriber;
import com.Patane.util.annotations.FieldDescriber;
import com.Patane.util.general.Check;

@ClassDescriber(
		name="ignite",
		desc="Ignites a living entity for a certain duration.")
public class Ignite extends Modifier{
	@FieldDescriber(desc="Duration the living entity is ignited for. Measured in server ticks (1s = 20ticks).")
	public int duration;
	
	public Ignite() {
		super();
	}
	
	public Ignite(Map<String, String> fields) {
		super(fields);
	}
	

	@Override
	protected void populateFields(Map<String, String> fields) {
		duration = Math.round(Check.greaterThan((float) getDouble(fields, "duration"), 0, "Duration must be greater than 0."));
	}
	
	public Ignite(int duration) {
		this.duration = duration;
		construct();
	}

	/* 
	 * ================================================================================
	 */

	@Override
	protected void valueConverts() {
		customValueConverter.put("duration", i -> (int)i+" ticks");
	}

	/* 
	 * ================================================================================
	 */
	
	@Override
	public void modify(ModifierInfo info) {
		info.getTarget().setFireTicks(duration);
	}
}
