package com.Patane.Brewery.util.YML;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.Patane.util.YML.BasicYML;
import com.Patane.util.general.Messenger;

public abstract class BreweryYML extends BasicYML{

	public BreweryYML(Plugin plugin, String config, String root, String header) {
		super(plugin, config, root, header);
	}
	/**
	 * Retireves a potion effect from a YML and creates it based on the values given.
	 * @param section ConfigurationSection to grab potion effect from.
	 * @return New PotionEffect with given values. If there is an error, returns null and prints error message.
	 */
	public static PotionEffect retrievePotionEffect(ConfigurationSection section){
		String effectName = null;
		try{
			effectName = extractLast(section);
			PotionEffectType type = PotionEffectType.getByName(effectName);
			int duration = getIntFromString(section.getString("duration"));
			int strength = getIntFromString(section.getString("strength"));
			try{
				boolean ambient = section.getBoolean("ambient");
				try{
					boolean particles = section.getBoolean("particles");
					return new PotionEffect(type, duration, strength, ambient, particles);
				} catch (NullPointerException e){
					return new PotionEffect(type, duration, strength, ambient);
				}
			} catch(NullPointerException e){
				return new PotionEffect(type, duration, strength);
			}
		} catch (NullPointerException | NumberFormatException e){
			Messenger.warning("Potion Effect '"+effectName+"' retrieval has failed. Check all YML values are set correctly.");
			e.printStackTrace();
			return null;
		}
	}
	/**
	 * Checks if string is of valid format. Format is upper case with no spaces (Underscores replace spaces).
	 * @param string String to check.
	 * @return The string if it was of valid format.
	 * @throws IllegalArgumentException If the string is of invalid format.
	 */
	public static String safeFormatCheck(String string) throws IllegalArgumentException{
		if(!string.equals(string.replace(" ", "_").toUpperCase()))
			throw new IllegalArgumentException("String must be in upper case with no spacing, eg. '"+string.replace(" ", "_").toUpperCase()+"'");
		return string;
	}
}
