package com.Patane.Brewery.CustomItems;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.ItemStack;

import com.Patane.Brewery.Brewery;
import com.Patane.Brewery.Collections.BrCollectable;
import com.Patane.Brewery.CustomEffects.BrEffect;
import com.Patane.util.general.Check;
import com.Patane.util.general.Messenger;
import com.Patane.util.ingame.ItemsUtil;


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
	final protected List<BrEffect> effects;
	// Contains list of UUID's currently executing this items effect. (Used to avoid DamageEntityEvent loops)
	protected List <UUID> executing;
	
	public BrItem(String name, CustomType type, ItemStack item, List<BrEffect> effects){
		super(name);
		if(Brewery.getItemCollection().contains(getID())){
			throw new IllegalArgumentException(getID()+" already exists!");
		}
		this.type = Check.nulled(type, "BrItem '"+name+"' has no set type. Please check YML files.");
		this.item = Check.nulled(ItemsUtil.addBrTag(item, getID()), "BrItem '"+name+"' has no item. Did it fail to create? Please check YML files.");
		this.effects = (effects == null ? new ArrayList<BrEffect>() : effects);
	}
	public ItemStack getItem(){
		return item;
	}
	public CustomType getType(){
		return type;
	}
	public List<BrEffect> getEffects (){
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
		for(BrEffect effect : effects){
			try{
				effect.execute(executor, location);
			} catch(Exception e){
				Messenger.warning("Failed to execute "+effect.getID()+" effect: "+e.getMessage());
				e.printStackTrace();
			}
		}
	}
	public static enum CustomType {
		THROWABLE(), HITTABLE(), PASSIVE();
	}

}
