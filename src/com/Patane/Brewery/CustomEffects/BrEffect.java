package com.Patane.Brewery.CustomEffects;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.LivingEntity;
import org.bukkit.potion.PotionEffect;

import com.Patane.Brewery.Handlers.BrMetaDataHandler;
import com.Patane.Brewery.Handlers.FormationHandler;
import com.Patane.runnables.PatRunnable;
import com.Patane.util.YAML.MapParsable;
import com.Patane.util.YAML.Namer;
import com.Patane.util.collections.PatCollectable;
import com.Patane.util.general.Chat;
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
	private final Float radius;
	// NEED TO ADD: if radius is 0, projectile must HIT an entity to damage/affect it and only it.
	private final Filter filter;
	private final List<PotionEffect> potionEffects;
	private final BrParticleEffect particleEffect;
	private final BrSoundEffect soundEffect;
	private final BrTag tag;
	private final boolean ignore_user;
	private final boolean stack;

	private List<String> incomplete = new ArrayList<String>();
	
	public BrEffect(boolean incompleteAllowed, String name, Modifier modifier, Trigger trigger, Float radius, 
			Filter filter, BrParticleEffect particleEffect, BrSoundEffect soundEffect, List<PotionEffect> potionEffects, 
			BrTag tag, Boolean ignore_user) {
		// Setting the name
		super(name);
		
		// ESSENTIAL VALUES.
		// modifier, trigger and radius are required, thus will NullPointerException if they are null.
		this.modifier = (incompleteAllowed ? modifier : Check.notNull(modifier, "BrEffect needs more data: '"+name+"' has no modifiers set anywhere. Please check YML files."));
		if(modifier == null) incomplete.add("modifier");
		
		this.trigger = (incompleteAllowed ? trigger : Check.notNull(trigger, "BrEffect needs more data: '"+name+"' has no triggers set anywhere. Please check YML files."));
		if(trigger == null)	incomplete.add("trigger");
		
		// NON-ESSENTIAL VALUES.
		// These values can be null. However if some objects are null, they are converted to empty objects.
		// If radius isnt set, then it only applies to any entities hit?
		this.radius = radius;
		this.filter = (filter == null ? new Filter() : filter);
		this.particleEffect = particleEffect;
		this.soundEffect = soundEffect;
		this.potionEffects = (potionEffects == null ? new ArrayList<PotionEffect>() : potionEffects);
		this.tag = tag;
		
		// DEFAULTED VALUES.
		// These values will always have a setting. If the given value is null, then the default is set.
		this.ignore_user = (ignore_user == null ? true : ignore_user);
		this.stack = false;
	}
	
	// Getters for essential values.
	public Modifier getModifier() {
		return modifier;
	}
	public Trigger getTrigger() {
		return trigger;
	}
	
	// Has & Getters for non-essential values.
	
	// Radius
	public boolean hasRadius() {
		return(radius == null ? false : true);
	}
	public Float getRadius() {
		return radius;
	}
	
	// Filter
	public boolean hasFilter() {
		return !filter.noFilters();
	}
	public Filter getFilter() {
		return filter;
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
	public List<PotionEffect> getPotions(){
		return potionEffects;
	}
	public PotionEffect[] getPotionsArray(){
		return potionEffects.toArray(new PotionEffect[potionEffects.size()]);
	}
	
	// Tags
	public boolean hasTag() {
		return (tag == null ? false : true);
	}
	public BrTag getTag() {
		return tag;
	}
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

	// Getters for Defaulted values.
	
	// Ignore_User
	public boolean ignoreUser() {
		return ignore_user;
	}
	// Stacks
	public boolean stack() {
		return stack;
	}
	/**
	 * Executes the effect on an impact location using the effects radius.
	 * @param executor LivingEntity who is executing the effect.
	 * @param impact Location to trigger effect (with effects radius).
	 */
	public boolean execute(Location impact, LivingEntity executor){
		try {
			trigger.execute(this, impact, executor);
			return true;
		} catch(Exception e) {
			Messenger.warning("Failed to execute '"+getID()+"' effect onto specific Location:");
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * Executes the effect on a single LivingEntity target.
	 * The radius can still apply here.
	 * On the event of a lingering effect, the lingering location should be updated with the targets location at the time.
	 * @param executor LivingEntity who is executing the effect.
	 * @param target LivingEntity who is hit by the effect.
	 */
	public boolean execute(LivingEntity executor, LivingEntity target){
		try {
			trigger.execute(this, executor, target);
			return true;
		} catch(Exception e) {
			Messenger.warning("Failed to execute '"+getID()+"' effect onto specific Living Entity:");
			e.printStackTrace();
			return false;
		}
	}

	public String hoverDetails() {
		return Chat.translate("&7"+getName()
		+"\n&2Modifier: &a"+(getModifier() != null ? getModifier().name() : "&cUndefined")
		+"\n&2Trigger: &a"+(getTrigger() != null ? getTrigger().name() : "&cUndefined")
		+(hasRadius() ? "\n&2Radius: &a"+getRadius() : "")
		+(hasTag() ? "\n&2Tag: &a"+getTag().name : "")
		+(hasParticle() ? "\n&2Particles: &aApplied" : "")
		+(hasSound() ? "\n&2Sounds: &aApplied" : "")
		+(hasPotions() ? "\n&2Potion Effects: &a"+getPotions().size() : "")
		+(hasFilter() ? "\n&2Filters: &aApplied" : ""));
	}
/*
 *  PARTICLE EFFECTS
 */
	@Namer(name = "Particle Effect")
	public static class BrParticleEffect extends MapParsable{
		final public Particle type;
		final public Formation formation;
		final public int intensity;
		final public double velocity;

		public BrParticleEffect(Map<String, String> fields){
			this.type = getEnumValue(Particle.class, fields, "type");
			this.formation = Check.notNull(FormationHandler.get(fields.get("formation")), "Formation '"+fields.get("formation")+"' could not be found.");
			try{ 
				this.intensity = getInt(fields, "intensity");
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
		public void spawn(Location location, float radius){
			spawn(location, radius, intensity, velocity);
		}
		public void spawn(Location location, float radius, int intensity){
			spawn(location, radius, intensity, velocity);
		}
		public void spawn(Location location, float radius, int intensity, double velocity){
//			if(radius == null)
//				radius = 0.5f;
			double offset = radius/2;
			location.getWorld().spawnParticle(type, location, Math.min(Integer.MAX_VALUE, (int) Math.pow(Math.max(radius, 1), 3)*intensity), offset,offset,offset, velocity);
		}
		public Formation getFormation(){
			return formation;
		}
	}

/*
 *  SOUND EFFECTS
 */
	@Namer(name = "Sound Effect")
	public static class BrSoundEffect extends MapParsable{
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
	public static class BrTag extends MapParsable{
		final public String name;
		
		public BrTag(Map<String, String> fields){
			this.name = getString(fields, "name");
		}
		public BrTag(Particle type, String name, float duration){
			this.name = name;
		}
		

		public void apply(PatRunnable task, List<LivingEntity> entities){
			BrMetaDataHandler.addClean(task, entities, "TAG", name);
		}
		
		public void clear(PatRunnable task){
			BrMetaDataHandler.remove(task, "TAG");
		}
	}
}
