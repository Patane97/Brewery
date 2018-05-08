package com.Patane.Brewery.CustomEffects;

import java.util.List;
import java.util.Map;

import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;

import com.Patane.Brewery.CustomEffects.Modifier.ModifierInfo;
import com.Patane.util.YML.YMLParsable;
import com.Patane.util.general.Messenger;
import com.Patane.util.ingame.Focusable.Focus;

public abstract class Trigger extends YMLParsable{

	protected Trigger(){};
	public Trigger(Map<String, String> fields){}
	
	public abstract void execute(BrEffect effect, Location impact, LivingEntity executor);

	/**
	 * Executes an effect on each entity within the effects given radius.
	 * @param effect Effect to execute.
	 * @param impact Location of the effects impact.
	 * @param executor The LivingEntity who cast the effect.
	 * @return A List of the LivingEntities in which the effects were executed on.
	 */
	protected List<LivingEntity> executeOnEntities(BrEffect effect, Location impact, LivingEntity executor) {		
		// Grabs all entites within the radius and filters them appropriately.
		List<LivingEntity> hitEntities = effect.getFilter().filter(impact, effect.getRadius());
		for(LivingEntity hitEntity : hitEntities){
			// Executes the effect on each entity hit.
			executeOnEntity(effect, impact, executor, hitEntity);
		}
		return hitEntities;
	}
	/**
	 * Executes an effect on a list of entities.
	 * @param effect Effect to execute.
	 * @param impact Location of the effects impact.
	 * @param executor The LivingEntity who cast the effect.
	 * @param hitEntity The LivingEntities to execute the effect on.
	 * @return A List of the LivingEntities in which the effects were executed on.
	 */
	protected List<LivingEntity> executeOnEntities(BrEffect effect, Location impact, LivingEntity executor, List<LivingEntity> hitEntities) {
		for(LivingEntity hitEntity : hitEntities)
			// Executes the effect on each entity given.
			executeOnEntity(effect, impact, executor, hitEntity);
		return hitEntities;
	}
	/**
	 * Executes an effect on a specific LivingEntity, with relevant needed information.
	 * @param effect Effect to execute.
	 * @param impact Location of the effects impact.
	 * @param executor The LivingEntity who cast the effect.
	 * @param hitEntity The LivingEntity to execute the effect on.
	 * @return The LivingEntity in which the effects were executed on, or null if they are dead.
	 */
	protected LivingEntity executeOnEntity(BrEffect effect, Location impact, LivingEntity executor, LivingEntity hitEntity){
		// If the entity is dead, do not perform any actions onto them. Let them rest in peace dangit!
		if(!hitEntity.isDead()){
			// Applies visual/auditory effects if they focus on Entities.
			applyByFocus(effect, hitEntity.getEyeLocation(), Focus.ENTITY);
			
			// Applies potion effects to given entity.
			hitEntity.addPotionEffects(effect.getPotions());
			
			// Applies modifiers (These ONLY target entities, so a focus check is not needed)
			applyModifiers(effect, impact, executor, hitEntity);
			
			// Debug to tell the entity it has been affected by certain effect
			Messenger.debug(hitEntity, "&cAffected by &7"+effect.getName()+"&c effect.");
			return hitEntity;
		}
		return null;
	}
	/**
	 * Applies an effect's modifier onto a specific LivingEntity.
	 * @param effect Effect to execute.
	 * @param impact Location of the effects impact.
	 * @param executor The LivingEntity who cast the effect.
	 * @param hitEntity The LivingEntity to apply the modifier to.
	 */
	protected void applyModifiers(BrEffect effect, Location impact, LivingEntity executor, LivingEntity hitEntity){
		effect.getModifier().modify(new ModifierInfo(impact, executor, hitEntity));
	}

	/**
	 * Applies certain visual/auditory effects based on whether they focus on a given point.
	 * @param effect Effect to check within.
	 * @param location Location to focus on.
	 * @param focus Focus type.
	 */
	protected void applyByFocus(BrEffect effect, Location location, Focus focus){
		/*
		 *  Applying Particles
		 */
		// Applying particle if there IS and
		// If its focus matches the focus given. For example, If given focus is Focus.ENTITY, then the particle effect
		// will only apply if the particles formation has Focus.ENTITY within it (eg, the 'Entity' formation)
		if(effect.hasParticle() && effect.getParticleEffect().getFormation().getFocus() == focus)
			// Applies the particle effect in its given formation.
			effect.getParticleEffect().getFormation().form(effect, location);

		/*
		 *  Applying Sounds
		 */
		if(effect.hasSound())
			effect.getSoundEffect().spawn(location);
	}
}
