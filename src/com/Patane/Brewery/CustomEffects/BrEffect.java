package com.Patane.Brewery.CustomEffects;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.LivingEntity;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.Patane.Brewery.Handlers.BrMetaDataHandler;
import com.Patane.runnables.PatRunnable;
import com.Patane.util.YAML.MapParsable;
import com.Patane.util.annotations.ClassDescriber;
import com.Patane.util.annotations.FieldDescriber;
import com.Patane.util.collections.ChatCollectable;
import com.Patane.util.general.Chat;
import com.Patane.util.general.Messenger;
import com.Patane.util.general.StringsUtil;
import com.Patane.util.general.StringsUtil.LambdaStrings;

import net.md_5.bungee.api.chat.TextComponent;

public class BrEffect extends ChatCollectable{
	/**
	 * ******************* STATIC YML SECTION *******************
	 */
	private static BrEffectYML yml;

	public static void setYML(BrEffectYML yml) {
		BrEffect.yml = yml;
	}
	public static BrEffectYML YML() {
		return yml;
	}
	/**
	 * ***************** STATIC METHODS SECTION *****************
	 */
	
//	/**
//	 * Gets many effects to a single string in default format
//	 * @param indentCount
//	 * @param deep
//	 * @param effects
//	 * @return
//	 */
//	public static String manyToChatString(int indentCount, boolean deep, BrEffect... effects) {
//		String effectsString = "";
//		if(effects.length == 0)
//			return "&8Nothing here!";
//		for(BrEffect effect : effects) {
//			if(effect != effects[0])
//				effectsString += "\n\n";
//			effectsString += effect.toChatString(indentCount, deep);
//		}
//		return effectsString;
//	}
	/**
	 * **********************************************************
	 */
	protected Modifier modifier;
	protected Trigger trigger;
	protected Float radius;
	protected Filter filter;
	protected BrParticleEffect particles;
	protected BrSoundEffect sounds;
	protected List<PotionEffect> potionEffects;
	protected BrTag tag;
	protected boolean ignoreUser;
//	protected boolean stack;

	protected List<String> incomplete = new ArrayList<String>();

	protected LambdaStrings title = s -> "&f&l"+s[0]+"&r";

	/* ================================================================================
	 * Constructors
	 * ================================================================================
	 */
	public BrEffect(String name, Modifier modifier, Trigger trigger, Float radius, 
			Filter filter, BrParticleEffect particles, BrSoundEffect sounds, List<PotionEffect> potionEffects, 
			BrTag tag, Boolean ignoreUser) {
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
		this.potionEffects = (potionEffects == null ? new ArrayList<PotionEffect>() : potionEffects);
		this.ignoreUser = (ignoreUser == null ? true : ignoreUser);
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
		else if(!incomplete.contains("modifier") && this.modifier == null)
			incomplete.add("modifier");
	}
	public Trigger getTrigger() {
		return trigger;
	}
	public void setTrigger(Trigger trigger) {
		this.trigger = trigger;
		if(incomplete.contains("trigger") && this.trigger != null)
			incomplete.remove("trigger");
		else if(!incomplete.contains("trigger") && this.trigger == null)
			incomplete.add("trigger");
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
		this.filter = (filter == null ? new Filter() : filter);
	}
	// Particle
	public boolean hasParticle() {
		return (particles == null ? false : true);
	}
	public BrParticleEffect getParticleEffect() {
		return particles;
	}
	public void setParticleEffect(BrParticleEffect particles) {
		this.particles = particles;
	}
	// Sound
	public boolean hasSound() {
		return (sounds == null ? false : true);
	}
	public BrSoundEffect getSoundEffect() {
		return sounds;
	}
	public void setSoundEffect(BrSoundEffect sounds) {
		this.sounds = sounds;
	}

