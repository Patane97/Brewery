package com.Patane.Brewery;

import java.util.Map;

import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

public abstract class YMLParsable extends Nameable{
	
	protected YMLParsable(){};
	public YMLParsable(Map<String, String> fields){}
	
	protected int getInt(Map<String, String> fields, String name){
		String value = fields.get(name);
		int result;
		try{
			result = Integer.parseInt(value);
		} catch (NullPointerException e){
			throw new IllegalArgumentException("'"+name()+"' is missing the '"+name+"' field");
		} catch (NumberFormatException e){
			throw new IllegalArgumentException("'"+name()+"' has invalid value in '"+name+"' field (Value must be numerical)");
		}
		return result;
	}
	protected double getDouble(Map<String, String> fields, String name){
		String value = fields.get(name);
		double result;
		try{
			result = Double.parseDouble(value);
		} catch (NullPointerException e){
			throw new IllegalArgumentException("'"+name()+"' is missing the '"+name+"' field");
		} catch (NumberFormatException e){
			throw new IllegalArgumentException("'"+name()+"' has invalid value in '"+name+"' field (Value must be numerical)");
		}
		return result;
	}
	protected DamageCause getDamageCause(Map<String, String> fields, String name){
		String value = fields.get(name);
		DamageCause result;
		if(value == null)
			throw new IllegalArgumentException("'"+name()+"' is missing the '"+name+"' field");
		result = DamageCause.valueOf(value);
		if(result == null)
			throw new IllegalArgumentException("'"+name()+"' has invalid value in '"+name+"' field (Value must be a DamageCause type)");
		return result;
	}
}
