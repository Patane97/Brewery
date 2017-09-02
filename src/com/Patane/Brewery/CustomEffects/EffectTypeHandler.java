package com.Patane.Brewery.CustomEffects;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.Patane.Brewery.Messenger;
import com.Patane.Brewery.Messenger.Msg;
import com.Patane.Brewery.Namer;
import com.Patane.Brewery.CustomEffects.types.Instant;
import com.Patane.Brewery.CustomEffects.types.Lingering;
import com.Patane.Brewery.util.StringUtilities;

public class EffectTypeHandler {
	private static HashMap<String, Class< ? extends EffectType>> effectTypes;
	
	public static Class< ? extends EffectType> get(String effectType){
		if(effectType == null)
			return null;
		for(String effectName : effectTypes.keySet()){
			if(effectType.contains(effectName))
				return effectTypes.get(effectName);
		}
		return null;
	}
	public static void registerAll() {
		effectTypes = new HashMap<String, Class< ? extends EffectType>>();
//		Reflections reflections = new Reflections("com.Patane.Brewery.CustomEffects.types");
//		Set<Class<? extends EffectType>> allClasses = reflections.getSubTypesOf(EffectType.class);
//		for(Class<? extends EffectType> clazz : allClasses){
//			register(clazz);
//		}
		register(Instant.class);
		register(Lingering.class);
		Messenger.debug(Msg.INFO, "Registered Types: "+StringUtilities.stringJoiner(effectTypes.keySet(), ", "));
	}
	private static void register(Class< ? extends EffectType> effectType){
		Namer info = effectType.getAnnotation(Namer.class);
		if(info == null){
			Messenger.warning("Failed to register EffectType '"+effectType.getSimpleName()+".class': Missing annotation!");
			return;
		}
		effectTypes.put(info.name(), effectType);
	}
	public static List<String> getKeys() {
		return new ArrayList<String>(effectTypes.keySet());
	}
}
