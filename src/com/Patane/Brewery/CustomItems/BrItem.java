package com.Patane.Brewery.CustomItems;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.ItemStack;

import com.Patane.Brewery.Brewery;
import com.Patane.Brewery.CustomEffects.BrEffect;
import com.Patane.util.collections.PatCollectable;
import com.Patane.util.general.Check;
import com.Patane.util.general.Messenger;
import com.Patane.util.general.Messenger.Msg;
import com.Patane.util.general.StringsUtil;
import com.Patane.util.ingame.ItemEncoder;
import com.Patane.util.ingame.ItemsUtil;


public class BrItem extends PatCollectable{
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
	
	final protected CooldownHandler cooldown;
	
	public BrItem(String name, CustomType type, ItemStack item, List<BrEffect> effects, float cooldown){
		super(name);
		if(Brewery.getItemCollection().contains(getID())){
			throw new IllegalArgumentException(getID()+" already exists!");
		}
		this.type = Check.nulled(type, "BrItem '"+name+"' has no set type. Please check YML files.");
		this.item = Check.nulled(ItemEncoder.addTag(item, getID()), "BrItem '"+name+"' has no item. Did it fail to create? Please check YML files.");
		this.effects = (effects == null ? new ArrayList<BrEffect>() : effects);
		this.cooldown = new CooldownHandler(this, cooldown, ItemsUtil.createItem(Material.GHAST_TEAR, 1, (short) 0, null));
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
	public CooldownHandler getCD() {
		return cooldown;
	}
	/*
	 * Not sure if using the 'executing' methods. Remove later (14/05/2018)
	 */
	public boolean addExecuting(UUID uuid){
		return executing.add(uuid);
	}
	public boolean delExecuting(UUID uuid){
		return executing.remove(uuid);
	}
	public boolean isExecuting(UUID uuid){
		return executing.contains(uuid);
	}
	/**
	 * Executes the item's effects in a specific location (Generally the location of impact).
	 * @param location Location to trigger the effects.
	 * @param executor LivingEntity who has triggered the item.
	 */
	public boolean execute(Location location, LivingEntity executor) {
		try {
			// Checks if either location or executor are null for any reason.
			Check.nulled(location, "Location of impact is missing.");
			Check.nulled(executor, "Executing entity is missing.");
			Messenger.debug(executor, "&7You &ahave activated &7"+getName()+"&a on a location.");
			// Collects a list of all successfully deployed effects.
			List<String> successful = new ArrayList<String>();
			
			// Loops through each effect within this BrItem.
			for(BrEffect effect : effects) {
				// If the effect has a radius, then it can be executed.
				// If the effect does not have a radius, then it cannot be executed on a specific location.
				if(effect.hasRadius())
					// If the effect executes successfully, add it to the successful array.
					if(effect.execute(location, executor))
						successful.add(effect.getID());
			}
			Messenger.debug(executor, "&aThe following effects have been executed due to &7"+getName()+"&a: &7"+StringsUtil.stringJoiner(successful, "&a, &7"));
			return true;
		} catch (Exception e) {
			Messenger.send(Msg.WARNING, "Failed to execute item '"+getName()+"' in specific Location:");
			e.printStackTrace();
			return false;
		}
	}
	public boolean execute(LivingEntity executor, LivingEntity target) {
		try {
			// Checks if either executor or target are null for any reason.
			Check.nulled(executor, "Executing entity is missing.");
			Check.nulled(target, "Target of impact is missing.");
			Messenger.debug(executor, "&7You &ahave activated &7"+getName()+"&a on an entity.");
			// Collects a list of all successfully deployed effects.
			List<String> successful = new ArrayList<String>();
			// Loops through each effect within this BrItem.
			for(BrEffect effect : effects) {
				if(effect.execute(executor, target))
					successful.add(effect.getID());
			}
			Messenger.debug(executor, "&aThe following effects have been executed due to &7"+getName()+"&a: &7"+StringsUtil.stringJoiner(successful, "&a, &7"));
			return true;
		} catch (Exception e) {
			Messenger.send(Msg.WARNING, "Failed to execute item '"+getName()+"' on specific Living Entity:");
			e.printStackTrace();
			return false;
		}
	}
	public static enum CustomType {
		THROWABLE(), HITTABLE(), @Deprecated MAIN_HAND(), @Deprecated OFF_HAND(), CLICKABLE(), @Deprecated SHOOTABLE();
	}

}
