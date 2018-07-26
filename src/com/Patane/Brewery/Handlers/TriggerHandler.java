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
import com.Patane.util.general.Messenger.Msg;
import com.Patane.util.general.StringsUtil;

public class TriggerHandler implements PatHandler{
	private static HashMap<String, Class< ? extends Trigger>> effectTypes;
	
	public static Class< ? extends Trigger> get(String effectType){
		if(effectType == null)
			return null;
		for(String effectName : effectTypes.keySet()){
			if(effectType.equals(effectName))
				return effectTypes.get(effectName);
		}
		return null;
	}
	public static void registerAll() {
		effectTypes = new HashMap<String, Class< ? extends Trigger>>();
//		Reflections reflections = new Reflections("com.Patane.Brewery.CustomEffects.types");
//		Set<Class<? extends Trigger>> allClasses = reflections.getSubTypesOf(Trigger.class);
//		for(Class<? extends Trigger> clazz : allClasses){
//			register(clazz);
//		}
		register(Instant.class);
		register(Lingering.class);
		register(Sticky.class);
		Messenger.debug(Msg.INFO, "Registered Types: "+StringsUtil.stringJoiner(effectTypes.keySet(), ", "));
	}
	private static void register(Class< ? extends Trigger> effectType){
		Namer info = effectType.getAnnotation(Namer.class);
		if(info == null){
			Messenger.warning("Failed to register Trigger '"+effectType.getSimpleName()+".class': Missing annotation!");
			return;
		}
		effectTypes.put(info.name(), effectType);
	}
	public static List<String> getKeys() {
		return new ArrayList<String>(effectTypes.keySet());
	}
}
