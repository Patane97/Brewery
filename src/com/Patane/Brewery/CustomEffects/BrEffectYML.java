package com.Patane.Brewery.CustomEffects;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Particle;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.yaml.snakeyaml.error.YAMLException;

import com.Patane.Brewery.Brewery;
import com.Patane.Brewery.Collections.BrEffectCollection;
import com.Patane.Brewery.CustomEffects.BrEffect.BrSoundEffect;
import com.Patane.Brewery.CustomEffects.BrEffect.BrTag;
import com.Patane.Brewery.CustomEffects.Filter.FilterType;
import com.Patane.Brewery.CustomEffects.Filter.FilterTypes;
import com.Patane.Brewery.Handlers.ModifierHandler;
import com.Patane.Brewery.Handlers.TriggerHandler;
import com.Patane.Brewery.YAML.BreweryYAML;
import com.Patane.util.formables.ParticleHandler;
import com.Patane.util.formables.Radius;
import com.Patane.util.formables.SpecialParticle;
import com.Patane.util.formables.Particles.STANDARD;
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
		for(String effectName : getPrefix().getKeys(false)) {
			BrEffect effect = load(effectName);
			if(effect != null)
				loadedNames.add(effect.getName());
		}
		Messenger.info("Successfully loaded Effects: "+StringsUtil.stringJoiner(loadedNames, ", "));
	}
	
	public boolean save(BrEffect effect) throws IllegalStateException {
		Messenger.info("Saving Effect: "+effect.getName());
		if(post(getPrefix(), effect, null, null)) {
			configHandler.saveConfigQuietly();
			return true;
		}
		return false;
	}
	
	public BrEffect load(String effectName) {
		Messenger.info("Loading Effect: "+effectName);
		setSelect(getPrefix());
		ConfigurationSection section = getSection(effectName);
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
				setMapParsable(baseHeader.createSection("radius"), defaultHeader.getConfigurationSection("radius"), effect.getRadius(), defaultEffect.getRadius());
//				if(effect.getRadius().equals(defaultEffect.getRadius()))
//					defaultHeader.set("radius", effect.getRadius());
//				else
//					baseHeader.set("radius", effect.getRadius());
			}
			else 
				baseHeader.set("radius", null);
			/*
			 * ==================> IGNORE USER <==================
			 */
			
			// If theyre not equal, always save
			if(effect.ignoreUser() != defaultEffect.ignoreUser())
				baseHeader.set("ignore user", effect.ignoreUser());
			// If ARE equal and are false, save to default
			else if(!effect.ignoreUser()) {
				defaultHeader.set("ignore user", effect.ignoreUser());
			}
			// If they ARE equal and are true, remove both
			else {
				baseHeader.set("ignore user", null);
				defaultHeader.set("ignore user", null);
			}
			
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
			if(effect.hasParticles()) {
				if(effect.getParticles().equals(defaultEffect.getParticles()))
					currentHeader = defaultHeader.createSection("particle effects");
				else
					currentHeader = baseHeader.createSection("particle effects");
				Map<Particle, Integer> counts = new HashMap<Particle, Integer>();
				
				// TODO: Maybe a cleaner way of writing all this (1) duplicates for mapparsable and potionffects (below)?
				for(SpecialParticle particle : effect.getParticles()) {
					int pCount = 0;
					if(!counts.containsKey(particle.getParticle()))
						counts.put(particle.getParticle(), 0);
					else
						pCount = counts.get(particle.getParticle())+1;
					String particleName = getDuplicateName(particle.getParticle().toString(), pCount);
					setMapParsable(currentHeader.createSection(particleName), null, particle, null);
					counts.put(particle.getParticle(), pCount++);
				}
			}
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
				if(effect.getPotions().equals(defaultEffect.getPotions()))
					currentHeader = defaultHeader.createSection("potion effects");
				else
					currentHeader = baseHeader.createSection("potion effects");
				
				Map<PotionEffectType, Integer> typeCounts = new HashMap<PotionEffectType, Integer>();
				
				// Loops through each potion effect and counts how many of that type have been saved.
				// If it sees that type already saved, it adds to the count and prints the effect with
				// (peCount) at the end
				// EG. there are 3 SLOW effects, this will ensure its printed as
				// 'SLOW', 'SLOW(1)', 'SLOW(2)'.
				for(PotionEffect potionEffect : effect.getPotions()) {
					int peCount = 0;
					if(!typeCounts.containsKey(potionEffect.getType()))
						typeCounts.put(potionEffect.getType(), 0);
					else {
						peCount = typeCounts.get(potionEffect.getType())+1;
					}
					BrEffectYML.postPotionEffect(currentHeader, peCount, potionEffect);
					typeCounts.put(potionEffect.getType(), peCount++);
				}
			}
			else 
				baseHeader.set("potion effects", null);
			/*
			 * ==================> FILTERS <==================
			 */
			
			if(effect.hasFilter()) {
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
			Messenger.printStackTrace(e);
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
	public static BrEffect retrieve(ConfigurationSection baseHeader, ConfigurationSection defaultHeader) {
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
			} catch (YAMLException e) {
			} catch (ClassNotFoundException e) {
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
				} catch (YAMLException e) {
				} catch (ClassNotFoundException e) {
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

			// Setting currentHeader to the baseHeader's trigger.
			// If this is unavailable, then currentHeader is set to the defaultSection's trigger.
			currentHeader = getAvailable(getSection(baseHeader, "radius"), getSection(defaultHeader, "radius"));
			
			Radius radius = null;
			try{
				radius = getMapParsable(currentHeader, getSection(defaultHeader, "radius"), Radius.class);
			} catch (YAMLException e) {}

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
					Messenger.printStackTrace(e);
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
				if(currentHeader != null) {
					// This is within a try/catch because it is optional.
					// If it failed, we dont want to halt the entire retrieval process.
					try{
						FilterType target = getFilterType(getSection(currentHeader, "target"), true);
						FilterType ignore = getFilterType(getSection(currentHeader, "ignore"), false);
						
						filter = new Filter(target, ignore);
					} 
					// Generally ClassNotFoundException (class is null) or YAMLException (currentHeader is null).
					catch(Exception e) {
						Messenger.warning("Failed to retrieve "+effectName+" filter:");
						Messenger.printStackTrace(e);
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
			if(currentHeader != null) {
				// This is within a try/catch because it is optional.
				// If it failed, we dont want to halt the entire retrieval process.
				try{

					// Setting the tag using the currentHeader, defaultHeader and getSimpleClassDefault method.
					tag = getMapParsable(currentHeader, getSection(defaultHeader, "tag"), BrTag.class);
				} 
				
				// Generally ClassNotFoundException (class is null) or YAMLException (currentHeader is null).
				catch(Exception e) {
					Messenger.warning("Failed to retrieve "+effectName+" tag:");
					Messenger.printStackTrace(e);
				}
			}
			
			/*
			 * ==================> PARTICLES <==================
			 */
			
			// Setting currentHeader to the baseHeader's particles.
			// If this is unavailable, then currentHeader is set to the defaultSection's particles.
			currentHeader = getAvailable(getSection(baseHeader, "particle effects"), getSection(defaultHeader, "particle effects"));
			
			// ParticleEffect is null if there are no particles in the base or default headers.
			List<SpecialParticle> particles = new ArrayList<SpecialParticle>();

			// If either the base or the default headers have a particle, then its added (base taking priority).
			if(currentHeader != null) {
				for(String rawParticleName : currentHeader.getKeys(false)) {
					// Removes any (brackets) at end of particleName. This is used to identify multiple
					String particleName = StringsUtil.firstGroup(rawParticleName, "(\\w+)(?=\\(\\d\\))");
					
					// This is within a try/catch because it is optional.
					// If it failed, we dont want to halt the entire retrieval process.
					try {
						// Grabbing the header
						currentHeader = getAvailable(getSection(baseHeader, "particle effects", rawParticleName), getSection(defaultHeader, "particle effects", rawParticleName));
						
						// Attempts to find the Special particle from particleHandler
						Class<? extends SpecialParticle> particleClass = ParticleHandler.get(particleName);
						
						// If this particle does not return anything from the handler, then its not a valid particle
						if(particleClass == null) {
							throw new NullPointerException(String.format("Particle of type %s does not exist!", particleName));
						}
						
						// If the particle class extends from OTHER, then we need to inject the ParticleType manually (outside constructor)
						if(STANDARD.class.isAssignableFrom(particleClass)) {
							
							// This won't give IllegalArgumentException as 'ParticleHandler.get' confirms the given name IS in the particle enum
							Particle particleType = StringsUtil.constructEnum(particleName, Particle.class);
							
							// Creating the OTHER particle. If this extends from OTHER, such as DIRECTIONAL, then particleClass will be DIRECTIONAL
							STANDARD otherParticle = (STANDARD) getMapParsable(currentHeader, getSection(defaultHeader, "particle effects", rawParticleName), particleClass);
							
							// Setting the particle of the OTHER manually.
							otherParticle.setParticle(particleType);
							
							// Add to list
							particles.add(otherParticle);
						}
						// Otherwise we are looking at a custom particle class and thus it will handle its own particletype injection.
						else
							particles.add(getMapParsable(currentHeader, getSection(defaultHeader, "particle effects", rawParticleName), particleClass));
						
					}
					// Any exceptions will be caught here and printed.
					catch(Exception e) {
						Messenger.warning(String.format("Failed to retrieve %s particle effect for %s:", rawParticleName, effectName));
						Messenger.printStackTrace(e);
					}
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
			if(currentHeader != null) {
				// This is within a try/catch because it is optional.
				// If it failed, we dont want to halt the entire retrieval process.
				try{

					// Setting the sound effect using the currentHeader, defaultHeader and getSimpleClassDefault method.
					soundEffect = getMapParsable(currentHeader, getSection(defaultHeader, "sounds"), BrSoundEffect.class);
				} 
				
				// Generally ClassNotFoundException (class is null) or YAMLException (currentHeader is null).
				catch(Exception e) {
					Messenger.warning("Failed to retrieve "+effectName+" sound effect:");
					Messenger.printStackTrace(e);
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
			if(currentHeader != null) {
				
				// Loops through each potion effect on the list.
				for(String potionName : currentHeader.getKeys(false)) {
					// This is within a try/catch because it is optional.
					// If it failed, we dont want to halt the entire retrieval process.
					try{
						
						// Attempts to retireve the potion effect from the given section.
						PotionEffect potionEffect = retrievePotionEffect(getSection(currentHeader, potionName));
						
						// If the potion effect is not null, adds it to the potion effects ArrayList.
						if(potionEffect != null) potionEffects.add(potionEffect);
						
					// Throws a NullPointerException if the potionEffect is null and debugging is on.
					} catch(NullPointerException e) {
						Messenger.warning("Failed to retrieve "+effectName+" potion effect '"+potionName+"':");
						Messenger.printStackTrace(e);
					}
				}
			}
			BrEffect effect = new BrEffect(effectName, modifier, trigger, radius, 
					filter, particles, soundEffect, potionEffects, tag, ignoreUser);

			
			// If effect isnt already in the collection, it adds it.
			if(!Brewery.getEffectCollection().hasItem(effect.getName()))
				Brewery.getEffectCollection().add(effect);
			
			return effect;
		} catch(YAMLException e) {
			Messenger.warning("An effect failed to be found and loaded:");
			Messenger.printStackTrace(e);
		} catch(Exception e) {
//			if(essentialTask != null)
//				Messenger.warning("'"+extractLast(baseHeader)+"' Effect failed to load due to an error with the "+essentialTask+":");
//			else
				Messenger.warning("'"+extractLast(baseHeader)+"' Effect failed to load:");
			Messenger.printStackTrace(e);
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
//	public static BrEffect retrieve(ConfigurationSection baseHeader, ConfigurationSection defaultHeader) {
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
//			} catch (YAMLException e) {
//			} catch (ClassNotFoundException e) {
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
//			} catch (YAMLException e) {
//			} catch (ClassNotFoundException e) {
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
//					Messenger.printStackTrace(e);
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
//					Messenger.printStackTrace(e);
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
//				if(currentHeader != null) {
//					// This is within a try/catch because it is optional.
//					// If it failed, we dont want to halt the entire retrieval process.
//					try{
//						FilterGroup target = getFilterGroup(getSection(currentHeader, "target"), true);
//						FilterGroup ignore = getFilterGroup(getSection(currentHeader, "ignore"), false);
//						
//						filter = new Filter(target, ignore);
//					} 
//					// Generally ClassNotFoundException (class is null) or YAMLException (currentHeader is null).
//					catch(Exception e) {
//						Messenger.warning("Failed to retrieve "+effectName+" filter:");
//						Messenger.printStackTrace(e);
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
//			if(currentHeader != null) {
//				// This is within a try/catch because it is optional.
//				// If it failed, we dont want to halt the entire retrieval process.
//				try{
//
//					// Setting the tag using the currentHeader, defaultHeader and getSimpleClassDefault method.
//					tag = getMapParsable(currentHeader, getSection(defaultHeader, "tag"), BrTag.class);
//				} 
//				
//				// Generally ClassNotFoundException (class is null) or YAMLException (currentHeader is null).
//				catch(Exception e) {
//					Messenger.warning("Failed to retrieve "+effectName+" tag:");
//					Messenger.printStackTrace(e);
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
//			if(currentHeader != null) {
//				// This is within a try/catch because it is optional.
//				// If it failed, we dont want to halt the entire retrieval process.
//				try{
//
//					// Setting the particle effect using the currentHeader, defaultHeader and getSimpleClassDefault method.
//					particleEffect = getMapParsable(currentHeader, getSection(defaultHeader, "particles"), BrParticleEffect.class);
//				} 
//				
//				// Generally ClassNotFoundException (class is null) or YAMLException (currentHeader is null).
//				catch(Exception e) {
//					Messenger.warning("Failed to retrieve "+effectName+" particle effect:");
//					Messenger.printStackTrace(e);
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
//			if(currentHeader != null) {
//				// This is within a try/catch because it is optional.
//				// If it failed, we dont want to halt the entire retrieval process.
//				try{
//
//					// Setting the sound effect using the currentHeader, defaultHeader and getSimpleClassDefault method.
//					soundEffect = getMapParsable(currentHeader, getSection(defaultHeader, "sounds"), BrSoundEffect.class);
//				} 
//				
//				// Generally ClassNotFoundException (class is null) or YAMLException (currentHeader is null).
//				catch(Exception e) {
//					Messenger.warning("Failed to retrieve "+effectName+" sound effect:");
//					Messenger.printStackTrace(e);
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
//			if(currentHeader != null) {
//				
//				// Loops through each potion effect on the list.
//				for(String potionName : currentHeader.getKeys(false)) {
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
//					} catch(NullPointerException e) {
//						Messenger.warning("Failed to retrieve "+effectName+" potion effect '"+potionName+"':");
//						Messenger.printStackTrace(e);
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
//		} catch(YAMLException e) {
//			Messenger.warning("An effect failed to be found and loaded:");
//			Messenger.printStackTrace(e);
//		} catch(Exception e) {
//			if(essentialTask != null)
//				Messenger.warning("'"+extractLast(baseHeader)+"' Effect failed to load due to an error with the "+essentialTask+":");
//			else
//				Messenger.warning("'"+extractLast(baseHeader)+"' Effect failed to load:");
//			Messenger.printStackTrace(e);
//		} finally {
//			// Removing effect to the processing list. This avoids any infinite loops of processing effects.
//			BrEffectCollection.delProcessing(extractLast(baseHeader));
//		}
//		return null;
//		
//	}
}
