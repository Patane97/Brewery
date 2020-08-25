package com.Patane.Brewery.Handlers;

import java.io.File;
import java.io.FileFilter;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.core.util.Loader;
import org.bukkit.craftbukkit.libs.org.apache.commons.io.FilenameUtils;

import com.Patane.Brewery.Brewery;
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
import com.Patane.util.annotations.ClassDescriber;
import com.Patane.util.general.Messenger;
import com.Patane.util.general.StringsUtil;

public class ModifierHandler implements PatHandler{
	private static Map<String, Class< ? extends Modifier>> modifiers;
	
	public static Class< ? extends Modifier> get(String modifier) {
		for(String modifierName : modifiers.keySet()) {
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
		
		// Saves all files in array which have the ".jar" extension
		File[] modifierFiles = new File(Brewery.getInstance().getDataFolder(), "modifiers").listFiles(new FileFilter() {
			@Override
			public boolean accept(File file) {
				return (FilenameUtils.getExtension(file.getName()) == "jar" ? true : false);
			}
		});
		
		// Lists through each file
		for(File modifierJar : modifierFiles) {
			String fileName = FilenameUtils.getName(modifierJar.getName());
			try {
				URLClassLoader loader = new URLClassLoader(new URL[] {
						new URL(modifierJar.toURI().toURL().toString())
				});
				
				Class<?> cls = loader.loadClass(fileName);
				
				
			} catch (MalformedURLException e) {
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
		}
		
	}
	private static void register(Class< ? extends Modifier> modifierClass) {
		ClassDescriber info = modifierClass.getAnnotation(ClassDescriber.class);
		if(info == null) {
			Messenger.severe("Failed to register Modifier '"+modifierClass.getSimpleName()+".class': Missing annotation! Please contact plugin creator to fix this issue.");
			return;
		}
		modifiers.put(info.name(), modifierClass);
	}
	public static List<String> getKeys() {
		return new ArrayList<String>(modifiers.keySet());
	}
}