	// Potion Effects
	public boolean hasPotions() {
		return (potionEffects.isEmpty() ? false : true);
	}
	public List<PotionEffect> getPotions() {
		return potionEffects;
	}
	public void setPotions(List<PotionEffect> potionEffects) {
		this.potionEffects = (potionEffects == null ? new ArrayList<PotionEffect>() : potionEffects);
		sortPotionsByAmplifier();
	}
	public void addPotion(PotionEffect potionEffect) {
		potionEffects.add(potionEffect);
		sortPotionsByAmplifier();
		
	}
	/**
	 * Sorting the potions by amplifier is important when layering multiple potions of the same type
	 * For some reason, minecraft will not apply a potion of lesser amplification if a potion of greater amplification of the same TYPE is currently applied.
	 * Applying the smaller amps first allows all potions to be applied, independant of their amplification!
	 */
	private void sortPotionsByAmplifier() {
		potionEffects.sort((p1,p2) -> {
			if(p1.getAmplifier() < p2.getAmplifier())
				return -1;
			if(p1.getAmplifier() == p2.getAmplifier())
				return 0;
			return 1;
		});
	}
	@Deprecated
	public void removePotions(PotionEffectType potionEffectType) {
		List<PotionEffect> newPotionEffects = new ArrayList<PotionEffect>();
		for(PotionEffect potionEffect : potionEffects) {
			if(!potionEffect.getType().getName().equals(potionEffectType.getName()))
				newPotionEffects.add(potionEffect);
		}
		setPotions(newPotionEffects);
	}
	
