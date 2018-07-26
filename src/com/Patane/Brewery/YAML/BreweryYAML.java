package com.Patane.Brewery.YAML;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.EntityType;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.Patane.Brewery.CustomEffects.Filter.FilterGroup;
import com.Patane.util.YAML.types.YAMLEditable;
import com.Patane.util.general.Messenger;
import com.Patane.util.general.Messenger.Msg;
import com.Patane.util.general.StringsUtil;

public abstract class BreweryYAML extends YAMLEditable{

	public BreweryYAML(Plugin plugin, String config, String root, String header) {
		super(null, config, root, header);
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
			int duration = parseInt(section.getString("duration"));
			int strength = parseInt(section.getString("strength"));
			try{
				boolean ambient = parseBoolean(section.getString("ambient"));
				try{
					boolean particles = parseBoolean(section.getString("particles"));
					return new PotionEffect(type, duration, strength, ambient, particles);
				} catch (Exception e){
					return new PotionEffect(type, duration, strength, ambient);
				}
			} catch(Exception e){
				return new PotionEffect(type, duration, strength);
			}
		} catch (Exception e){
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
	
	public static FilterGroup getFilterGroup(ConfigurationSection section, boolean defaultReturn) throws ClassNotFoundException, NullPointerException{
		if(section != null){
			Messenger.debug(Msg.INFO, "    +--- "+extractLast(section)+": ");
			List<EntityType> entities = new ArrayList<EntityType>();
			for(String entityName : section.getStringList("entities")){
				EntityType entityType = getEnumFromString(entityName, EntityType.class);
				if(entityType != null){
					entities.add(entityType);
				}
			}
			if(!entities.isEmpty())Messenger.debug(Msg.INFO, "    +------ entities: "+StringsUtil.stringJoiner(section.getStringList("entities"), ", "));
			List<String> player = section.getStringList("players");
			if(!player.isEmpty())Messenger.debug(Msg.INFO, "    +------ players: "+StringsUtil.stringJoiner(player, ", "));
			List<String> permissions = section.getStringList("permissions");
			if(!permissions.isEmpty())Messenger.debug(Msg.INFO, "    +------ permissions: "+StringsUtil.stringJoiner(permissions, ", "));
			List<String> tags = section.getStringList("tags");
			if(!tags.isEmpty())Messenger.debug(Msg.INFO, "    +------ tags: "+StringsUtil.stringJoiner(tags, ", "));
			return new FilterGroup(entities, player, permissions, tags, defaultReturn);
		}
		return new FilterGroup(null, null, null, null, defaultReturn);
	}
}
