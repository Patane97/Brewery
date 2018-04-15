package com.Patane.Brewery.CustomEffects;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.entity.EntityType;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.Patane.Brewery.Brewery;
import com.Patane.Brewery.Messenger;
import com.Patane.Brewery.Messenger.Msg;
import com.Patane.Brewery.CustomEffects.BrEffect.BrParticleEffect;
import com.Patane.Brewery.CustomEffects.BrEffect.BrSoundEffect;
import com.Patane.Brewery.CustomEffects.BrEffect.DefaultContainer;
import com.Patane.Brewery.YML.BasicYML;
import com.Patane.Brewery.util.ErrorHandler;
import com.Patane.Brewery.util.ErrorHandler.BrLoadException;
import com.Patane.Brewery.util.ErrorHandler.Importance;
import com.Patane.Brewery.util.StringUtilities;

public class BrEffectYML extends BasicYML{

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
			load(effectName);
		}
		Messenger.info("Successfully loaded Effects: "+StringUtilities.stringJoiner(Brewery.getEffectCollection().getAllIDs(), ", "));
	}
	public void load(String effectName){
		try{
			setHeader(effectName);
			Messenger.debug(Msg.INFO, "Attempting to load "+effectName+" effect...");
			if(!effectName.equals(effectName.replace(" ", "_").toUpperCase()))
				ErrorHandler.optionalLoadError(Msg.WARNING, Importance.REQUIRED, "Failed to load "+effectName+": Name must be in upper case with no spacing, eg. '"+effectName.replace(" ", "_").toUpperCase()+"'");
			//MODIFIER
			setHeader(effectName, "modifier");
			String modifierName = header.getString("type");
			Modifier modifier = getByClass(Importance.REQUIRED, ModifierHandler.get(modifierName), "Modifier", "the "+effectName+" effect", header, "type");
			//RADIUS
			setHeader(effectName);
			Integer radius = getIntFromString(Importance.ERROR, header.getString("radius"), "radius", "the "+effectName+" effect's radius");
			if(radius != null)
				Messenger.debug(Msg.INFO, "     + radius["+radius+"]");
			//TRIGGER
			EffectType effectType = null;
			setHeader(effectName, "trigger");
			String effectTypeName = header.getString("type");
			effectType = getByClass(Importance.ERROR, EffectTypeHandler.get(effectTypeName), "trigger", "the "+effectName+" effect's trigger", header, "type");
			//ENTITIES
			List<EntityType> entities = null;
			if(isSection(effectName, "entities")){
				entities = new ArrayList<EntityType>();
				setHeader(effectName);
				for(String entityName : header.getStringList("entities")){
					try{
						EntityType entityType = getEnumFromString(Importance.REQUIRED, EntityType.class, entityName, "entity type", "an entity for "+effectName+" effect");
						entities.add(entityType);
					} catch (BrLoadException e){
						Messenger.warning(e.getMessage());
					}
				}
			}
			//PARTICLES
			BrParticleEffect particleEffect = getByClass(Importance.ERROR, BrParticleEffect.class, "Particle", "the "+effectName+" effect's particle", getSection(effectName, "particle"), "type");
			//SOUNDS
			BrSoundEffect soundEffect = getByClass(Importance.ERROR, BrSoundEffect.class, "Sound", "the "+effectName+" effect's sound", getSection(effectName, "sound"), "type");
			//POTIONEFFECTS
			List<PotionEffect> potionEffects = new ArrayList<PotionEffect>();
			if(isSection(effectName, "potion_effects")){
				setHeader(effectName, "potion_effects");
				for(String potionName : header.getKeys(false)){
					try{
						setHeader(effectName, "potion_effects", potionName);
						String preName = "the "+effectName+"'s "+potionName+" Effect";
						// Type
						PotionEffectType type = PotionEffectType.getByName(potionName);
						if(type == null)
							ErrorHandler.optionalLoadError(Msg.WARNING, Importance.ERROR, "Failed to load "+preName+": '"+potionName+"' not recognised as a valid PotionEffect.");
						// Duration
						int duration = Math.round(getFloatFromString(Importance.ERROR, header.getString("duration"), "duration", preName)*20);
						// Strength
						int strength = getIntFromString(Importance.ERROR, header.getString("strength"), "strength", preName);
						Messenger.debug(Msg.INFO, "     + Potion Effect["+potionName+", "+duration+", "+strength+"]");
						potionEffects.add(new PotionEffect(type, duration, strength));
					} catch (BrLoadException e){
						Messenger.warning(e.getMessage());
					}
				}
			}
			new BrEffect(effectName, modifier, particleEffect, soundEffect, new DefaultContainer(effectType, radius, (entities == null ? null : entities.toArray(new EntityType[0]))), potionEffects.toArray(new PotionEffect[0]));
		} catch (BrLoadException e){
			Messenger.warning(e.getMessage());
		}
	}
}
