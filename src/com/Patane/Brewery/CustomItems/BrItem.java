package com.Patane.Brewery.CustomItems;

import java.util.List;
import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.ItemStack;

import com.Patane.Brewery.Brewery;
import com.Patane.Brewery.Messenger;
import com.Patane.Brewery.Collections.BrCollectable;
import com.Patane.Brewery.CustomEffects.BrEffect;
import com.Patane.Brewery.CustomEffects.EffectType;
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
	final protected List<EffectContainer> effects;
	// Contains list of UUID's currently executing this items effect. (Used to avoid DamageEntityEvent loops)
	protected List <UUID> executing;
	
	public BrItem(String name, CustomType type, ItemStack item, List<EffectContainer> effects){
		super(name);
		if(Brewery.getItemCollection().contains(getID())){
			throw new IllegalArgumentException(getID()+" already exists!");
		}
		this.type = type;
		this.item = ItemUtilities.addBrTag(item, getID());
		this.effects = effects;
		
		Brewery.getItemCollection().add(this);
	}
	public ItemStack getItem(){
		return item;
	}
	public CustomType getType(){
		return type;
	}
	public List<EffectContainer> getEffectContainers (){
		return effects;
	}
	public boolean addExecuting(UUID uuid){
		return executing.contains(uuid);
	}
	public boolean delExecuting(UUID uuid){
		return executing.contains(uuid);
	}
	public boolean isExecuting(UUID uuid){
		return executing.contains(uuid);
	}
	public void execute(LivingEntity executor, Location location){
		if(executor == null){
			Messenger.warning("Attempted to execute '"+getName()+"' with a null executor!");
			return;
		}
		Messenger.debug(executor, "&7You &ahave activated &7"+getName());
		for(EffectContainer effectContainer : effects){
			try{
				effectContainer.execute(executor, location);
			} catch(Exception e){
				Messenger.warning("Failed to execute "+effectContainer.getEffect().getID()+" effect: "+e.getMessage());
				e.printStackTrace();
			}
		}
	}
	public static enum CustomType {
		THROWABLE(), HITTABLE(), PASSIVE();
	}
	public static class EffectContainer {
		final private BrEffect effect;
		final private int radius;
		final private EffectType type;
		final private EntityType[] entities;
		
		public EffectContainer(BrEffect effect, int radius, EffectType type, EntityType... entities){
			this.effect = effect;
			this.type = type;
			this.radius = (radius < 0 ? 0 : radius);
			this.entities = entities;
		}
		public BrEffect getEffect() {
			return effect;
		}
		public int getRadius() {
			return radius;
		}
		public EffectType getType() {
			return type;
		}
		public EntityType[] getEntities() {
			return entities;
		}
		public void execute(LivingEntity executor, Location location){
			type.execute(this, executor, location);
		}
	}

}
