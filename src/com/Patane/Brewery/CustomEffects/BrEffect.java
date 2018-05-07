package com.Patane.Brewery.CustomEffects;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.potion.PotionEffect;

import com.Patane.Brewery.Handlers.BrMetaDataHandler;
import com.Patane.Brewery.Handlers.FormationHandler;
import com.Patane.handlers.MetaDataHandler;
import com.Patane.runnables.PatRunnable;
import com.Patane.util.YML.Namer;
import com.Patane.util.YML.YMLParsable;
import com.Patane.util.collections.PatCollectable;
import com.Patane.util.general.Check;
import com.Patane.util.general.Messenger;

public class BrEffect extends PatCollectable{
	/**
	 * ******************* STATIC YML SECTION *******************
	 */
	private static BrEffectYML yml;

	public static void setYML(BrEffectYML yml){
		BrEffect.yml = yml;
	}
	public static BrEffectYML YML(){
		return yml;
	}
	/**
	 * **********************************************************
	 */
	private final Modifier modifier;
	private final Trigger trigger;
	private final Integer radius;
	// NEED TO ADD: if radius is 0, projectile must HIT an entity to damage/affect it and only it.
	private final ArrayList<EntityType> entities;
	private final ArrayList<PotionEffect> potionEffects;
	private final BrParticleEffect particleEffect;
	private final BrSoundEffect soundEffect;
	private final BrTag tag;

	private List<String> incomplete = new ArrayList<String>();
	
	public BrEffect(boolean incompleteAllowed, String name, Modifier modifier, Trigger trigger, Integer radius, 
			EntityType[] entities, BrParticleEffect particleEffect, BrSoundEffect soundEffect, PotionEffect[] potionEffects, 
			BrTag tag) {
		// Setting the name
		super(name);
		
		// ESSENTIAL VALUES.
		// modifier, trigger and radius are required, thus will NullPointerException if they are null.
		this.modifier = (incompleteAllowed ? modifier : Check.nulled(modifier, "BrEffect needs more data: '"+name+"' has no modifiers set anywhere. Please check YML files."));
		if(modifier == null) incomplete.add("modifier");
		
		this.trigger = (incompleteAllowed ? trigger : Check.nulled(trigger, "BrEffect needs more data: '"+name+"' has no triggers set anywhere. Please check YML files."));
		if(trigger == null)	incomplete.add("trigger");
		// PROBLEM!
		// Maybe make radius non-essential?
		// If radius isnt set, then it only applies to any entities hit?
		this.radius = (incompleteAllowed ? radius : Check.nulled(radius, "BrEffect needs more data: '"+name+"' has no radius set anywhere. Please check YML files."));
		if(radius == null) incomplete.add("radius");
		
		// NON-ESSENTIAL VALUES.
		// These values can be null. However if the arrays are null, they are converted to empty arrays.
		this.entities = (entities == null ? new ArrayList<EntityType>() : new ArrayList<EntityType>(Arrays.asList(entities)));
		this.particleEffect = particleEffect;
		this.soundEffect = soundEffect;
		this.potionEffects = (potionEffects == null ? new ArrayList<PotionEffect>() : new ArrayList<PotionEffect>(Arrays.asList(potionEffects)));
		this.tag = tag;
	}
	
	// Getters for essential values.
	public Modifier getModifier() {
		return modifier;
	}
	public Trigger getTrigger() {
		return trigger;
	}
	public Integer getRadius() {
		return radius;
	}
	
	// Has & Getters for non-essential values.
	
	// Entities
	public boolean hasEntities() {
		return (entities.isEmpty() ? false : true);
	}
	public ArrayList<EntityType> getEntities() {
		return entities;
	}
	public EntityType[] getEntitiesArray(){
		return entities.toArray(new EntityType[entities.size()]);
	}
	
	// Particle
	public boolean hasParticle() {
		return (particleEffect == null ? false : true);
	}
	public BrParticleEffect getParticleEffect() {
		return particleEffect;
	}

	// Sound
	public boolean hasSound() {
		return (soundEffect == null ? false : true);
	}
	public BrSoundEffect getSoundEffect() {
		return soundEffect;
	}

