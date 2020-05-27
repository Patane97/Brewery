package com.Patane.Brewery.Handlers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.Patane.Brewery.CustomEffects.Modifier;
import com.Patane.Brewery.CustomEffects.modifiers.Damage;
import com.Patane.Brewery.CustomEffects.modifiers.Effect;
import com.Patane.Brewery.CustomEffects.modifiers.Feed;
import com.Patane.Brewery.CustomEffects.modifiers.Force;
import com.Patane.Brewery.CustomEffects.modifiers.Heal;
import com.Patane.Brewery.CustomEffects.modifiers.Ignite;
import com.Patane.Brewery.CustomEffects.modifiers.Kill;
import com.Patane.Brewery.CustomEffects.modifiers.None;
import com.Patane.Brewery.CustomEffects.modifiers.Polymorph;
import com.Patane.Brewery.CustomEffects.modifiers.Smite;
import com.Patane.handlers.PatHandler;
import com.Patane.util.YAML.Namer;
import com.Patane.util.general.Messenger;
import com.Patane.util.general.StringsUtil;

public class ModifierHandler implements PatHandler{
	private static Map<String, Class< ? extends Modifier>> modifiers;
	
	public static Class< ? extends Modifier> get(String modifier){
		for(String modifierName : modifiers.keySet()){
			if(modifierName.equalsIgnoreCase(modifier))
				return modifiers.get(modifierName);
		}
		return null;
	}
	public static void registerAll() {
		modifiers = new HashMap<String, Class< ? extends Modifier>>();
		register(None.class);
		register(Damage.class);
		register(Heal.class);
		register(Feed.class);
		register(Ignite.class);
		register(Smite.class);
		register(Force.class);
		register(Polymorph.class);
		register(Kill.class);
		register(Effect.class);
		Messenger.debug("Registered Modifiers: "+StringsUtil.stringJoiner(modifiers.keySet(), ", "));
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
