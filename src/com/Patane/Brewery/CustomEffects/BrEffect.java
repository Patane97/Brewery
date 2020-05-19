package com.Patane.Brewery.CustomEffects;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.LivingEntity;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.Patane.Brewery.Handlers.BrMetaDataHandler;
import com.Patane.Brewery.Handlers.FormationHandler;
import com.Patane.runnables.PatRunnable;
import com.Patane.util.YAML.Namer;
import com.Patane.util.YAML.TypeParsable;
import com.Patane.util.YAML.Typer;
import com.Patane.util.collections.PatCollectable;
import com.Patane.util.general.Chat;
import com.Patane.util.general.Check;
import com.Patane.util.general.Messenger;
import com.Patane.util.general.StringsUtil;
import com.Patane.util.general.StringsUtil.LambdaStrings;

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
	protected Modifier modifier;
	protected Trigger trigger;
	protected Float radius;
	protected Filter filter;
	protected BrParticleEffect particles;
	protected BrSoundEffect sounds;
	protected List<PotionEffect> potion_effects;// *** Change to set OR standard Array
	protected BrTag tag;
	protected boolean ignore_user;
//	protected boolean stack;

	protected List<String> incomplete = new ArrayList<String>();
	
	public BrEffect(String name, Modifier modifier, Trigger trigger, Float radius, 
			Filter filter, BrParticleEffect particles, BrSoundEffect sounds, List<PotionEffect> potion_effects, 
			BrTag tag, Boolean ignore_user) {
		// Setting the name
		super(name);
		
		// ESSENTIAL VALUES.
		// modifier, trigger and radius are required, thus will NullPointerException if they are null.
		this.modifier = modifier;
		if(modifier == null) incomplete.add("modifier");
		
		this.trigger = trigger;
		if(trigger == null)	incomplete.add("trigger");
		
		// NULLABLE VALUES.
		// These values can be null. However if some objects are null, they are converted to empty objects.
		// If radius isnt set, then it only applies to any entities hit?
		this.radius = radius;
		this.particles = particles;
		this.sounds = sounds;
		this.tag = tag;
		
		// DEFAULTED VALUES.
		// These values will always have a setting. If the given value is null, then the default is set.
		this.filter = (filter == null ? new Filter() : filter);
		this.potion_effects = (potion_effects == null ? new ArrayList<PotionEffect>() : potion_effects);
		this.ignore_user = (ignore_user == null ? true : ignore_user);
//		this.stack = false;
		if(!isComplete())
			Messenger.info(name+" effect is incomplete. Missing: "+StringsUtil.stringJoiner(incomplete, ", "));
	}
	
	// Getters for essential values.
	public Modifier getModifier() {
		return modifier;
	}
	public void setModifier(Modifier modifier) {
		this.modifier = modifier;
		if(incomplete.contains("modifier") && this.modifier != null)
			incomplete.remove("modifier");
	}
	public Trigger getTrigger() {
		return trigger;
	}
	public void setTrigger(Trigger trigger) {
		this.trigger = trigger;
		if(incomplete.contains("trigger") && this.trigger != null)
			incomplete.remove("trigger");
		
	}
	
	// Has & Getters for non-essential values.
	
	// Radius
	public boolean hasRadius() {
		return(radius == null ? false : true);
	}
	public Float getRadius() {
		return radius;
	}
	public void setRadius(Float radius) {
		this.radius = radius;
	}
	// Filter
	public boolean hasFilter() {
		return !filter.noFilters();
	}
	public Filter getFilter() {
		return filter;
	}
	public void setFilter(Filter filter) {
		this.filter = filter;
	}
	// Particle
	public boolean hasParticle() {
		return (particles == null ? false : true);
	}
	public BrParticleEffect getParticleEffect() {
		return particles;
	}
	public void setParticle(BrParticleEffect particles) {
		this.particles = particles;
	}
	
	
	// Sound
	public boolean hasSound() {
		return (sounds == null ? false : true);
	}
	public BrSoundEffect getSoundEffect() {
		return sounds;
	}
	public void setSound(BrSoundEffect sounds) {
		this.sounds = sounds;
	}

	// Potion Effects
	public boolean hasPotions() {
		return (potion_effects.isEmpty() ? false : true);
	}
	public List<PotionEffect> getPotions(){
		return potion_effects;
	}
	public void setPotions(List<PotionEffect> potionEffects) {
		this.potion_effects = potionEffects;
	}
	public void addPotion(PotionEffect potionEffect) {
		potion_effects.add(potionEffect);
	}
	public void removePotions(PotionEffectType potionEffectType) {
		List<PotionEffect> newPotionEffects = new ArrayList<PotionEffect>();
		for(PotionEffect potionEffect : potion_effects) {
			if(!potionEffect.getType().getName().equals(potionEffectType.getName()))
				newPotionEffects.add(potionEffect);
		}
		setPotions(newPotionEffects);
	}
	
	// Tags
	public boolean hasTag() {
		return (tag == null ? false : true);
	}
	public BrTag getTag() {
		return tag;
	}
	public void setTag(String tag) {
		this.tag = new BrTag(tag);
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

	public void setIgnoreUser(boolean ignore_user) {
		this.ignore_user = ignore_user;		
	}
	// Stacks
//	public boolean stack() {
//		return stack;
//	}
	
	/**
	 * Returns this effect in a formatted state appropriate for Chat or HoverText (using \n for new lines)
	 * @param layout Layout to stick to. If the string needs to be indented outside of the usual, add indentation to the layout.
	 * @param deep True to include all details of each value. False for just a simple overview of the effect
	 * @return Single string containing all information of this Effect
	 */
	public String toChatString(LambdaStrings title, LambdaStrings layout, boolean deep) {
		// Starting with the effect name as a bolded title
		String effectInfo = title.build(getName());
		
		// Saving the modifier and trigger strings now as they can be null
		String modifier = StringsUtil.typeParsToChatString(layout, getModifier(), deep);
		String trigger = StringsUtil.typeParsToChatString(layout, getTrigger(), deep);
		
		// Adding the modifier and trigger strings. If they are null, show as "undefined"
		effectInfo += "\n" + (modifier == null ? layout.build("Modifier", "&8Undefined") : modifier);
		effectInfo += "\n" + (trigger == null ? layout.build("Trigger", "&8Undefined") : trigger);
		
		// If radius is present, show it
		if(hasRadius())
			effectInfo += "\n" + layout.build("Radius", getRadius().toString());
		
		// If tag is present, show it
		if(hasTag())
			effectInfo += "\n" + layout.build("Tag", getTag().name);

		// If ignore_user is present, show it
		if(ignoreUser() == false)
			effectInfo += "\n" + layout.build("Ignore User", "false");
		
		// If potions are present, show them
		if(hasPotions()) {
			// If not deep, then simply print potion count
			if(!deep)
				effectInfo += "\n" + layout.build("Potion Effects", Integer.toString(getPotions().size()));
			// Otherwise grab all potion effect infos from the string builder
			else
				effectInfo += "\n" + layout.build("Potion Effects", "") 
				+ "\n" + StringsUtil.toChatString(s -> "&2  "+s[0]+": &7"+s[1], getPotions().toArray(new PotionEffect[0]));
		}
		
		// If particles are present, show them. Does not need null check as we know we have them
		if(hasParticle())
			effectInfo += "\n" + StringsUtil.typeParsToChatString(layout, getParticleEffect(), deep);

		// If sounds are present, show them. Does not need null check as we know we have them
		if(hasSound())
			effectInfo += "\n" + StringsUtil.typeParsToChatString(layout, getSoundEffect(), deep);
		
		// If filters are present, show them.
		if(hasFilter()) {
			// If not deep, print Filter followed by Target (white TICK) count and Ignore (dark grey CROSS) count
			if(!deep)
				//\u2714 = BOLD TICK
				//\u2718 = BOLD CROSS
				effectInfo += "\n" + layout.build("Filter", "Active (&f\u2714&7"+getFilter().getTarget().getSize()+" &8\u2718&7"+getFilter().getIgnore().getSize()+")");
			// Otherwise grab filters in groups (Entities, Players, Permissions, Tags) and show if they are Target or Ignore through Tick or Cross
			else
				effectInfo += "\n" + layout.build("Filter", "") 
				+ getFilter().toChatString(layout);
	
		}
		return effectInfo;
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
			Messenger.warning("Failed to execute '"+getName()+"' effect onto specific Location:");
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
			Messenger.warning("Failed to execute '"+getName()+"' effect onto specific Living Entity:");
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
	@Typer(type = "Particles")
	public static class BrParticleEffect extends TypeParsable{
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
	@Typer(type = "Sounds")
	public static class BrSoundEffect extends TypeParsable{
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
	@Typer(type = "Tag")
	public static class BrTag extends TypeParsable{
		final public String name;
		
		public BrTag(Map<String, String> fields){
			this.name = getString(fields, "name");
		}
		public BrTag(String name){
			this.name = name;
		}
		

		public void apply(PatRunnable task, List<LivingEntity> entities){
			BrMetaDataHandler.addClean(task, entities, "TAG", name);
		}
		
		public void clear(PatRunnable task){
			BrMetaDataHandler.remove(task, "TAG");
		}
	}

	public static String manyToChatString(LambdaStrings title, LambdaStrings layout, boolean deep, BrEffect... effects) {
		String effectsString = "";
		if(effects.length == 0)
			return "&8Nothing here!";
		for(BrEffect effect : effects) {
			if(effect != effects[0])
				effectsString += "\n\n";
			effectsString += effect.toChatString(title, layout, deep);
		}
		return effectsString;
	}
}