	// Potion Effects
	public boolean hasPotions() {
		return (potionEffects.isEmpty() ? false : true);
	}
	public ArrayList<PotionEffect> getPotions(){
		return potionEffects;
	}
	public PotionEffect[] getPotionsArray(){
		return potionEffects.toArray(new PotionEffect[potionEffects.size()]);
	}
	
	public void execute(LivingEntity executor, Location impact){
		trigger.execute(this, impact, executor);
	}
	
	// Tags
	public void applyTag(PatRunnable run, List<LivingEntity> entities) {
		if(tag != null)
			tag.apply(run, entities);
	}
	public void clearTag(PatRunnable run) {
		if(tag != null)
			tag.clear(run);
	}
	// Completed
	public boolean isComplete() {
		return incomplete.isEmpty();
	}
	// Get incompleted essential values
	public List<String> getIncomplete(){
		return incomplete;
	}

/*
 *  PARTICLE EFFECTS
 */
	@Namer(name = "Particle Effect")
	public static class BrParticleEffect extends YMLParsable{
		final public Particle type;
		final public Formation formation;
		final public int intensity;
		final public double velocity;

		public BrParticleEffect(Map<String, String> fields){
			this.type = getEnumValue(Particle.class, fields, "type");
			this.formation = Check.nulled(FormationHandler.get(fields.get("formation")), "Formation '"+fields.get("formation")+"' could not be found.");
			try{ this.intensity = getInt(fields, "intensity");
			} catch (IllegalArgumentException e){
				Messenger.warning("Particle Effect failed to load:");
				throw e;
			}
			try{
				this.velocity = getDouble(fields, "velocity");
			} catch (IllegalArgumentException e){
				Messenger.warning("Particle Effect failed to load:");
				throw e;
			}
		}
		public BrParticleEffect(Particle type, Formation formation, int intensity, double velocity){
			this.type = type;
			this.formation = formation;
			this.intensity = intensity;
			this.velocity = velocity;
		}
		public void spawn(Location location, int radius){
			spawn(location, radius, intensity, velocity);
		}
		public void spawn(Location location, int radius, int intensity){
			spawn(location, radius, intensity, velocity);
		}
		public void spawn(Location location, int radius, int intensity, double velocity){
			double offset = radius/2;
			location.getWorld().spawnParticle(type, location, Math.min(Integer.MAX_VALUE, (int) Math.pow(radius, 3)*intensity), offset,offset,offset, velocity);
		}
		public Formation getFormation(){
			return formation;
		}
	}

/*
 *  SOUND EFFECTS
 */
	@Namer(name = "Sound Effect")
	public static class BrSoundEffect extends YMLParsable{
		final public Sound type;
//		final public Formation formation;
		final public float volume;
		final public float pitch;

		public BrSoundEffect(Map<String, String> fields){
			this.type = getEnumValue(Sound.class, fields, "type");
//			this.formation = getEnumValue(Formation.class, fields, "formation");
			this.volume = (float) getDouble(fields, "volume", 100);
			this.pitch = (float) getDouble(fields, "pitch", 1);
		}
		public BrSoundEffect(Sound type, float volume, float pitch){
			this.type = type;
//			this.formation = formation;
			this.volume = volume;
			this.pitch = pitch;
		}
		public void spawn(Location location){
			location.getWorld().playSound(location, type, volume, pitch);
		}
	}

/*
 *  TAGS
 */
	@Namer(name = "Tag")
	public static class BrTag extends YMLParsable{
		final public String name;

		public BrTag(Map<String, String> fields){
			this.name = getString(fields, "name");
		}
		public BrTag(Particle type, String name, float duration){
			this.name = name;
		}
		

		public void apply(PatRunnable task, List<LivingEntity> entities){
			BrMetaDataHandler.addClean(task, entities, MetaDataHandler.id("tag", name), null);
		}
		
		public void clear(PatRunnable task){
			BrMetaDataHandler.remove(task, MetaDataHandler.id("tag", name));
		}
	}
}
