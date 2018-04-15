package com.Patane.Brewery.CustomEffects;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.EntityType;
import org.bukkit.potion.PotionEffect;

import com.Patane.Brewery.Brewery;
import com.Patane.Brewery.Namer;
import com.Patane.Brewery.YMLParsable;
import com.Patane.Brewery.Collections.BrCollectable;
import com.Patane.Brewery.CustomEffects.types.Instant;
import com.Patane.Brewery.util.ErrorHandler.BrLoadException;

public class BrEffect extends BrCollectable{
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
	// if radius is 0, projectile must HIT an entity to damage/affect it and only it.
	private final ArrayList<PotionEffect> potionEffects;
	private final BrParticleEffect particleEffect;
	private final BrSoundEffect soundEffect;
	private final DefaultContainer defaultContainer;
	
	public BrEffect(String name, Modifier modifier, BrParticleEffect particleEffect, BrSoundEffect soundEffect, DefaultContainer defaultContainer, PotionEffect... potionEffects) {
		super(name);
		this.modifier = modifier;
		this.particleEffect = particleEffect;
		this.soundEffect = soundEffect;
		this.defaultContainer = (defaultContainer == null ? new DefaultContainer() : defaultContainer);
		this.potionEffects = new ArrayList<PotionEffect>(Arrays.asList(potionEffects));
		Brewery.getEffectCollection().add(this);
	}
	
	public boolean hasModifier() {
		return (modifier == null ? false : true);
	}
	public Modifier getModifier() {
		return modifier;
	}
	
	public boolean hasParticleEffect() {
		return (particleEffect == null ? false : true);
	}
	public BrParticleEffect getParticleEffect() {
		return particleEffect;
	}
	
	public boolean hasSoundEffect() {
		return (soundEffect == null ? false : true);
	}
	public BrSoundEffect getSoundEffect() {
		return soundEffect;
	}
	
	public ArrayList<PotionEffect> getPotionEffects(){
		return potionEffects;
	}

	public DefaultContainer getDefaultInfo() {
		return defaultContainer;
	}

	@Namer(name = "Particle Effect")
	public static class BrParticleEffect extends YMLParsable{
		final private Particle type;
		final private int intensity;
		final private double velocity;

		public BrParticleEffect(Map<String, String> fields) throws BrLoadException{
			this.type = Particle.valueOf(fields.get("type"));
			this.intensity = getInt(fields, "intensity");
			this.velocity = getDouble(fields, "velocity");
		}
		public BrParticleEffect(Particle type, int intensity, double velocity){
			this.type = type;
			this.intensity = intensity;
			this.velocity = velocity;
		}
		public void spawn(Location location, int radius){
			double offset = radius/2;
			location.getWorld().spawnParticle(type, location, Math.min(Integer.MAX_VALUE, (int) Math.pow(radius, 3)*intensity), offset,offset,offset, velocity);
		}
	}
	@Namer(name = "Sound Effect")
	public static class BrSoundEffect extends YMLParsable{
		final private Sound type;
		final private float volume;
		final private float pitch;

		public BrSoundEffect(Map<String, String> fields) throws BrLoadException{
			this.type = Sound.valueOf(fields.get("type"));
			this.volume = (float) getDouble(fields, "volume", 100);
			this.pitch = (float) getDouble(fields, "pitch", 1);
		}
		public BrSoundEffect(Sound type, float volume, float pitch){
			this.type = type;
			this.volume = volume;
			this.pitch = pitch;
		}
		public void spawn(Location location){
			location.getWorld().playSound(location, type, volume, pitch);
		}
	}
	public static class DefaultContainer {
		final private EffectType type;
		final private int radius;
		final private EntityType[] entities;
		
		public DefaultContainer(EffectType type, Integer radius, EntityType... entities){
			this.type 		= (type == null ? new Instant() : type);
			this.radius 	= (radius == null ? 1 : (radius < 0 ? 0 : radius));
			this.entities 	= (entities == null ? new EntityType[0] : entities);
		}
		public DefaultContainer(){
			this(null,null);
		}
		public EffectType getType() {
			return type;
		}
		public int getRadius() {
			return radius;
		}
		public EntityType[] getEntities() {
			return entities;
		}
	}
}
