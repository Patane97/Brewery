package com.Patane.Brewery.CustomEffects.types;

import java.util.HashMap;

import com.Patane.Brewery.Messenger;

public class EffectTypeHandler{
	private static HashMap<String, Class<? extends EffectType>> types;
	
	public static Class<? extends EffectType> getCommand(String cmd){
		for(String commandName : types.keySet()){
			if(cmd.contains(commandName))
				return types.get(commandName);
		}
		return null;
	}
	public static void registerAll() {
		types = new HashMap<String, Class<? extends EffectType>>();
		register(Instant.class);
		register(Lingering.class);
	}
	public static void register(Class< ? extends EffectType> command){
		EffectTypeInfo cmdInfo = command.getAnnotation(EffectTypeInfo.class);
		if(cmdInfo == null) {
			Messenger.warning("A command is missing its attached EffectTypeInfo Annotation!");
			return;
		}
		types.put(cmdInfo.name(), command);

	}

}
