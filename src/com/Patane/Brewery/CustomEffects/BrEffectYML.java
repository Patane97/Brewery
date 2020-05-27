package com.Patane.Brewery.CustomEffects;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.potion.PotionEffect;
import org.yaml.snakeyaml.error.YAMLException;

import com.Patane.Brewery.Brewery;
import com.Patane.Brewery.Collections.BrEffectCollection;
import com.Patane.Brewery.CustomEffects.BrEffect.BrParticleEffect;
import com.Patane.Brewery.CustomEffects.BrEffect.BrSoundEffect;
import com.Patane.Brewery.CustomEffects.BrEffect.BrTag;
import com.Patane.Brewery.CustomEffects.Filter.FilterType;
import com.Patane.Brewery.CustomEffects.Filter.FilterTypes;
import com.Patane.Brewery.Handlers.ModifierHandler;
import com.Patane.Brewery.Handlers.TriggerHandler;
import com.Patane.Brewery.YAML.BreweryYAML;
import com.Patane.util.general.Check;
import com.Patane.util.general.Messenger;
import com.Patane.util.general.StringsUtil;
import com.sun.istack.internal.NotNull;

public class BrEffectYML extends BreweryYAML{
	private static BrEffectYML instance;
	public BrEffectYML() {
		super("effects", "effects");
		instance = this;
	}

	@Override
	public void save() throws IllegalStateException {
		List<String> savedNames = new ArrayList<String>();
		setSelect(getPrefix());
		for(BrEffect effect : Brewery.getEffectCollection().getAllItems()) {
			if(save(effect))
				savedNames.add(effect.getName());
		}
		if(configHandler.saveConfig())
			Messenger.info("Successfully saved Effects: "+StringsUtil.stringJoiner(savedNames, ", "));
	}

	@Override
	public void load() throws IllegalStateException {
		List<String> loadedNames = new ArrayList<String>();
		setSelect(getPrefix());
		for(String effectName : getSelect().getKeys(false)) {
			BrEffect effect = load(getSectionAndWarn(getSelect(), effectName));
			if(effect != null)
				loadedNames.add(effect.getName());
		}
		Messenger.info("Successfully loaded Effects: "+StringsUtil.stringJoiner(loadedNames, ", "));
	}
	
	public boolean save(BrEffect effect) throws IllegalStateException {
		if(post(getPrefix(), effect, null, null)) {
			configHandler.saveConfigQuietly();
			return true;
		}
		return false;
	}
	
	public BrEffect load(ConfigurationSection section) {
		return retrieve(section, null);
	}

