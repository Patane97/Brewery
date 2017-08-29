package com.Patane.Brewery.CustomEffects;

import java.util.ArrayList;
import java.util.Arrays;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.potion.PotionEffect;

import com.Patane.Brewery.Brewery;
import com.Patane.Brewery.Collections.BrCollectable;

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
	
	public BrEffect(String name, Modifier modifier, BrParticleEffect particleEffect, BrSoundEffect soundEffect, PotionEffect... potionEffects) {
		super(name);
		this.modifier = modifier;
		this.particleEffect = particleEffect;
		this.soundEffect = soundEffect;
		this.potionEffects = new ArrayList<PotionEffect>(Arrays.asList(potionEffects));
		Brewery.getEffectCollection().add(this);
	}
	public boolean hasModifier() {
		return (modifier == null ? false : true);
	}
	public Modifier getModifier() {
		return modifier;
	}
	public BrParticleEffect getParticleEffect() {
		return particleEffect;
	}
	public boolean hasParticleEffect() {
		return (particleEffect == null ? false : true);
	}
	public BrSoundEffect getSoundEffect() {
		return soundEffect;
	}

	public boolean hasSoundEffect() {
		return (soundEffect == null ? false : true);
	}
	public ArrayList<PotionEffect> getPotionEffects(){
		return potionEffects;
	}
	
	public static class BrParticleEffect {
		Particle particle;
		int intensity;
		double velocity;
		
		public BrParticleEffect(Particle particle, int intensity, double velocity){
			this.particle = particle;
			this.intensity = intensity;
			this.velocity = velocity;
		}
		public void spawn(Location location, int radius){
			double offset = radius/2;
			location.getWorld().spawnParticle(particle, location, Math.min(Integer.MAX_VALUE, (int) Math.pow(radius, 3)*intensity), offset,offset,offset, velocity);
		}
	}
	public static class BrSoundEffect {
		
	}
}
