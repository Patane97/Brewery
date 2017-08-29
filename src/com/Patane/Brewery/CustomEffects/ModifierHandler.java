package com.Patane.Brewery.CustomEffects;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.Patane.Brewery.Messenger;
import com.Patane.Brewery.Messenger.Msg;
import com.Patane.Brewery.Namer;
import com.Patane.Brewery.CustomEffects.modifiers.Damage;
import com.Patane.Brewery.CustomEffects.modifiers.Feed;
import com.Patane.Brewery.CustomEffects.modifiers.Heal;
import com.Patane.Brewery.CustomEffects.modifiers.Ignite;

public class ModifierHandler{
private static HashMap<String, Class< ? extends Modifier>> modifiers;
	
	public static Class< ? extends Modifier> get(String modifier){
		if(modifier == null)
			return null;
		for(String modName : modifiers.keySet()){
			if(modifier.contains(modName))
				return modifiers.get(modName);
		}
		return null;
	}
	public static void registerAll() {
		modifiers = new HashMap<String, Class< ? extends Modifier>>();
		register(Damage.class);
		register(Heal.class);
		register(Feed.class);;
		register(Ignite.class);
	}
	private static void register(Class< ? extends Modifier> modifierClass){
		Namer info = modifierClass.getAnnotation(Namer.class);
		if(info == null) {
			return;
		}
		modifiers.put(info.name(), modifierClass);
		Messenger.debug(Msg.INFO, "Registered "+info.name());
	}
	public static List<String> getKeys() {
		return new ArrayList<String>(modifiers.keySet());
	}
}
