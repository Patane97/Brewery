package com.Patane.Brewery.CustomEffects;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.EntityType;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;

import com.Patane.Brewery.Brewery;
import com.Patane.Brewery.CustomEffects.BrEffect.BrParticleEffect;
import com.Patane.Brewery.CustomEffects.BrEffect.BrSoundEffect;
import com.Patane.Brewery.util.YML.BreweryYML;
import com.Patane.util.general.Check;
import com.Patane.util.general.ErrorHandler.YMLException;
import com.Patane.util.general.Messenger;
import com.Patane.util.general.Messenger.Msg;
import com.Patane.util.general.StringsUtil;

public class BrEffectYML extends BreweryYML{

	public BrEffectYML(Plugin plugin) {
		super(plugin, "effects.yml", "effects");
	}

	@Override
	public void save() {
		// Unfinished?
		for(BrEffect effect : Brewery.getEffectCollection().getAllItems()){
			String effectID = effect.getID();
			setHeader(createSection(effectID));
			//MODIFIER
			setHeader(clearCreateSection(effectID, "modifier"));
			header.set("type", effect.getModifier().name());
			for(Field field : effect.getModifier().getClass().getFields()){
				try {
					header.set(field.getName(), field.get(effect.getModifier()));
				} catch (IllegalArgumentException | IllegalAccessException e) {
					e.printStackTrace();
				}
			}
//			setHeader(itemName, "effects", effectContainer.getEffect().getID());
//			header.set("entities", YMLUtilities.getEntityTypeNames(effectContainer.getEntities()));
			
		}
		config.save();
//		for(BrEffect effect : Brewery.getEffectCollection().getAllItems()){
//			String effectName = effect.getName();
//			setHeader(createSection(effectName));
//			// TYPE
//			header.set("type", effect.getType().getClass().getAnnotation(EffectTypeInfo.class).name());
//			for(Field field : effect.getType().getClass().getFields()){
//				try {
//					header.set(field.getName(), field.get(effect.getType()));
//				} catch (IllegalArgumentException | IllegalAccessException e) {
//					e.printStackTrace();
//				}
//			}
//		}
	}

	@Override
	public void load() {
		setHeader(getRootSection());
		for(String effectName : header.getKeys(false)){
			load(getSection(header, effectName));
		}
		Messenger.info("Successfully loaded Effects: "+StringsUtil.stringJoiner(Brewery.getEffectCollection().getAllIDs(), ", "));
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
			// PROBLEM! 
			// Still needs to deal with a scenario where the effect can be in the baseHeader, 
			// but NOT the defaultHeader.
			// (eg. THUNDER from AXE_OF_THOR in items.yml).
			
			// Making sure the baseHeader is not null.
			Check.nulled(baseHeader);
			
			// If the defaultHeader is null, it should equal the baseHeader (attempting to fix problem above).
//			defaultHeader = (getSection(defaultHeader) == null ? baseHeader : defaultHeader);
			
			// Creating currentHeader to be used throughout this method.
			ConfigurationSection currentHeader = baseHeader;
			
			// Getting the effect name from the last portion of the baseHeader.
			String effectName = extractLast(baseHeader);
			
			// Checks if the effect's name is valid. Must be in upper case and with no spaces (Underscores replace spaces).
			safeFormatCheck(effectName);

			Messenger.debug(Msg.INFO, " + Effect["+effectName+"]");
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
			} catch (YMLException e){}
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
			
			EffectType trigger = null;
			// If this effect is allowed to be incomplete then this can be null.
			try{
				// Setting the modifier using the currentHeader, defaultHeader and getSimpleClassDefault method.
				trigger = getSimpleClassDefault(currentHeader, getSection(defaultHeader, "trigger"), EffectTypeHandler.get(triggerName), "type");
			} catch (YMLException e){}
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
			BrEffect effect = new BrEffect(incompleteAllowed, effectName, modifier, trigger, radius, entities.toArray(new EntityType[0]), particleEffect, soundEffect, potionEffects.toArray(new PotionEffect[0]));
			
			// If effect isnt already in the collection, it adds it.
			if(!Brewery.getEffectCollection().contains(effect.getID()))
				Brewery.getEffectCollection().add(effect);
			
