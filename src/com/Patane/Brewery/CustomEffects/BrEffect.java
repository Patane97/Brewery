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
import com.Patane.util.YAML.MapParsable;
import com.Patane.util.YAML.Namer;
import com.Patane.util.collections.ChatCollectable;
import com.Patane.util.general.Chat;
import com.Patane.util.general.Check;
import com.Patane.util.general.Messenger;
import com.Patane.util.general.StringsUtil;
import com.Patane.util.general.StringsUtil.LambdaStrings;

public class BrEffect extends ChatCollectable{
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
	 * ***************** STATIC METHODS SECTION *****************
	 */
	
	/**
	 * Gets many effects to a single string in default format
	 * @param indentCount
	 * @param deep
	 * @param effects
	 * @return
	 */
	public static String manyToChatString(int indentCount, boolean deep, BrEffect... effects) {
		String effectsString = "";
		if(effects.length == 0)
			return "&8Nothing here!";
		for(BrEffect effect : effects) {
			if(effect != effects[0])
				effectsString += "\n\n";
			effectsString += effect.toChatString(indentCount, deep);
		}
		return effectsString;
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
	protected List<PotionEffect> potion_effects;
	protected BrTag tag;
	protected boolean ignore_user;
//	protected boolean stack;

	protected List<String> incomplete = new ArrayList<String>();

	protected LambdaStrings title = s -> "&f&l"+s[0];

	/* ================================================================================
	 * Constructors
	 * ================================================================================
	 */
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

	/* ================================================================================
	 * Setters, Getters and Has...ers
	 * ================================================================================
	 */
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
		return filter.isActive();
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
		sortPotionsByAmplifier();
	}
	public void addPotion(PotionEffect potionEffect) {
		potion_effects.add(potionEffect);
		sortPotionsByAmplifier();
		
	}
	/**
	 * Sorting the potions by amplifier is important when layering multiple potions of the same type
	 * For some reason, minecraft will not apply a potion of lesser amplification if a potion of greater amplification of the same TYPE is currently applied.
	 * Applying the smaller amps first allows all potions to be applied, independant of their amplification!
	 */
	private void sortPotionsByAmplifier() {
		potion_effects.sort((p1,p2) -> {
			if(p1.getAmplifier() < p2.getAmplifier())
				return -1;
			if(p1.getAmplifier() == p2.getAmplifier())
				return 0;
			return 1;
		});
	}
	public void removePotions(PotionEffectType potionEffectType) {
		List<PotionEffect> newPotionEffects = new ArrayList<PotionEffect>();
		for(PotionEffect potionEffect : potion_effects) {
			if(!potionEffect.getType().getName().equals(potionEffectType.getName()))
				newPotionEffects.add(potionEffect);
		}
		setPotions(newPotionEffects);
	}
	
	public void removePotion(PotionEffect potionEffect) {
		potion_effects.remove(potionEffect);
		// Dont need to sort here as its already in correct order
	}
	
