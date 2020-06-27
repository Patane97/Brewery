package com.Patane.Brewery.YAML;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.EntityType;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.Patane.Brewery.CustomEffects.Filter.FilterType;
import com.Patane.util.YAML.types.YAMLEditable;
import com.Patane.util.general.Messenger;

public abstract class BreweryYAML extends YAMLEditable{

	public BreweryYAML(String fileName, String prefix, String... filePath) {
		super(fileName, prefix, filePath);
	}
	
	public static void postPotionEffect(ConfigurationSection section, PotionEffect effect) {
		postPotionEffect(section, 0, effect);
	}
	/**
	 * Posts a potion effect to a YML.
	 * @param section Base header section to post to.
	 * @param effect PotionEffect to post.
	 */
	public static void postPotionEffect(ConfigurationSection section, int a, PotionEffect effect) {
		String effectName = effect.getType().getName();
		
		a = (a < 0 ? 0 : a);
		
		if(a != 0)
			effectName = String.format("%s(%d)", effectName, a);
		
		section = section.createSection(effectName);
		section.set("duration", effect.getDuration());
		section.set("amplifier", effect.getAmplifier());
		if(!effect.isAmbient())
			section.set("ambient", effect.isAmbient());
		if(!effect.hasParticles())
			section.set("particles", effect.hasParticles());
		if(!effect.hasIcon())
			section.set("icon", effect.hasParticles());
	}
	/**
	 * Retireves a potion effect from a YML and creates it based on the values given.
	 * @param section ConfigurationSection to grab potion effect from.
	 * @return New PotionEffect with given values. If there is an error, returns null and prints error message.
	 */
	public static PotionEffect retrievePotionEffect(ConfigurationSection section) {
		String effectName = null;
		String effectNameRaw = null;
		Pattern namePattern = Pattern.compile("(\\w+).*(?=\\(\\d\\))");
		Matcher nameMatch;
		try{
			// Grabbing the raw effect name before removing any "(/d)"s
			effectNameRaw = extractLast(section);
			// Generating the matcher from pattern
			nameMatch = namePattern.matcher(effectNameRaw);
			
			// If it has a match
			if(nameMatch.find())
				// Save the name as the group
				effectName = nameMatch.group();
			// Otherwise, save the whole thing as the name
			else
				effectName = effectNameRaw;
			
			PotionEffectType type = PotionEffectType.getByName(effectName);
			int duration = parseInt(section.getString("duration"));
			int amplifier = parseInt(section.getString("amplifier"));
			
			// For ambient, particles and icon, the default values are set, THEN we check if the actual value is supplied.
			// This way, they can provide 'particles' without providing 'ambient' and not have any errors.
			boolean ambient = true;
			if(section.contains("ambient"))
				ambient = parseBoolean(section.getString("ambient"));

			boolean particles = true;
			if(section.contains("particles"))
				ambient = parseBoolean(section.getString("particles"));

			boolean icon = true;
			if(section.contains("icon"))
				ambient = parseBoolean(section.getString("icon"));
			
			
			return new PotionEffect(type, duration, amplifier, ambient, particles, icon);
		} catch (Exception e) {
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
	
	public static void setFilterType(ConfigurationSection section, FilterType filterType) {
		
		if(!filterType.getEntities().isEmpty()) {
			
			// Converting EntityType's into String that are safe for YML
			List<String> entityNames = new ArrayList<String>();
			for(EntityType entityType : filterType.getEntities()) {
				entityNames.add(entityType.toString());
			}
			
			section.set("entities", entityNames);
		}
		if(!filterType.getPlayers().isEmpty())
			section.set("players", filterType.getPlayers());
		
		if(!filterType.getPermissions().isEmpty())
			section.set("permissions", filterType.getPermissions());
		
		if(!filterType.getTags().isEmpty())
			section.set("tags", filterType.getTags());
	}
	
	public static FilterType getFilterType(ConfigurationSection section, boolean defaultReturn) throws ClassNotFoundException, NullPointerException{
		if(section != null) {
			List<EntityType> entities = new ArrayList<EntityType>();
			for(String entityName : section.getStringList("entities")) {
				EntityType entityType = getEnumFromString(entityName, EntityType.class);
				if(entityType != null) {
					entities.add(entityType);
				}
			}
			List<String> player = section.getStringList("players");
			List<String> permissions = section.getStringList("permissions");
			List<String> tags = section.getStringList("tags");
			return new FilterType(entities, player, permissions, tags, defaultReturn);
		}
		return new FilterType(null, null, null, null, defaultReturn);
	}
}
