package com.Patane.Brewery.CustomEffects;

import java.util.ArrayList;
import java.util.Arrays;

import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.potion.PotionEffect;

import com.Patane.Brewery.Brewery;
import com.Patane.Brewery.Collections.BrCollectable;
import com.Patane.Brewery.CustomEffects.types.EffectType;

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
	private final EffectType type;
	private final Modifier modifier;
	private final int radius; 
	// if radius is 0, projectile must HIT an entity to damage/affect it and only it.
	private final ArrayList<PotionEffect> potionEffects;
	
	public BrEffect(String name, EffectType type, Modifier modifier, int radius, PotionEffect... potionEffects) {
		super(name);
		this.type = type;
		this.modifier = modifier;
		this.radius = (radius < 0 ? 0 : radius);
		this.potionEffects = new ArrayList<PotionEffect>(Arrays.asList(potionEffects));
		
		Brewery.getEffectCollection().add(this);
	}
	public EffectType getType() {
		return type;
	}
	public Modifier getModifier() {
		return modifier;
	}
	public int getRadius() {
		return radius;
	}
	public ArrayList<PotionEffect> getPotionEffects(){
		return potionEffects;
	}
	public void execute(LivingEntity shooter, Location location, EntityType[] hitableEntities){
		type.execute(this, shooter, location, hitableEntities);
	}
	
//	public static enum EffectTypeEnum{
//		INSTANT(INSTANT.class), LINGERING(LINGERING.class);
//		Class<? extends EffectType> clazz;
//		
//		EffectTypeEnum(Class<? extends EffectType> clazz){
//			this.clazz = clazz;
//		}
//	}
}
