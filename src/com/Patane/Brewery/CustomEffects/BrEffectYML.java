package com.Patane.Brewery.CustomEffects;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.EntityType;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;

import com.Patane.Brewery.Brewery;
import com.Patane.Brewery.Collections.BrEffectCollection;
import com.Patane.Brewery.CustomEffects.BrEffect.BrParticleEffect;
import com.Patane.Brewery.CustomEffects.BrEffect.BrSoundEffect;
import com.Patane.Brewery.CustomEffects.BrEffect.BrTag;
import com.Patane.Brewery.Handlers.ModifierHandler;
import com.Patane.Brewery.Handlers.TriggerHandler;
import com.Patane.Brewery.util.YML.BreweryYML;
import com.Patane.handlers.ErrorHandler.YMLException;
import com.Patane.util.general.Check;
import com.Patane.util.general.Messenger;
import com.Patane.util.general.Messenger.Msg;
import com.Patane.util.general.StringsUtil;

public class BrEffectYML extends BreweryYML{

	public BrEffectYML(Plugin plugin) {
		super(plugin, "effects.yml", "effects", "YML File for each effect\nExample:");
	}

	@Override
	public void save() {}

	@Override
	public void load() {
		setHeader(getRootSection());
		for(String effectName : header.getKeys(false)){
			load(getSectionAndWarn(header, effectName));
		}
		Messenger.info("Successfully loaded Effects: "+StringsUtil.stringJoiner(Brewery.getEffectCollection().getAllIDs(), ", "));
	}
	
	public void load(ConfigurationSection section){
		retrieve(section, null, true);
	}

