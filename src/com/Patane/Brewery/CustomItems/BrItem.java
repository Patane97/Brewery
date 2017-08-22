package com.Patane.Brewery.CustomItems;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.ItemStack;

import com.Patane.Brewery.Brewery;
import com.Patane.Brewery.Collections.BrCollectable;
import com.Patane.Brewery.CustomEffects.CustomEffect;
import com.Patane.Brewery.util.ItemUtilities;


public class BrItem extends BrCollectable{
	/**
	 * ******************* STATIC YML SECTION *******************
	 */
	private static BrItemYML yml;

	public static void setYML(BrItemYML yml){
		BrItem.yml = yml;
	}
	public static BrItemYML YML(){
		return yml;
	}
	/**
	 * **********************************************************
	 */
	final protected ItemStack item;
	final protected CustomType type;
	protected Map<CustomEffect, EntityType[]> effectPerEntities = new HashMap<CustomEffect, EntityType[]>();
	
	public BrItem(String name, CustomType type, ItemStack item, Map<CustomEffect, EntityType[]> effectPerEntities){
		super(name);
		if(Brewery.getItemCollection().contains(getID())){
			throw new IllegalArgumentException(getID()+" already exists!");
		}
		this.type = type;
		this.item = ItemUtilities.addBrTag(item, getID());
		this.effectPerEntities = effectPerEntities;
		
		Brewery.getItemCollection().add(this);
	}
	public ItemStack getItem(){
		return item;
	}
	public CustomType getType(){
		return type;
	}
	public Map<CustomEffect, EntityType[]> getEffectPerEntities (){
		return effectPerEntities;
	}
	public void execute(LivingEntity shooter, Location location){
		for(CustomEffect customEffect : effectPerEntities.keySet()){
			customEffect.execute(shooter, location, effectPerEntities.get(customEffect));
		}
//		for(CustomEffect customEffect : customEffects){
//			customEffect.execute(shooter, location);
//		}
	}
	public static enum CustomType {
		THROWABLE(), HITTABLE(), PASSIVE();
	}
}