	/**
	 * This function will save an effect to a YML location.
	 * If a default YML location & effect is provided then it will save any common values between the effect and defaulteffect in the default location.
	 * eg. If effect has a radius=5 and defaultEffect has radius=3, then radius=5 will be saved onlyin baseHeader location.
	 *     Otherwise, if effect has a radius=4 and defaultEffect has radius=4, then radius=4 will be saved only in the defaultHeader location.
	 * 
	 * @param baseHeader
	 * @param defaultHeader
	 * @param effect
	 * @param defaultEffect
	 */
	public static boolean post(@NotNull ConfigurationSection baseHeader, @NotNull BrEffect effect, ConfigurationSection defaultHeader, BrEffect defaultEffect) {
		try {
			Check.notNull(baseHeader);
			if(defaultHeader == null)
				defaultHeader = baseHeader;
			Check.notNull(effect);
			if(defaultEffect == null)
				defaultEffect = effect;
			
			Messenger.debug("Posting effect: "+effect.getName());
			ConfigurationSection currentHeader = baseHeader;

			/*
			 * ==================> NAME <==================
			 */
			
			// If there is already a section for this effect in base/default headers, then use that. Otherwise, create a new one. We never want to OVERWRITE something thats already there.
			baseHeader = (baseHeader.isConfigurationSection(effect.getName()) ? baseHeader.getConfigurationSection(effect.getName()) : baseHeader.createSection(effect.getName()));
			defaultHeader = (defaultHeader.isConfigurationSection(defaultEffect.getName()) ? defaultHeader.getConfigurationSection(defaultEffect.getName()) : defaultHeader.createSection(defaultEffect.getName()));
			
			/*
			 * ==================> MODIFIER <==================
			 */
			
			if(effect.getModifier() != null) {
				setMapParsable(baseHeader.createSection("modifier"), defaultHeader.getConfigurationSection("modifier"), effect.getModifier(), defaultEffect.getModifier());
			} else 
				baseHeader.set("modifier", null);
			/*
			 * ==================> TRIGGER <==================
			 */
			
			if(effect.getTrigger() != null) {
				setMapParsable(baseHeader.createSection("trigger"), defaultHeader.getConfigurationSection("trigger"), effect.getTrigger(), defaultEffect.getTrigger());
			} else 
				baseHeader.set("trigger", null);
			/*
			 * ==================> RADIUS <==================
			 */
			
			if(effect.hasRadius()) {
				if(effect.getRadius().equals(defaultEffect.getRadius()))
					defaultHeader.set("radius", effect.getRadius());
				else
					baseHeader.set("radius", effect.getRadius());
			}
			else 
				baseHeader.set("radius", null);
			/*
			 * ==================> IGNORE USER <==================
			 */
			
			if(!effect.ignoreUser()) {
				if(effect.ignoreUser() == defaultEffect.ignoreUser())
					defaultHeader.set("ignore user", effect.ignoreUser());
				else
					baseHeader.set("ignore user", effect.ignoreUser());
			}
			else 
				baseHeader.set("ignore user", null);
			
			/*
			 * ==================> TAG <==================
			 */
			
			if(effect.hasTag())
				setMapParsable(baseHeader.createSection("tag"), defaultHeader.getConfigurationSection("tag"), effect.getTag(), defaultEffect.getTag());
			else 
				baseHeader.set("tag", null);
			/*
			 * ==================> PARTICLE EFFECTS <==================
			 */
			if(effect.hasParticle())
				setMapParsable(baseHeader.createSection("particles"), defaultHeader.getConfigurationSection("particles"), effect.getParticleEffect(), defaultEffect.getParticleEffect());
			else 
				baseHeader.set("particles", null);
			/*
			 * ==================> SOUND EFFECTS <==================
			 */
			
			if(effect.hasSound())
				setMapParsable(baseHeader.createSection("sounds"), defaultHeader.getConfigurationSection("sounds"), effect.getSoundEffect(), defaultEffect.getSoundEffect());
			else 
				baseHeader.set("sounds", null);
			/*
			 * ==================> POTION EFFECTS <==================
			 */
			
			if(effect.hasPotions()) {
				Messenger.debug("effect: "+effect.getPotions().toString());
				Messenger.debug("default: "+defaultEffect.getPotions().toString());
				if(effect.getPotions().equals(defaultEffect.getPotions()))
					currentHeader = defaultHeader.createSection("potion effects");
				else
					currentHeader = baseHeader.createSection("potion effects");
				
				for(PotionEffect potionEffect : effect.getPotions())
					BrEffectYML.postPotionEffect(currentHeader, potionEffect);
			}
			else 
				baseHeader.set("potion effects", null);
			/*
			 * ==================> FILTERS <==================
			 */
			
			if(effect.hasFilter()) {
				Messenger.debug("==> Has Filter");
				if(effect.getFilter().equals(defaultEffect.getFilter()))
					currentHeader = defaultHeader.createSection("filter");
				else 
					currentHeader = baseHeader.createSection("filter");
				// >>> Target
				if(!effect.getFilter().getType(FilterTypes.TARGET).noFilter()) {
					setFilterType(currentHeader.createSection("target"), effect.getFilter().getType(FilterTypes.TARGET));
				}
				// >>> Ignore
				if(!effect.getFilter().getType(FilterTypes.IGNORE).noFilter()) {
					setFilterType(currentHeader.createSection("ignore"), effect.getFilter().getType(FilterTypes.IGNORE));
				}
			}
			else 
				baseHeader.set("filter", null);
			
			instance.configHandler.saveConfigQuietly();
			
			return true;
		} catch (Exception e) {
			if(effect == null)
				Messenger.warning("An Unknown Effect failed to save:");
			Messenger.warning("'"+effect.getName()+"' Effect failed to save:");
			e.printStackTrace();
		}
		return false;
	}
	/**
	 * This function will extract a BrEffect from a YML file.
	 * If the BrEffect is not complete, a default effect can be applied to fill in any null values.
	 * 
	 * @param baseHeader The root where the effect's YML structure begins
	 * @param defaultHeader The default root that will be used to fill any values that could not be determined/found in the YML. If this is set to null, then the YML MUST provide all details, or the effect's retrieval will fail.
	 * @param incompleteAllowed Whether essential values (such as Trigger) can be missing. Generally true if this is retireving a default effect.
	 */
	public static BrEffect retrieve(ConfigurationSection baseHeader, ConfigurationSection defaultHeader){
		// Setting the name of the current essential task. Used to give a little bit of info when errors occur.
//		String essentialTask = null;
		try{			
			// Making sure the baseHeader is not null.
			Check.notNull(baseHeader);
			
			// Creating currentHeader to be used throughout this method.
			ConfigurationSection currentHeader = baseHeader;
			
			
			// Getting the effect name from the last portion of the baseHeader.
			String effectName = extractLast(baseHeader);
			
			// Adding effect to the processing list. This avoids any infinite loops of processing effects.
			BrEffectCollection.addProcessing(effectName);
			
			/*
			 * ==================> MODIFIER <==================
			 */
//			essentialTask = "Modifier";
			
			// Setting currentHeader to the baseHeader's modifier.
			// If this is unavailable, then currentHeader is set to the defaultSection's modifier.
			currentHeader = getAvailable(getSection(baseHeader, "modifier"), getSection(defaultHeader, "modifier"));
			
			// Getting modifierName value from either base or default headers, depending on whats available.
			String modifierName = getString("type", currentHeader, getSection(defaultHeader, "modifier"));
			
			Modifier modifier = null;
			// If this effect is allowed to be incomplete then this can be null.
			try{
				// Setting the modifier using the currentHeader, defaultHeader and getSimpleClassDefault method.
				modifier = getMapParsable(currentHeader, getSection(defaultHeader, "modifier"), ModifierHandler.get(modifierName), "type");
			} catch (YAMLException e){
			} catch (ClassNotFoundException e){
				throw new ClassNotFoundException("Type required for 'modifier' is missing or unknown.");
			}

			/*
			 * ==================> TRIGGER <==================
			 */
//			essentialTask = "Trigger";
			
			// Setting currentHeader to the baseHeader's trigger.
			// If this is unavailable, then currentHeader is set to the defaultSection's trigger.
			currentHeader = getAvailable(getSection(baseHeader, "trigger"), getSection(defaultHeader, "trigger"));
			
			// Getting triggerName value from either base or default headers, depending on whats available.
			String triggerName = getString("type", currentHeader, getSection(defaultHeader, "trigger"));
			
			Trigger trigger = null;
			// If this effect is allowed to be incomplete then this can be null.
			if(triggerName != null) {
				try{
					// Setting the modifier using the currentHeader, defaultHeader and getSimpleClassDefault method.
					trigger = getMapParsable(currentHeader, getSection(defaultHeader, "trigger"), TriggerHandler.get(triggerName), "type");
				} catch (YAMLException e){
				} catch (ClassNotFoundException e){
					throw new ClassNotFoundException("'type' field is required but missing.");
				}
			}
			
			/*
			 * =========================================================
			 * 					NON-ESSENTIAL FIELDS
			 * =========================================================
			 */
//			essentialTask = null;
			
			/*
			 * ==================> RADIUS <==================
			 */
			
			// Setting currentHeader to the baseHeader.
			// If this doesnt have a radius, then currentHeader is set to the defaultSection.
			// If defaultSection doesnt have a radius, then currentHeader is null.
			currentHeader = getAvailableWithSet("radius", getSection(baseHeader), getSection(defaultHeader));

			// Extracting the nullable Float from the radius, currentHeader and defaultHeader.
			Float radius = null;
			if(currentHeader != null) {
				try {
					radius = getFloat("radius", currentHeader, defaultHeader);
				} catch(Exception e) {
					Messenger.warning("Failed to retrieve "+effectName+" radius:");
					e.printStackTrace();
				}
			}

			/*
			 * ==================> IGNORE USER <==================
			 */

			// Setting currentHeader to the baseHeader.
			// If this doesnt have an ignore_user, then currentHeader is set to the defaultSection.
			// If defaultSection doesnt have a ignore_user, then currentHeader is null.
			currentHeader = getAvailableWithSet("ignore user", getSection(baseHeader), getSection(defaultHeader));

			// Extracting the nullable Boolean from the radius, currentHeader and defaultHeader.
			Boolean ignoreUser = null;
			if(currentHeader != null) {
				try{
					ignoreUser = getBoolean("ignore user", currentHeader, defaultHeader);
				} catch(Exception e) {
					Messenger.warning("Failed to retrieve "+effectName+" ignore user:");
					e.printStackTrace();
				}
			}
			
			/*
			 * ==================> FILTER <==================
			 */

			// Setting currentHeader to the baseHeader's filter.
			// If this is unavailable, then currentHeader is set to the defaultSection's filter.
			currentHeader = getAvailable(getSection(baseHeader, "filter"), getSection(defaultHeader, "filter"));

			// Filter is null if there are no filters in the base or default headers.
			Filter filter = null;
			
			// If either the base or the default headers have a filter, then its added (base taking priority).
				if(currentHeader != null){
					// This is within a try/catch because it is optional.
					// If it failed, we dont want to halt the entire retrieval process.
					try{
						FilterType target = getFilterType(getSection(currentHeader, "target"), true);
						FilterType ignore = getFilterType(getSection(currentHeader, "ignore"), false);
						
						filter = new Filter(target, ignore);
					} 
					// Generally ClassNotFoundException (class is null) or YAMLException (currentHeader is null).
					catch(Exception e){
						Messenger.warning("Failed to retrieve "+effectName+" filter:");
						e.printStackTrace();
					}
				}
			/*
			 * ==================> TAGS <==================
			 */
			
			// Setting currentHeader to the baseHeader's tags.
			// If this is unavailable, then currentHeader is set to the defaultSection's tags.
			currentHeader = getAvailable(getSection(baseHeader, "tag"), getSection(defaultHeader, "tag"));
			
			// BrTag is null if there are no tags in the base or default headers.
			BrTag tag = null;
			// If either the base or the default headers have a tag, then its added (base taking priority).
			if(currentHeader != null){
				// This is within a try/catch because it is optional.
				// If it failed, we dont want to halt the entire retrieval process.
				try{

					// Setting the tag using the currentHeader, defaultHeader and getSimpleClassDefault method.
					tag = getMapParsable(currentHeader, getSection(defaultHeader, "tag"), BrTag.class);
				} 
				
				// Generally ClassNotFoundException (class is null) or YAMLException (currentHeader is null).
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
			currentHeader = getAvailable(getSection(baseHeader, "particles"), getSection(defaultHeader, "particles"));
			
			// ParticleEffect is null if there are no particles in the base or default headers.
			BrParticleEffect particleEffect = null;

			// If either the base or the default headers have a particle, then its added (base taking priority).
			if(currentHeader != null){
				// This is within a try/catch because it is optional.
				// If it failed, we dont want to halt the entire retrieval process.
				try{

					// Setting the particle effect using the currentHeader, defaultHeader and getSimpleClassDefault method.
					particleEffect = getMapParsable(currentHeader, getSection(defaultHeader, "particles"), BrParticleEffect.class);
				} 
				
				// Generally ClassNotFoundException (class is null) or YAMLException (currentHeader is null).
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
			currentHeader = getAvailable(getSection(baseHeader, "sounds"), getSection(defaultHeader, "sounds"));
			
			// SoundEffect is null if there are no sounds in the base or default headers.
			BrSoundEffect soundEffect = null;

			// If either the base or the default headers have a sound, then its added (base taking priority).
			if(currentHeader != null){
				// This is within a try/catch because it is optional.
				// If it failed, we dont want to halt the entire retrieval process.
				try{

					// Setting the sound effect using the currentHeader, defaultHeader and getSimpleClassDefault method.
					soundEffect = getMapParsable(currentHeader, getSection(defaultHeader, "sounds"), BrSoundEffect.class);
				} 
				
				// Generally ClassNotFoundException (class is null) or YAMLException (currentHeader is null).
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
			currentHeader = getAvailable(getSection(baseHeader, "potion effects"), getSection(defaultHeader, "potion effects"));
			
			// Creates a new ArrayList for the entities
			List<PotionEffect> potionEffects = new ArrayList<PotionEffect>();
			
			// If either the base or the default headers have potion effects, then they are added (base taking priority).
			if(currentHeader != null){
				
				// Loops through each potion effect on the list.
				for(String potionName : currentHeader.getKeys(false)){
					// This is within a try/catch because it is optional.
					// If it failed, we dont want to halt the entire retrieval process.
					try{
						
						// Attempts to retireve the potion effect from the given section.
						PotionEffect potionEffect = retrievePotionEffect(getSection(currentHeader, potionName));
						
						// If the potion effect is not null, adds it to the potion effects ArrayList.
						if(potionEffect != null) potionEffects.add(potionEffect);
						
					// Throws a NullPointerException if the potionEffect is null and debugging is on.
					} catch(NullPointerException e){
						Messenger.warning("Failed to retrieve "+effectName+" potion effect '"+potionName+"':");
						e.printStackTrace();
					}
				}
			}
			BrEffect effect = new BrEffect(effectName, modifier, trigger, radius, 
					filter, particleEffect, soundEffect, potionEffects, tag, ignoreUser);

			
			// If effect isnt already in the collection, it adds it.
			if(!Brewery.getEffectCollection().hasItem(effect.getName()))
				Brewery.getEffectCollection().add(effect);
			
			return effect;
		} catch(YAMLException e){
			Messenger.warning("An effect failed to be found and loaded:");
			e.printStackTrace();
		} catch(Exception e) {
//			if(essentialTask != null)
//				Messenger.warning("'"+extractLast(baseHeader)+"' Effect failed to load due to an error with the "+essentialTask+":");
//			else
				Messenger.warning("'"+extractLast(baseHeader)+"' Effect failed to load:");
			e.printStackTrace();
		} finally {
			// Removing effect to the processing list. This avoids any infinite loops of processing effects.
			BrEffectCollection.delProcessing(extractLast(baseHeader));
		}
		return null;
		
	}
	/**
	 * This function will extract a BrEffect from a YML file.
	 * If the BrEffect is not complete, a default effect can be applied to fill in any null values.
	 * 
	 * @param baseHeader The root where the effect's YML structure begins
	 * @param defaultHeader The default root that will be used to fill any values that could not be determined/found in the YML. If this is set to null, then the YML MUST provide all details, or the effect's retrieval will fail.
	 * @param incompleteAllowed Whether essential values (such as Trigger) can be missing. Generally true if this is retireving a default effect.
	 */
//	public static BrEffect retrieve(ConfigurationSection baseHeader, ConfigurationSection defaultHeader){
//		// Setting the name of the current essential task. Used to give a little bit of info when errors occur.
//		String essentialTask = null;
//		try{			
//			// Making sure the baseHeader is not null.
//			Check.notNull(baseHeader);
//			
//			// Creating currentHeader to be used throughout this method.
//			ConfigurationSection currentHeader = baseHeader;
//			
//			
//			// Getting the effect name from the last portion of the baseHeader.
//			String effectName = extractLast(baseHeader);
//			
//			// Adding effect to the processing list. This avoids any infinite loops of processing effects.
//			BrEffectCollection.addProcessing(effectName);
//			
//			/*
//			 * ==================> MODIFIER <==================
//			 */
//			essentialTask = "Modifier";
//			
//			// Setting currentHeader to the baseHeader's modifier.
//			// If this is unavailable, then currentHeader is set to the defaultSection's modifier.
//			currentHeader = getAvailable(getSection(baseHeader, "modifier"), getSection(defaultHeader, "modifier"));
//			
//			// Getting modifierName value from either base or default headers, depending on whats available.
//			String modifierName = getString("type", currentHeader, getSection(defaultHeader, "modifier"));
//			
//			Modifier modifier = null;
//			// If this effect is allowed to be incomplete then this can be null.
//			try{
//				// Setting the modifier using the currentHeader, defaultHeader and getSimpleClassDefault method.
//				modifier = getMapParsable(currentHeader, getSection(defaultHeader, "modifier"), ModifierHandler.get(modifierName), "type");
//			} catch (YAMLException e){
//			} catch (ClassNotFoundException e){
//				throw new ClassNotFoundException("Type required for 'modifier' is missing.");
//			}
//
//			/*
//			 * ==================> TRIGGER <==================
//			 */
//			essentialTask = "Trigger";
//			
//			// Setting currentHeader to the baseHeader's trigger.
//			// If this is unavailable, then currentHeader is set to the defaultSection's trigger.
//			currentHeader = getAvailable(getSection(baseHeader, "trigger"), getSection(defaultHeader, "trigger"));
//			
//			// Getting triggerName value from either base or default headers, depending on whats available.
//			String triggerName = getString("type", currentHeader, getSection(defaultHeader, "trigger"));
//			
//			Trigger trigger = null;
//			// If this effect is allowed to be incomplete then this can be null.
//			try{
//				// Setting the modifier using the currentHeader, defaultHeader and getSimpleClassDefault method.
//				trigger = getMapParsable(currentHeader, getSection(defaultHeader, "trigger"), TriggerHandler.get(triggerName), "type");
//			} catch (YAMLException e){
//			} catch (ClassNotFoundException e){
//				throw new ClassNotFoundException("'type' field is required but missing.");
//			}
//			
//			/*
//			 * =========================================================
//			 * 					NON-ESSENTIAL FIELDS
//			 * =========================================================
//			 */
//			essentialTask = null;
//			
//			/*
//			 * ==================> RADIUS <==================
//			 */
//			
//			// Setting currentHeader to the baseHeader.
//			// If this doesnt have a radius, then currentHeader is set to the defaultSection.
//			// If defaultSection doesnt have a radius, then currentHeader is null.
//			currentHeader = getAvailableWithSet("radius", getSection(baseHeader), getSection(defaultHeader));
//
//			// Extracting the nullable Float from the radius, currentHeader and defaultHeader.
//			Float radius = null;
//			if(currentHeader != null) {
//				try {
//					radius = getFloat("radius", currentHeader, defaultHeader);
//				} catch(Exception e) {
//					Messenger.warning("Failed to retrieve "+effectName+" radius:");
//					e.printStackTrace();
//				}
//			}
//
//			/*
//			 * ==================> IGNORE USER <==================
//			 */
//
//			// Setting currentHeader to the baseHeader.
//			// If this doesnt have an ignore_user, then currentHeader is set to the defaultSection.
//			// If defaultSection doesnt have a ignore_user, then currentHeader is null.
//			currentHeader = getAvailableWithSet("ignore user", getSection(baseHeader), getSection(defaultHeader));
//
//			// Extracting the nullable Boolean from the radius, currentHeader and defaultHeader.
//			Boolean ignoreUser = null;
//			if(currentHeader != null) {
//				try{
//					ignoreUser = getBoolean("ignore user", currentHeader, defaultHeader);
//				} catch(Exception e) {
//					Messenger.warning("Failed to retrieve "+effectName+" ignore user:");
//					e.printStackTrace();
//				}
//			}
//			
//			/*
//			 * ==================> FILTER <==================
//			 */
//
//			// Setting currentHeader to the baseHeader's filter.
//			// If this is unavailable, then currentHeader is set to the defaultSection's filter.
//			currentHeader = getAvailable(getSection(baseHeader, "filter"), getSection(defaultHeader, "filter"));
//
//			// Filter is null if there are no filters in the base or default headers.
//			Filter filter = null;
//			
//			// If either the base or the default headers have a filter, then its added (base taking priority).
//				if(currentHeader != null){
//					// This is within a try/catch because it is optional.
//					// If it failed, we dont want to halt the entire retrieval process.
//					try{
//						FilterGroup target = getFilterGroup(getSection(currentHeader, "target"), true);
//						FilterGroup ignore = getFilterGroup(getSection(currentHeader, "ignore"), false);
//						
//						filter = new Filter(target, ignore);
//					} 
//					// Generally ClassNotFoundException (class is null) or YAMLException (currentHeader is null).
//					catch(Exception e){
//						Messenger.warning("Failed to retrieve "+effectName+" filter:");
//						e.printStackTrace();
//					}
//				}
//			/*
//			 * ==================> TAGS <==================
//			 */
//			
//			// Setting currentHeader to the baseHeader's tags.
//			// If this is unavailable, then currentHeader is set to the defaultSection's tags.
//			currentHeader = getAvailable(getSection(baseHeader, "tag"), getSection(defaultHeader, "tag"));
//			
//			// BrTag is null if there are no tags in the base or default headers.
//			BrTag tag = null;
//			// If either the base or the default headers have a tag, then its added (base taking priority).
//			if(currentHeader != null){
//				// This is within a try/catch because it is optional.
//				// If it failed, we dont want to halt the entire retrieval process.
//				try{
//
//					// Setting the tag using the currentHeader, defaultHeader and getSimpleClassDefault method.
//					tag = getMapParsable(currentHeader, getSection(defaultHeader, "tag"), BrTag.class);
//				} 
//				
//				// Generally ClassNotFoundException (class is null) or YAMLException (currentHeader is null).
//				catch(Exception e){
//					Messenger.warning("Failed to retrieve "+effectName+" tag:");
//					e.printStackTrace();
//				}
//			}
//			
//			/*
//			 * ==================> PARTICLES <==================
//			 */
//			
//			// Setting currentHeader to the baseHeader's particles.
//			// If this is unavailable, then currentHeader is set to the defaultSection's particles.
//			currentHeader = getAvailable(getSection(baseHeader, "particles"), getSection(defaultHeader, "particles"));
//			
//			// ParticleEffect is null if there are no particles in the base or default headers.
//			BrParticleEffect particleEffect = null;
//
//			// If either the base or the default headers have a particle, then its added (base taking priority).
//			if(currentHeader != null){
//				// This is within a try/catch because it is optional.
//				// If it failed, we dont want to halt the entire retrieval process.
//				try{
//
//					// Setting the particle effect using the currentHeader, defaultHeader and getSimpleClassDefault method.
//					particleEffect = getMapParsable(currentHeader, getSection(defaultHeader, "particles"), BrParticleEffect.class);
//				} 
//				
//				// Generally ClassNotFoundException (class is null) or YAMLException (currentHeader is null).
//				catch(Exception e){
//					Messenger.warning("Failed to retrieve "+effectName+" particle effect:");
//					e.printStackTrace();
//				}
//			}
//			
//			/*
//			 * ==================> SOUNDS <==================
//			 */
//			// Setting currentHeader to the baseHeader's sounds.
//			// If this is unavailable, then currentHeader is set to the defaultSection's sounds.
//			currentHeader = getAvailable(getSection(baseHeader, "sounds"), getSection(defaultHeader, "sounds"));
//			
//			// SoundEffect is null if there are no sounds in the base or default headers.
//			BrSoundEffect soundEffect = null;
//
//			// If either the base or the default headers have a sound, then its added (base taking priority).
//			if(currentHeader != null){
//				// This is within a try/catch because it is optional.
//				// If it failed, we dont want to halt the entire retrieval process.
//				try{
//
//					// Setting the sound effect using the currentHeader, defaultHeader and getSimpleClassDefault method.
//					soundEffect = getMapParsable(currentHeader, getSection(defaultHeader, "sounds"), BrSoundEffect.class);
//				} 
//				
//				// Generally ClassNotFoundException (class is null) or YAMLException (currentHeader is null).
//				catch(Exception e){
//					Messenger.warning("Failed to retrieve "+effectName+" sound effect:");
//					e.printStackTrace();
//				}
//			}
//			
//			/*
//			 * ==================> POTION EFFECTS <==================
//			 */
//
//			// Setting currentHeader to the baseHeader's potion effects.
//			// If this is unavailable, then currentHeader is set to the defaultSection's potion effects.
//			currentHeader = getAvailable(getSection(baseHeader, "potion effects"), getSection(defaultHeader, "potion effects"));
//			
//			// Creates a new ArrayList for the entities
//			List<PotionEffect> potionEffects = new ArrayList<PotionEffect>();
//			
//			// If either the base or the default headers have potion effects, then they are added (base taking priority).
//			if(currentHeader != null){
//				
//				// Loops through each potion effect on the list.
//				for(String potionName : currentHeader.getKeys(false)){
//					// This is within a try/catch because it is optional.
//					// If it failed, we dont want to halt the entire retrieval process.
//					try{
//						
//						// Attempts to retireve the potion effect from the given section.
//						PotionEffect potionEffect = retrievePotionEffect(getSection(currentHeader, potionName));
//						
//						// If the potion effect is not null, adds it to the potion effects ArrayList.
//						if(potionEffect != null) potionEffects.add(potionEffect);
//						
//					// Throws a NullPointerException if the potionEffect is null and debugging is on.
//					} catch(NullPointerException e){
//						Messenger.warning("Failed to retrieve "+effectName+" potion effect '"+potionName+"':");
//						e.printStackTrace();
//					}
//				}
//			}
//			BrEffect effect = new BrEffect(effectName, modifier, trigger, radius, 
//					filter, particleEffect, soundEffect, potionEffects, tag, ignoreUser);
//
//			
//			// If effect isnt already in the collection, it adds it.
//			if(!Brewery.getEffectCollection().hasItem(effect.getName()))
//				Brewery.getEffectCollection().add(effect);
//			
//			return effect;
//		} catch(YAMLException e){
//			Messenger.warning("An effect failed to be found and loaded:");
//			e.printStackTrace();
//		} catch(Exception e) {
//			if(essentialTask != null)
//				Messenger.warning("'"+extractLast(baseHeader)+"' Effect failed to load due to an error with the "+essentialTask+":");
//			else
//				Messenger.warning("'"+extractLast(baseHeader)+"' Effect failed to load:");
//			e.printStackTrace();
//		} finally {
//			// Removing effect to the processing list. This avoids any infinite loops of processing effects.
//			BrEffectCollection.delProcessing(extractLast(baseHeader));
//		}
//		return null;
//		
//	}
}