	// Tags
	public boolean hasTag() {
		return (tag == null ? false : true);
	}
	public BrTag getTag() {
		return tag;
	}
	public void setTag(BrTag tag) {
		this.tag = tag;
	}
	public void applyTag(PatRunnable run, List<LivingEntity> entities) {
		if(tag != null)
			tag.apply(run, entities);
	}
	public void clearTag(PatRunnable run) {
		if(tag != null)
			tag.clear(run);
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

	/* ================================================================================
	 * Completion-check
	 * ================================================================================
	 */
	// Completed
	public boolean isComplete() {
		return incomplete.isEmpty();
	}
	// Get incompleted essential values
	public List<String> getIncomplete(){
		return incomplete;
	}


	/* ================================================================================
	 * ChatStringable Methods
	 * ================================================================================
	 */
	@Override
	public LambdaStrings layout(){
		// Example: &2Type: &7Name
		return s -> "&2"+s[0]+"&2: &7"+s[1];
	}
	/**
	 * Returns this effect in a formatted state appropriate for Chat or HoverText (using \n for new lines)
	 * @param layout Layout to stick to. If the string needs to be indented outside of the usual, add indentation to the layout.
	 * @param deep True to include all details of each value. False for just a simple overview of the effect
	 * @param alternateLayout layout to fit this effect to
	 * @return A chat-friendly string containing all information of this Effect
	 */
	@Override
	public String toChatString(int indentCount, boolean deep, LambdaStrings alternateLayout) {
		// USEFUL SECTION TO COPY TO OTHER TOCHATSTRINGS!
		// If alternatelayout is null, then keep deepLayout as null as it means deeper ChatStringables use their default layout as well
		LambdaStrings deepLayout = alternateLayout;
		// If the alternateLayout is null, we want to use the default layout for itself
		alternateLayout = (alternateLayout == null ? layout() : alternateLayout);
		// //////////////////////////////////////////////
		
		// Starting with the effect name as a bolded title
		String effectInfo = title.build(getName());
		
		// Saving the modifier and trigger strings. If they are null, show as "&8Undefined"
		String modifier = (this.modifier != null ? this.modifier.toChatString(indentCount, deep, deepLayout) : alternateLayout.build("Modifier", "&8Undefined"));
		String trigger = (this.trigger != null ? this.trigger.toChatString(indentCount, deep, deepLayout) : alternateLayout.build("Trigger", "&8Undefined"));
		
		// Adding the modifier and trigger strings. 
		effectInfo += "\n" + modifier;
		effectInfo += "\n" + trigger;
		// If radius is present, show it
		if(hasRadius())
			effectInfo += "\n" + Chat.indent(indentCount) + alternateLayout.build("Radius", radius.toString());
		
		// If tag is present, show it
		if(hasTag())
			effectInfo += "\n" + Chat.indent(indentCount) + alternateLayout.build("Tag", getTag().name);

		// If ignore_user is present, show it
		if(ignoreUser() == false)
			effectInfo += "\n" + Chat.indent(indentCount) + alternateLayout.build("Ignore User", "false");
		
		// If potions are present, show them
		if(hasPotions())
			effectInfo += "\n" + StringsUtil.toChatString(indentCount, deep, deepLayout, potion_effects.toArray(new PotionEffect[0]));
//		{
//			// If not deep, then simply print potion count
//			if(!deep)
//				effectInfo += "\n" + Chat.indent(indentCount) + alternateLayout.build("Potion Effects", Integer.toString(potion_effects.size()));
//			// Otherwise grab all potion effect infos from the string builder
//			else
//				effectInfo += "\n" + Chat.indent(indentCount) + alternateLayout.build("Potion Effects", "")
//				// *** CHANGE THIS?
//				+ "\n" + StringsUtil.toChatString(s -> "&2  "+s[0]+": &7"+s[1], potion_effects.toArray(new PotionEffect[0]));
//		}
		
		// If particles are present, show them. Does not need null check as we know we have them
		if(hasParticle())
			effectInfo += "\n" + particles.toChatString(indentCount, deep, deepLayout);

		// If sounds are present, show them. Does not need null check as we know we have them
		if(hasSound())
			effectInfo += "\n" + sounds.toChatString(indentCount, deep, deepLayout);
		
		// If filters are present, show them.
		if(hasFilter())
			effectInfo += "\n" + filter.toChatString(indentCount, deep, deepLayout);
		
		return effectInfo;
	}
	/* ================================================================================
	 * Effect Executors
	 * ================================================================================
	 */
	
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

	/* ================================================================================
	 * *** OLD, TO BE REPLACED COMPLETELY BY TOCHATSTRING
	 * ================================================================================
	 */
	@Deprecated
	public String hoverDetails() {
		return Chat.translate("&7"+getName()
		+"\n&2Modifier: &a"+(getModifier() != null ? getModifier().className() : "&cUndefined")
		+"\n&2Trigger: &a"+(getTrigger() != null ? getTrigger().className() : "&cUndefined")
		+(hasRadius() ? "\n&2Radius: &a"+getRadius() : "")
		+(hasTag() ? "\n&2Tag: &a"+getTag().name : "")
		+(hasParticle() ? "\n&2Particles: &aApplied" : "")
		+(hasSound() ? "\n&2Sounds: &aApplied" : "")
		+(hasPotions() ? "\n&2Potion Effects: &a"+getPotions().size() : "")
		+(hasFilter() ? "\n&2Filters: &aApplied" : ""));
	}

/* ================================================================================
 * Effect Specific Classes
 * ================================================================================
 */
	/* ================================================================================
	 * Particle Effects
	 * ================================================================================
	 */
	@Namer(name = "Particle Effect")
	public static class BrParticleEffect extends MapParsable{
		public Particle type;
		public Formation formation;
		public int intensity;
		public double velocity;
		
		public BrParticleEffect() {
			super();
		}
		
		public BrParticleEffect(Map<String, String> fields) {
			super(fields);
		}		

		@Override
		protected void populateFields(Map<String, String> fields) {
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
			construct();
		}
		@Override
		public LambdaStrings layout(){
			// Example: &2Type: &7Name
			return s -> "&2"+s[0]+"&2: &7"+s[1];
		}
		@Override
		public String toChatString(int indentCount, boolean deep, LambdaStrings alternateLayout) {
			alternateLayout = (alternateLayout == null ? layout() : alternateLayout);
			if(!deep)
				return Chat.indent(indentCount)+alternateLayout.build(className(), "&8Active");
			return Chat.indent(indentCount)+alternateLayout.build(className(), "")
					+ super.toChatString(indentCount, deep, alternateLayout);
		}
		public Formation getFormation(){
			return formation;
		}
		
		
		/* 
		 * ================================================================================
		 */
		
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
	}
	/* ================================================================================
	 * Sound Effects
	 * ================================================================================
	 */
	@Namer(name = "Sound Effect")
	public static class BrSoundEffect extends MapParsable{
		public Sound type;
//		public Formation formation;
		public float volume;
		public float pitch;
		
		public BrSoundEffect() {
			super();
		}
		
		public BrSoundEffect(Map<String, String> fields) {
			super(fields);
		}

		@Override
		protected void populateFields(Map<String, String> fields){
			this.type = getEnumValue(Sound.class, fields, "type");
	//		this.formation = getEnumValue(Formation.class, fields, "formation");
			this.volume = (float) getDouble(fields, "volume", 100);
			this.pitch = (float) getDouble(fields, "pitch", 1);
		}
		public BrSoundEffect(Sound type, float volume, float pitch){
			this.type = type;
//			this.formation = formation;
			this.volume = volume;
			this.pitch = pitch;
			construct();
		}
		
		@Override
		public LambdaStrings layout(){
			// Example: &2Type: &7Name
			return s -> "&2"+s[0]+"&2: &7"+s[1];
		}
		@Override
		public String toChatString(int indentCount, boolean deep, LambdaStrings alternateLayout) {
			alternateLayout = (alternateLayout == null ? layout() : alternateLayout);

			if(!deep)
				return Chat.indent(indentCount)+alternateLayout.build(className(), "&8Active");
			return Chat.indent(indentCount)+alternateLayout.build(className(), "")
					+ super.toChatString(indentCount, deep, alternateLayout);
		}

		/* 
		 * ================================================================================
		 */
		
		public void spawn(Location location){
			location.getWorld().playSound(location, type, volume, pitch);
		}
	}
	/* ================================================================================
	 * Tags
	 * ================================================================================
	 */
	@Namer(name = "Tag")
	public static class BrTag extends MapParsable{
		public String name;
		
		public BrTag() {
			super();
		}
		
		public BrTag(Map<String, String> fields) {
			super(fields);
		}

		@Override
		protected void populateFields(Map<String, String> fields) {
			this.name = getString(fields, "name");
		}
		public BrTag(String name){
			this.name = name;
			construct();
		}
		@Override
		public LambdaStrings layout(){
			// Example: &2Type: &7Name
			return s -> "&2"+s[0]+"&2: &7"+s[1];
		}
		@Override
		public String toChatString(int indentCount, boolean deep, LambdaStrings alternateLayout) {
			alternateLayout = (alternateLayout == null ? layout() : alternateLayout);
			
			return Chat.indent(indentCount) + alternateLayout.build(className(), name);
		}

		/* 
		 * ================================================================================
		 */

		public void apply(PatRunnable task, List<LivingEntity> entities){
			BrMetaDataHandler.addClean(task, entities, "TAG", name);
		}
		
		public void clear(PatRunnable task){
			BrMetaDataHandler.remove(task, "TAG");
		}
	}
}
