package com.Patane.Brewery.CustomEffects;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.Patane.Brewery.Messenger;
import com.Patane.Brewery.Messenger.Msg;
import com.Patane.Brewery.Namer;
import com.Patane.Brewery.CustomEffects.modifiers.Damage;
import com.Patane.Brewery.CustomEffects.modifiers.Feed;
import com.Patane.Brewery.CustomEffects.modifiers.Force;
import com.Patane.Brewery.CustomEffects.modifiers.Heal;
import com.Patane.Brewery.CustomEffects.modifiers.Ignite;
import com.Patane.Brewery.CustomEffects.modifiers.Kill;
import com.Patane.Brewery.CustomEffects.modifiers.Polymorph;
import com.Patane.Brewery.CustomEffects.modifiers.Smite;
import com.Patane.Brewery.util.StringUtilities;

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
		register(Feed.class);
		register(Ignite.class);
		register(Smite.class);
		register(Force.class);
		register(Polymorph.class);
		register(Kill.class);
		Messenger.debug(Msg.INFO, "Registered Modifiers: "+StringUtilities.stringJoiner(modifiers.keySet(), ", "));
	}
	private static void register(Class< ? extends Modifier> modifierClass){
		Namer info = modifierClass.getAnnotation(Namer.class);
		if(info == null){
			Messenger.warning("Failed to register Modifier '"+modifierClass.getSimpleName()+".class': Missing annotation!");
			return;
		}
		modifiers.put(info.name(), modifierClass);
	}
	public static List<String> getKeys() {
		return new ArrayList<String>(modifiers.keySet());
	}
}
