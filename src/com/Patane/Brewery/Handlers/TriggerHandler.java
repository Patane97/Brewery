package com.Patane.Brewery.Handlers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.Patane.Brewery.CustomEffects.Trigger;
import com.Patane.Brewery.CustomEffects.triggers.Instant;
import com.Patane.Brewery.CustomEffects.triggers.Lingering;
import com.Patane.Brewery.CustomEffects.triggers.Sticky;
import com.Patane.handlers.PatHandler;
import com.Patane.util.YAML.Namer;
import com.Patane.util.general.Messenger;
import com.Patane.util.general.StringsUtil;

// *** Combine all Handler junk into 'PatHandler'. Formation/Modifier/Trigger handlers have the same code :(
public class TriggerHandler implements PatHandler{
	private static HashMap<String, Class< ? extends Trigger>> triggers;
	
	public static Class< ? extends Trigger> get(String trigger){
		for(String triggerName : triggers.keySet()){
			if(triggerName.equalsIgnoreCase(trigger))
				return triggers.get(triggerName);
		}
		return null;
	}
	public static void registerAll() {
		triggers = new HashMap<String, Class< ? extends Trigger>>();
//		Reflections reflections = new Reflections("com.Patane.Brewery.CustomEffects.types");
//		Set<Class<? extends Trigger>> allClasses = reflections.getSubTypesOf(Trigger.class);
//		for(Class<? extends Trigger> clazz : allClasses){
//			register(clazz);
//		}
		register(Instant.class);
		register(Lingering.class);
		register(Sticky.class);
		Messenger.debug("Registered Types: "+StringsUtil.stringJoiner(triggers.keySet(), ", "));
	}
	private static void register(Class< ? extends Trigger> triggerClass){
		Namer info = triggerClass.getAnnotation(Namer.class);
		if(info == null){
			Messenger.warning("Failed to register Trigger '"+triggerClass.getSimpleName()+".class': Missing annotation!");
			return;
		}
		triggers.put(info.name(), triggerClass);
	}
	public static List<String> getKeys() {
		return new ArrayList<String>(triggers.keySet());
	}
}