	public void removePotion(PotionEffect potionEffect) {
		potionEffects.remove(potionEffect);
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
	
	// IgnoreUser
	public boolean ignoreUser() {
		return ignoreUser;
	}

	public void setIgnoreUser(Boolean ignoreUser) {
		this.ignoreUser = (ignoreUser == null ? true : ignoreUser);	
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
	public List<String> getIncomplete() {
		return incomplete;
	}


	/* ================================================================================
	 * ChatStringable & ChatHoverable Methods
	 * ================================================================================
	 */
	@Override
	public LambdaStrings layout() {
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
		String effectInfo = Chat.indent(indentCount)+title.build(getName());
		
		// Saving the modifier and trigger strings. If they are null, show as "&8Undefined"
		String modifier = (this.modifier != null ? this.modifier.toChatString(indentCount, deep, deepLayout) : Chat.indent(indentCount) + alternateLayout.build("Modifier", "&8Undefined"));
		String trigger = (this.trigger != null ? this.trigger.toChatString(indentCount, deep, deepLayout) : Chat.indent(indentCount) + alternateLayout.build("Trigger", "&8Undefined"));
		
		// Adding the modifier and trigger strings. 
		effectInfo += "\n" + modifier;
		effectInfo += "\n" + trigger;
		// If radius is present, show it
		if(hasRadius())
			effectInfo += "\n" + Chat.indent(indentCount) + alternateLayout.build("Radius", radius.toString());
		
		// If tag is present, show it
		if(hasTag())
			effectInfo += "\n" + tag.toChatString(indentCount, deep, deepLayout);

		// If ignore_user is present, show it
		if(ignoreUser() == false)
			effectInfo += "\n" + Chat.indent(indentCount) + alternateLayout.build("Ignore User", "false");
		
		// If potions are present, show them
		if(hasPotions()) {
			// Need to manually do deep check here as the potion effect toChatString method prints each potion individually, whilst we want to look at the list as a whole
			if(deep) {
				effectInfo += "\n" + Chat.indent(indentCount) + alternateLayout.build("Potion Effects", "")
							+ "\n" + StringsUtil.toChatString(indentCount+1, deep, alternateLayout, potionEffects.toArray(new PotionEffect[0]));
			}
			else
				effectInfo += "\n" + Chat.indent(indentCount) + alternateLayout.build("Potion Effects", Integer.toString(potionEffects.size()));
				
		}
		
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
	
	@Override
	public TextComponent[] toChatHover(int indentCount, boolean deep, LambdaStrings alternateLayout) {
		// USEFUL SECTION TO COPY TO OTHER TOCHATSTRINGS!
		// If alternatelayout is null, then keep deepLayout as null as it means deeper ChatStringables use their default layout as well
		LambdaStrings deepLayout = alternateLayout;
		// If the alternateLayout is null, we want to use the default layout for itself
		alternateLayout = (alternateLayout == null ? layout() : alternateLayout);
		// //////////////////////////////////////////////
		
		List<TextComponent> componentList = new ArrayList<TextComponent>();

		// Title
		TextComponent current = StringsUtil.createTextComponent(Chat.indent(indentCount)+title.build(getName()));
		componentList.add(current);
		
		indentCount++;
		// Modifier
		if(this.modifier != null) {
			componentList.add(StringsUtil.createTextComponent("\n"));
			componentList.addAll(Arrays.asList(this.modifier.toChatHover(indentCount, deep, deepLayout)));
		}
		else {
			current = StringsUtil.hoverText("\n"+Chat.indent(indentCount) + alternateLayout.build("Modifier", "&8Undefined")
			, "&f&lModifier"
			+ "\n&8There is currently no modifier for this effect. Effect is incomplete until one is added.");
			componentList.add(current);
		}
		
		// Trigger
		if(this.trigger != null) {
			componentList.add(StringsUtil.createTextComponent("\n"));
			componentList.addAll(Arrays.asList(this.trigger.toChatHover(indentCount, deep, deepLayout)));
		}
		else {
			current = StringsUtil.hoverText("\n"+Chat.indent(indentCount) + alternateLayout.build("Trigger", "&8Undefined")
			, "&f&lTrigger"
			+ "\n&8There is currently no trigger for this effect. Effect is incomplete until one is added.");
			componentList.add(current);
		}
		
		// Radius
		if(hasRadius())
			componentList.add(StringsUtil.hoverText("\n" + Chat.indent(indentCount) + alternateLayout.build("Radius", radius.toString())
			, "&f&lRadius"
			+ String.format("\n&7This effect will hit each living entity within %.1f blocks of the activated location.", radius)));
		
		// Tag
		if(hasTag()) {
			componentList.add(StringsUtil.createTextComponent("\n"));
			componentList.addAll(Arrays.asList(tag.toChatHover(indentCount, deep, deepLayout)));
		}
		
		// If ignore_user is present, show it
		if(ignoreUser() == false)
			componentList.add(StringsUtil.hoverText("\n"+Chat.indent(indentCount) + alternateLayout.build("Ignore User", "false")
			, "&f&lIgnore User"
			+ "\n&7The user of the effect will not be ignored by its modifier."));
		
		// If potions are present, show them
		if(hasPotions()) {
			// Need to manually do deep check here as the potion effect toChatString method prints each potion individually, whilst we want to look at the list as a whole
			if(deep) {
				componentList.add(StringsUtil.createTextComponent("\n"+Chat.indent(indentCount) + alternateLayout.build("Potion Effects", "")));
				for(PotionEffect potionEffect : potionEffects) {
					current = StringsUtil.hoverText("\n"+StringsUtil.toChatString(indentCount+1, false, s -> "&2> "+s[0], potionEffect)
							, StringsUtil.toChatString(0, true, alternateLayout, potionEffect));
					componentList.add(current);
				}
			}
			else {
				current = StringsUtil.hoverText("\n"+Chat.indent(indentCount) + alternateLayout.build("Potion Effects", Integer.toString(potionEffects.size()))
						, StringsUtil.toChatString(indentCount, true, alternateLayout, potionEffects.toArray(new PotionEffect[0])));
			}
		}
		
		// If particles are present, show them.
		if(hasParticle()) {
			componentList.add(StringsUtil.createTextComponent("\n"));
			componentList.addAll(Arrays.asList(particles.toChatHover(indentCount, deep, deepLayout)));
		}
		
		// If sounds are present, show them.
		if(hasSound()) {
			componentList.add(StringsUtil.createTextComponent("\n"));
			componentList.addAll(Arrays.asList(sounds.toChatHover(indentCount, deep, deepLayout)));
		}

		// If filters are present, show them.
		if(hasFilter()) {
			componentList.add(StringsUtil.createTextComponent("\n"));
			componentList.addAll(Arrays.asList(filter.toChatHover(indentCount, deep, deepLayout)));
		}
		
		return componentList.toArray(new TextComponent[0]);
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
	public boolean execute(Location impact, LivingEntity executor) {
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
	public boolean execute(LivingEntity executor, LivingEntity target) {
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
	@ClassDescriber(
			name="Particle Effect",
			desc="Applies custom particle effects when the attached effect is activated.")
	public static class BrParticleEffect extends MapParsable {
		@FieldDescriber(desc="Custom particle effects to appear.")
		public Particle type;
		@FieldDescriber(desc="Formation to place the particles.")
		public Formation formation;
		@FieldDescriber(desc="Intensity or multitude of particles to display.")
		public int intensity;
		@FieldDescriber(desc="Initial Velocity for the particles being spawned.")
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
			this.formation = getEnumValue(Formation.class, fields, "formation");
			try{ 
				this.intensity = getInt(fields, "intensity");
				this.velocity = getDouble(fields, "velocity");
			} catch (IllegalArgumentException e) {
				Messenger.warning("Particle Effect failed to load:");
				throw e;
			}
		}
		public BrParticleEffect(Particle type, Formation formation, int intensity, double velocity) {
			this.type = type;
			this.formation = formation;
			this.intensity = intensity;
			this.velocity = velocity;
			construct();
		}
		@Override
		public LambdaStrings layout() {
			// Example: &2Type: &7Name
			return s -> "&2"+s[0]+"&2: &7"+s[1];
		}
		@Override
		public String toChatString(int indentCount, boolean deep, LambdaStrings alternateLayout) {
			alternateLayout = (alternateLayout == null ? layout() : alternateLayout);
			if(!deep)
				return Chat.indent(indentCount)+alternateLayout.build(className(), "&7Active");
			return Chat.indent(indentCount)+alternateLayout.build(className(), "")
					+ super.toChatString(indentCount+1, deep, alternateLayout);
		}
		
		@Override
		public TextComponent[] toChatHover(int indentCount, boolean deep, LambdaStrings alternateLayout) {
			// If the alternateLayout is null, we want to use the default layout for itself
			alternateLayout = (alternateLayout == null ? layout() : alternateLayout);
			List<TextComponent> componentList = new ArrayList<TextComponent>();
			
			TextComponent current;
			
			if(!deep) {
				current = StringsUtil.hoverText(Chat.indent(indentCount) + alternateLayout.build(className(), "&7Active")
				, toChatString(0, true, alternateLayout));
				componentList.add(current);
			}
			else {
			
				current = StringsUtil.hoverText(Chat.indent(indentCount) + alternateLayout.build(className(), "")
						, "&f&l"+className()
						+ "\n&7"+classDesc());
				componentList.add(current);
				
				for(String fieldName : fieldMap.keySet()) {
					// If the field is also a Formation, run this same method and add a new line AND its results to compontnList
					if(fieldMap.get(fieldName) instanceof Formation) {
						Formation foundFormation = (Formation) fieldMap.get(fieldName);
						current = StringsUtil.hoverText("\n"+Chat.indent(indentCount+1) + alternateLayout.build(fieldName, foundFormation.toString())
							, "&f&l"+fieldName
							+ "\n&7"+getFieldDesc(fieldName)
							+ "\n"+Chat.INDENT+"&f&l\u2193"
							+ "\n&f&l"+foundFormation.toString()
							+ "\n&7"+foundFormation.getDesc());
						componentList.add(current);
					}
					// Otherwise, build the hover text to show the fieldname with its description on hover
					else {
						current = StringsUtil.hoverText("\n"+Chat.indent(indentCount+1) + alternateLayout.build(fieldName, this.getValueStrings().get(fieldName))
								, "&f&l"+fieldName
								+ "\n&7"+getFieldDesc(fieldName));
						componentList.add(current);
					}
				}
			}
			
			return componentList.toArray(new TextComponent[0]);
			
		}
		
		public Particle getType() {
			return type;
		}

		public Formation getFormation() {
			return formation;
		}

		public int getIntensity() {
			return intensity;
		}

		public double getVelocity() {
			return velocity;
		}

		
		/* 
		 * ================================================================================
		 */
		
		public void spawn(Location location, float radius) {
			spawn(location, radius, intensity, velocity);
		}
		public void spawn(Location location, float radius, int intensity) {
			spawn(location, radius, intensity, velocity);
		}
		public void spawn(Location location, float radius, int intensity, double velocity) {
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
	@ClassDescriber(
			name="Sound Effect",
			desc="Applies a custom sound effect when the attached effect is activated.")
	public static class BrSoundEffect extends MapParsable{
		@FieldDescriber(desc="Custom sound effect to be heard.")
		public Sound type;
//		public Formation formation;
		@FieldDescriber(desc="Volume of the sound effect.")
		public float volume;
		@FieldDescriber(desc="Altered pitch of the sound effect.")
		public float pitch;
		
		public BrSoundEffect() {
			super();
		}
		
		public BrSoundEffect(Map<String, String> fields) {
			super(fields);
		}

		@Override
		protected void populateFields(Map<String, String> fields) {
			this.type = getEnumValue(Sound.class, fields, "type");
	//		this.formation = getEnumValue(Formation.class, fields, "formation");
			this.volume = (float) getDouble(fields, "volume", 100);
			this.pitch = (float) getDouble(fields, "pitch", 1);
		}
		public BrSoundEffect(Sound type, float volume, float pitch) {
			this.type = type;
//			this.formation = formation;
			this.volume = volume;
			this.pitch = pitch;
			construct();
		}
		
		@Override
		public LambdaStrings layout() {
			// Example: &2Type: &7Name
			return s -> "&2"+s[0]+"&2: &7"+s[1];
		}
		@Override
		public String toChatString(int indentCount, boolean deep, LambdaStrings alternateLayout) {
			alternateLayout = (alternateLayout == null ? layout() : alternateLayout);

			if(!deep)
				return Chat.indent(indentCount)+alternateLayout.build(className(), "&7Active");
			return Chat.indent(indentCount)+alternateLayout.build(className(), "")
					+ super.toChatString(indentCount, deep, alternateLayout);
		}
		@Override
		public TextComponent[] toChatHover(int indentCount, boolean deep, LambdaStrings alternateLayout) {
			alternateLayout = (alternateLayout == null ? layout() : alternateLayout);
			List<TextComponent> componentList = new ArrayList<TextComponent>();
			
			TextComponent current;

			if(!deep) {
				current = StringsUtil.hoverText(Chat.indent(indentCount) + alternateLayout.build(className(), "&7Active")
				, toChatString(0, true, alternateLayout));
				componentList.add(current);
			}
			else {
				current = StringsUtil.hoverText(Chat.indent(indentCount) + alternateLayout.build(className(), "")
				, "&f&l"+className()
				+ "\n&7"+classDesc());
				componentList.add(current);
				
				componentList.addAll(Arrays.asList(super.toChatHover(indentCount+1, deep, alternateLayout)));
			}
			
			return componentList.toArray(new TextComponent[0]);
		}
		public Sound getType() {
			return type;
		}

		public float getVolume() {
			return volume;
		}

		public float getPitch() {
			return pitch;
		}


		/* 
		 * ================================================================================
		 */
		
		public void spawn(Location location) {
			location.getWorld().playSound(location, type, volume, pitch);
		}
	}
	/* ================================================================================
	 * Tags
	 * ================================================================================
	 */
	// *** Update this description if tags have more uses!
	@ClassDescriber(
			name="Tag",
			desc="Adds a tag to each living entity being affected by the attached effect. This tag can be used in other effects filters to target or ignore entities hit by this effect.")
	public static class BrTag extends MapParsable{
		@FieldDescriber(desc="The word or phrase to recognise this tag with.")
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
		public BrTag(String name) {
			this.name = name;
			construct();
		}
		@Override
		public LambdaStrings layout() {
			// Example: &2Type: &7Name
			return s -> "&2"+s[0]+"&2: &7"+s[1];
		}
		@Override
		public String toChatString(int indentCount, boolean deep, LambdaStrings alternateLayout) {
			alternateLayout = (alternateLayout == null ? layout() : alternateLayout);
			
			return Chat.indent(indentCount) + alternateLayout.build(className(), name);
		}
		@Override
		public String toString() {
			return name;
		}
		
		public String getName() {
			return name;
		}
		
		/* 
		 * ================================================================================
		 */

		public void apply(PatRunnable task, List<LivingEntity> entities) {
			BrMetaDataHandler.addClean(task, entities, "TAG", name);
		}
		
		public void clear(PatRunnable task) {
			BrMetaDataHandler.remove(task, "TAG");
		}
	}
}
