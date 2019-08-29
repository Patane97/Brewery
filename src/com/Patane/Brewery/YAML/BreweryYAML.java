package com.Patane.Brewery.YAML;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.EntityType;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.Patane.Brewery.CustomEffects.Filter.FilterGroup;
import com.Patane.util.YAML.types.YAMLEditable;
import com.Patane.util.general.Messenger;

public abstract class BreweryYAML extends YAMLEditable{

	public BreweryYAML(String fileName, String prefix, String... filePath) {
		super(fileName, prefix, filePath);
	}
	/**
	 * Posts a potion effect to a YML.
	 * @param section Base header section to post to.
	 * @param effect PotionEffect to post.
	 */
	public static void postPotionEffect(ConfigurationSection section, PotionEffect effect) {
		section = section.createSection(effect.getType().getName());
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
	public static PotionEffect retrievePotionEffect(ConfigurationSection section){
		String effectName = null;
		try{
			effectName = extractLast(section);
			PotionEffectType type = PotionEffectType.getByName(effectName);
			int duration = parseInt(section.getString("duration"));
			int amplifier = parseInt(section.getString("amplifier"));
			try{
				boolean ambient = parseBoolean(section.getString("ambient"));
				try{
					boolean particles = parseBoolean(section.getString("particles"));
					try {
						boolean icon = parseBoolean(section.getString("icon"));
						return new PotionEffect(type, duration, amplifier, ambient, particles, icon);
					} catch (Exception e) {
						return new PotionEffect(type, duration, amplifier, ambient, particles);
					}
				} catch (Exception e){
					return new PotionEffect(type, duration, amplifier, ambient);
				}
			} catch(Exception e){
				return new PotionEffect(type, duration, amplifier);
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
	
	public static void setFilterGroup(ConfigurationSection section, FilterGroup filterGroup) {
		
		if(!filterGroup.getEntities().isEmpty()) {
			
			// Converting EntityType's into String that are safe for YML
			List<String> entityNames = new ArrayList<String>();
			for(EntityType entityType : filterGroup.getEntities()) {
				entityNames.add(entityType.toString());
			}
			
			section.set("entities", entityNames);
		}
		if(!filterGroup.getPlayers().isEmpty())
			section.set("players", filterGroup.getPlayers());
		
		if(!filterGroup.getPermissions().isEmpty())
			section.set("permissions", filterGroup.getPermissions());
		
		if(!filterGroup.getTags().isEmpty())
			section.set("tags", filterGroup.getTags());
	}
	
	public static FilterGroup getFilterGroup(ConfigurationSection section, boolean defaultReturn) throws ClassNotFoundException, NullPointerException{
		if(section != null){
			List<EntityType> entities = new ArrayList<EntityType>();
			for(String entityName : section.getStringList("entities")){
				EntityType entityType = getEnumFromString(entityName, EntityType.class);
				if(entityType != null){
					entities.add(entityType);
				}
			}
			List<String> player = section.getStringList("players");
			List<String> permissions = section.getStringList("permissions");
			List<String> tags = section.getStringList("tags");
			return new FilterGroup(entities, player, permissions, tags, defaultReturn);
		}
		return new FilterGroup(null, null, null, null, defaultReturn);
	}
}