			return effect;
		} catch (ClassNotFoundException e) {
			Messenger.debug(Msg.WARNING, "Effect could not be retrieved: Class error.");
			e.printStackTrace();
		} catch (YMLException e) {
			Messenger.debug(Msg.WARNING, "Effect could not be retrieved: YML error.");
			e.printStackTrace();
		}
		return null;
		
	}
	public void load(ConfigurationSection section){
		retrieve(section, null, true);
//		try{
//			setHeader(effectName);
//			Messenger.debug(Msg.INFO, "Attempting to load "+effectName+" effect...");
//			if(!effectName.equals(effectName.replace(" ", "_").toUpperCase()))
//				ErrorHandler.optionalLoadError(Msg.WARNING, Importance.REQUIRED, "Failed to load "+effectName+": Name must be in upper case with no spacing, eg. '"+effectName.replace(" ", "_").toUpperCase()+"'");
//			//MODIFIER
//			setHeader(effectName, "modifier");
//			String modifierName = header.getString("type");
//			Modifier modifier = getByClass(Importance.REQUIRED, ModifierHandler.get(modifierName), "Modifier", "the "+effectName+" effect", header, "type");
//			
//			//			Messenger.debug(Msg.INFO, "     + Modifier[#" + modifierName + "#, " + StringsUtil.stringJoiner(modifier.stringValues(), ", ")+"]");
//			//RADIUS
//			Integer radius = null;
//			setHeader(effectName);
//			if(header.getString("radius") != null){
//				radius = getIntFromString(header.getString("radius"));
//				if(radius != null)
//					Messenger.debug(Msg.INFO, "     + radius["+radius+"]");
//			}
//			//TRIGGER
//			EffectType effectType = null;
//			if(isSection(effectName, "trigger")){
//				setHeader(effectName, "trigger");
//				String effectTypeName = header.getString("type");
//				effectType = getByClass(Importance.MINOR, EffectTypeHandler.get(effectTypeName), "trigger", "the "+effectName+" effect's trigger", header, "type");
//			}
//			//ENTITIES
//			List<EntityType> entities = null;
//			if(isSection(effectName, "entities")){
//				entities = new ArrayList<EntityType>();
//				setHeader(effectName);
////				for(String entityName : header.getStringList("entities")){
////					try{
////						EntityType entityType = getEnumFromString(Importance.REQUIRED, EntityType.class, entityName, "entity type", "an entity for "+effectName+" effect");
////						entities.add(entityType);
////					} catch (LoadException e){
////						Messenger.warning(e.getMessage());
////					}
////				}
//			}
//			//PARTICLES
//			BrParticleEffect particleEffect = null;
//			if(isSection(effectName, "Particle"))
//				particleEffect = getByClass(Importance.MINOR, BrParticleEffect.class, "Particle", "the "+effectName+" effect's particle", getSection(effectName, "particle"));
//			//SOUNDS
//			BrSoundEffect soundEffect = null;
//			if(isSection(effectName, "Sound"))
//				soundEffect = getByClass(Importance.MINOR, BrSoundEffect.class, "Sound", "the "+effectName+" effect's sound", getSection(effectName, "sound"), "type");
//			//POTIONEFFECTS
//			List<PotionEffect> potionEffects = new ArrayList<PotionEffect>();
//			if(isSection(effectName, "potion_effects")){
//				setHeader(effectName, "potion_effects");
//				for(String potionName : header.getKeys(false)){
//					try{
//						setHeader(effectName, "potion_effects", potionName);
//						String preName = "the "+effectName+"'s "+potionName+" Effect";
//						// Type
//						PotionEffectType type = PotionEffectType.getByName(potionName);
//						if(type == null)
//							ErrorHandler.optionalLoadError(Msg.WARNING, Importance.MINOR, "Failed to load "+preName+": '"+potionName+"' not recognised as a valid PotionEffect.");
//						// Duration
//						int duration = Math.round(getFloatFromString(header.getString("duration"))*20);
//						// Strength
//						int strength = getIntFromString(header.getString("strength"));
//						Messenger.debug(Msg.INFO, "     + Potion Effect["+potionName+", "+duration+", "+strength+"]");
//						potionEffects.add(new PotionEffect(type, duration, strength));
//					} catch (LoadException e){
//						Messenger.warning(e.getMessage());
//					}
//				}
//			}
//			new BrEffect(effectName, modifier, particleEffect, soundEffect, new DefaultContainer(effectType, radius, (entities == null ? null : entities.toArray(new EntityType[0]))), potionEffects.toArray(new PotionEffect[0]));
//		} catch (LoadException e){
//			Messenger.warning(e.getMessage());
//		}
	}
}