	/**
	 * This function will extract a BrEffect from a YML file.
	 * If the BrEffect is not complete, a default effect can be applied to fill in any null values.
	 * 
	 * @param baseHeader The root where the effect's YML structure begins
	 * @param defaultHeader The default root that will be used to fill any values that could not be determined/found in the YML. If this is set to null, then the YML MUST provide all details, or the effect's retrieval will fail.
	 * @param incompleteAllowed Whether essential values (such as Trigger) can be missing. Generally true if this is retireving a default effect.
	 */
	public static BrEffect retrieve(ConfigurationSection baseHeader, ConfigurationSection defaultHeader, boolean incompleteAllowed){
		try{			
			// Making sure the baseHeader is not null.
			Check.nulled(baseHeader);
			
			// Creating currentHeader to be used throughout this method.
			ConfigurationSection currentHeader = baseHeader;
			
			// Getting the effect name from the last portion of the baseHeader.
			String effectName = extractLast(baseHeader);
			
			// Checks if the effect's name is valid. Must be in upper case and with no spaces (Underscores replace spaces).
			safeFormatCheck(effectName);
			
			Messenger.debug(Msg.INFO, " + Effect["+effectName+"]");
			
			// Adding effect to the processing list. This avoids any infinite loops of processing effects.
			BrEffectCollection.addProcessing(effectName);
			
			/*
			 * ==================> MODIFIER <==================
			 */
			
			// Setting currentHeader to the baseHeader's modifier.
			// If this is unavailable, then currentHeader is set to the defaultSection's modifier.
			currentHeader = getAvailable(getSection(baseHeader, "modifier"), getSection(defaultHeader, "modifier"));
			
			// Getting modifierName value from either base or default headers, depending on whats available.
			String modifierName = getStringDefault("type", currentHeader, getSection(defaultHeader, "modifier"));
			
			Modifier modifier = null;
			// If this effect is allowed to be incomplete then this can be null.
			try{
				// Setting the modifier using the currentHeader, defaultHeader and getSimpleClassDefault method.
				modifier = getSimpleClassDefault(currentHeader, getSection(defaultHeader, "modifier"), ModifierHandler.get(modifierName), "type");
			} catch (YMLException e){
			} catch (ClassNotFoundException e){
				throw new ClassNotFoundException("Type required for 'modifier' is missing.");
			}
			
			/*
			 * ==================> RADIUS <==================
			 */

			// Setting currentHeader to the baseHeader.
			// If this doesnt have a radius, then currentHeader is set to the defaultSection.
			// If defaultSection doesnt have a radius, then currentHeader is null.
			currentHeader = getAvailableWithSet("radius", getSection(baseHeader), getSection(defaultHeader));
			
			// Setting radiusStr to current or default header's radius.
			String radiusStr = getStringDefault("radius", currentHeader, defaultHeader);
			
			// If the radiusStr given (isnt null), then attempt to turn the Str into an Integer.
			Integer radius = null;
			if(radiusStr != null){
				radius = getIntFromString(radiusStr);
				if(radius != null) Messenger.debug(Msg.INFO, "    + Radius: "+radius);
			}
			
			/*
			 * ==================> TRIGGER <==================
			 */
			
			// Setting currentHeader to the baseHeader's trigger.
			// If this is unavailable, then currentHeader is set to the defaultSection's trigger.
			currentHeader = getAvailable(getSection(baseHeader, "trigger"), getSection(defaultHeader, "trigger"));
			
			// Getting triggerName value from either base or default headers, depending on whats available.
			String triggerName = getStringDefault("type", currentHeader, getSection(defaultHeader, "trigger"));
			
			Trigger trigger = null;
			// If this effect is allowed to be incomplete then this can be null.
			try{
				// Setting the modifier using the currentHeader, defaultHeader and getSimpleClassDefault method.
				trigger = getSimpleClassDefault(currentHeader, getSection(defaultHeader, "trigger"), TriggerHandler.get(triggerName), "type");
			} catch (YMLException e){
			} catch (ClassNotFoundException e){
				throw new ClassNotFoundException("Type required for 'trigger' is missing.");
			}
			
			/*
			 * ==================> ENTITIES <==================
			 */			
			// Setting currentHeader to the baseHeader.
			// If this doesnt have entities, then currentHeader is set to the defaultSection.
			// If defaultSection doesnt have entities, then currentHeader is null.
			currentHeader = getAvailableWithSet("entities", getSection(baseHeader), getSection(defaultHeader));
			
			// Creates a new ArrayList for the entities
			List<EntityType> entities = new ArrayList<EntityType>();
			
			// If either the base or the default headers have entities, then they are added (base taking priority).
			if(currentHeader != null){
				Messenger.debug(Msg.INFO, "    + Entities: ");
				
				// Loops through each entitiy on the list.
				for(String entityName : currentHeader.getStringList("entities")){
					// This is within a try/catch because it is optional.
					// If it failed, we dont want to halt the entire retrieval process.
					try{
						
						// Finds and matches the given entityName with an EntityType Enum.
						EntityType entityType = getEnumFromString(entityName, EntityType.class);
						Messenger.debug(Msg.INFO, "    +---"+entityType.name());
						
						// If entityType isnt null, adds it to the entities ArrayList.
						if(entityType != null) entities.add(entityType);
						
					// Throws a NullPointerException if the entityname is null or object is null.
					} catch(NullPointerException e){}
				}
			}
			/*
			 * ==================> TAGS <==================
			 */
			
			// Setting currentHeader to the baseHeader's tags.
			// If this is unavailable, then currentHeader is set to the defaultSection's tags.
			currentHeader = getAvailable(getSection(baseHeader, "tag"), getSection(defaultHeader, "tag"));
			
			// BrTag is null if there are no particles in the base or default headers.
			BrTag tag = null;

			// If either the base or the default headers have a particle, then its added (base taking priority).
			if(currentHeader != null){
				// This is within a try/catch because it is optional.
				// If it failed, we dont want to halt the entire retrieval process.
				try{

					// Setting the particle effect using the currentHeader, defaultHeader and getSimpleClassDefault method.
					tag = getSimpleClassDefault(currentHeader, getSection(defaultHeader, "tag"), BrTag.class);
				} 
				
				// Generally ClassNotFoundException (class is null) or YMLException (currentHeader is null).
				catch(Exception e){
					Messenger.warning("Failed to retrieve "+effectName+" tag:");
					e.printStackTrace();
				}
			}
			
			/*
			 * ==================> PARTICLES <==================
			 */
			
			// Setting currentHeader to the baseHeader's particles.
			// If this is unavailable, then currentHeader is set to the defaultSection's particles.
			currentHeader = getAvailable(getSection(baseHeader, "particle"), getSection(defaultHeader, "particle"));
			
			// ParticleEffect is null if there are no particles in the base or default headers.
			BrParticleEffect particleEffect = null;

			// If either the base or the default headers have a particle, then its added (base taking priority).
			if(currentHeader != null){
				// This is within a try/catch because it is optional.
				// If it failed, we dont want to halt the entire retrieval process.
				try{

					// Setting the particle effect using the currentHeader, defaultHeader and getSimpleClassDefault method.
					particleEffect = getSimpleClassDefault(currentHeader, getSection(defaultHeader, "particle"), BrParticleEffect.class);
				} 
				
				// Generally ClassNotFoundException (class is null) or YMLException (currentHeader is null).
				catch(Exception e){
					Messenger.warning("Failed to retrieve "+effectName+" particle effect:");
					e.printStackTrace();
				}
			}
			
			/*
			 * ==================> SOUNDS <==================
			 */
			// Setting currentHeader to the baseHeader's sounds.
			// If this is unavailable, then currentHeader is set to the defaultSection's sounds.
			currentHeader = getAvailable(getSection(baseHeader, "sound"), getSection(defaultHeader, "sound"));
			
			// SoundEffect is null if there are no sounds in the base or default headers.
			BrSoundEffect soundEffect = null;

			// If either the base or the default headers have a sound, then its added (base taking priority).
			if(currentHeader != null){
				// This is within a try/catch because it is optional.
				// If it failed, we dont want to halt the entire retrieval process.
				try{

					// Setting the sound effect using the currentHeader, defaultHeader and getSimpleClassDefault method.
					soundEffect = getSimpleClassDefault(currentHeader, getSection(defaultHeader, "sound"), BrSoundEffect.class);
				} 
				
				// Generally ClassNotFoundException (class is null) or YMLException (currentHeader is null).
				catch(Exception e){
					Messenger.warning("Failed to retrieve "+effectName+" sound effect:");
					e.printStackTrace();
				}
			}
			
			/*
			 * ==================> POTION EFFECTS <==================
			 */

			// Setting currentHeader to the baseHeader's potion effects.
			// If this is unavailable, then currentHeader is set to the defaultSection's potion effects.
			currentHeader = getAvailable(getSection(baseHeader, "potion_effects"), getSection(defaultHeader, "potion_effects"));
			
			// Creates a new ArrayList for the entities
			List<PotionEffect> potionEffects = new ArrayList<PotionEffect>();
			
			// If either the base or the default headers have potion effects, then they are added (base taking priority).
			if(currentHeader != null){
				Messenger.debug(Msg.INFO, "    + Potion Effects: ");
				
				// Loops through each potion effect on the list.
				for(String potionName : currentHeader.getKeys(false)){
					// This is within a try/catch because it is optional.
					// If it failed, we dont want to halt the entire retrieval process.
					try{
						
						// Attempts to retireve the potion effect from the given section.
						PotionEffect potionEffect = retrievePotionEffect(getSection(currentHeader, potionName));
						
						// Prints relevant potion effect information for debugging.
						Messenger.debug(Msg.INFO,"    +---"+potionName);
						Messenger.debug(Msg.INFO,"    +-----[duration: "+potionEffect.getDuration()+"]");
						Messenger.debug(Msg.INFO,"    +-----[strength: "+potionEffect.getAmplifier()+"]");
						Messenger.debug(Msg.INFO,"    +-----[ambient: "+potionEffect.isAmbient()+"]");
						Messenger.debug(Msg.INFO,"    +-----[particles: "+potionEffect.hasParticles()+"]");
						
						// If the potion effect is not null, adds it to the potion effects ArrayList.
						if(potionEffect != null) potionEffects.add(potionEffect);
						
					// Throws a NullPointerException if the potionEffect is null and debugging is on.
					} catch(NullPointerException e){
						Messenger.warning("Failed to retrieve "+effectName+" potion effect '"+potionName+"':");
						e.printStackTrace();
					}
				}
			}
			BrEffect effect = new BrEffect(incompleteAllowed, effectName, modifier, trigger, radius, 
					entities.toArray(new EntityType[0]), particleEffect, soundEffect, potionEffects.toArray(new PotionEffect[0]), 
					tag);

			// Removing effect to the processing list. This avoids any infinite loops of processing effects.
			BrEffectCollection.delProcessing(effectName);
			
			// If effect isnt already in the collection, it adds it.
			if(!Brewery.getEffectCollection().contains(effect.getID()))
				Brewery.getEffectCollection().add(effect);
			
			return effect;
		} catch(YMLException e){
			Messenger.warning("An effect failed to be found and loaded:");
			e.printStackTrace();
		} catch (Exception e) {
			Messenger.warning("'"+extractLast(baseHeader)+"' Effect failed to load:");
			e.printStackTrace();
		}
		return null;
		
	}
}
