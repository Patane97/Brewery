package com.Patane.Brewery.CustomItems;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.ItemStack;

import com.Patane.Brewery.Brewery;
import com.Patane.Brewery.CustomEffects.BrEffect;
import com.Patane.GUI.GUIAction;
import com.Patane.GUI.GUIClick;
import com.Patane.GUI.GUIIcon;
import com.Patane.GUI.GUIPage;
import com.Patane.util.collections.PatCollectable;
import com.Patane.util.general.Chat;
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
	protected ItemStack item;
	protected CustomType type;
	protected List<BrEffect> effects;
	protected Float cooldown; // Measured in seconds
	
	public BrItem(String name, CustomType type, ItemStack item, List<BrEffect> effects, Float cooldown){
		super(name);
		if(Brewery.getItemCollection().contains(getID())){
			throw new IllegalArgumentException(getID()+" already exists!");
		}
		setType(type);
		setItemStack(item);
		this.effects = (effects == null ? new ArrayList<BrEffect>() : effects);
		this.cooldown = cooldown;
		constructGUI();
	}
	
	protected GUIPage guiPage;
	
	public GUIPage guiPage() {
		return guiPage;
	}
	
	
	/**
	 * Generates an ItemStack appropriate to give to a player for use.
	 */
	public ItemStack generateItem() {
		return ItemEncoder.addTag(item.clone(), "UUID", UUID.randomUUID().toString());
	}
	/**
	 * <b>Do not use this method to give this BrItem to a player. Use {@link #generateItem()} instead!</b>
	 */
	public ItemStack getItemStack() {
		return item;
	}
	public void setItemStack(ItemStack item) {
		this.item = Check.notNull(ItemEncoder.addTag(item, "NAME", getID()), "BrItem '"+this.getName()+"' has no item. Did it fail to create?");
	}
	
	
	public CustomType getType() {
		return type;
	}
	public void setType(CustomType type) {
		this.type = Check.notNull(type, "BrItem '"+this.getName()+"' has no set type.");
	}
	
	
	public boolean hasEffects() {
		return !effects.isEmpty();
	}
	public boolean hasEffect(String effectName) {
		for(BrEffect effect : effects)
			if(effect.getName().equalsIgnoreCase(effectName))
				return true;
		return false;
	}
	public BrEffect getEffect(String effectName) {
		for(BrEffect effect : effects)
			if(effect.getName().equalsIgnoreCase(effectName))
				return effect;
		return null;
	}
	public List<BrEffect> getEffects() {
		return effects;
	}
	public void addEffect(BrEffect effect) {
		Messenger.debug("Adding "+effect.getName()+" effect to "+this.getName());
		effects.add(effect);
	}

	public void removeEffect(String effectName) {
		for(BrEffect effect : effects) {
			if(effect.getName().equalsIgnoreCase(effectName)) {
				Messenger.debug("Removing "+effect.getName()+" effect from "+this.getName());
				effects.remove(effect);
				return;
			}
		}
	}
	
	public boolean hasCooldown() {
		return (cooldown == null ? false :  true);
	}
	public float getCooldown() {
		return cooldown;
	}
	public void setCooldown(Float cooldown) {
		this.cooldown = cooldown;
	}
	/**
	 * Executes the item's effects in a specific location (Generally the location of impact).
	 * @param location Location to trigger the effects.
	 * @param executor LivingEntity who has triggered the item.
	 */
	public boolean execute(Location location, LivingEntity executor) {
		try {
			// Checks if either location or executor are null for any reason.
			Check.notNull(location, "Location of impact is missing.");
			Check.notNull(executor, "Executing entity is missing.");
			Messenger.debug(executor, "&7You &ahave activated &7"+getName()+"&a on a location.");
			// Collects a list of all successfully deployed effects.
			List<String> successful = new ArrayList<String>();
			
			// Loops through each effect within this BrItem.
			for(BrEffect effect : effects) {
				// If the effect has a radius, then it can be executed.
				// If the effect does not have a radius, then it cannot be executed on a specific location.
				if(effect.isComplete())
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
			Check.notNull(executor, "Executing entity is missing.");
			Check.notNull(target, "Target of impact is missing.");
			Messenger.debug(executor, "&7You &ahave activated &7"+getName()+"&a on an entity.");
			// Collects a list of all successfully deployed effects.
			List<String> successful = new ArrayList<String>();
			// Loops through each effect within this BrItem.
			for(BrEffect effect : effects) {
				if(effect.isComplete())
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
	public String hoverDetails() {
		List<String> effectNames = new ArrayList<String>();
		for(BrEffect effect : getEffects())
			effectNames.add(effect.getName());
		
		return Chat.translate("&7"+getName()
		+"\n&2Type: &a"+getType()
		+"\n&2Item: &a"+ItemsUtil.getDisplayName(getItemStack())
		+(hasCooldown() ? "\n&2Cooldown: &a"+getCooldown() : "")
		+(hasEffects() ? "\n&2Effects: &a"+StringsUtil.stringJoiner(effectNames, "&2, &a") : ""));
	}
	
	public static BrItem get(ItemStack item){
		String brItemName = ItemEncoder.getString(item, "NAME");
		if(brItemName == null)
			return null;
		return Brewery.getItemCollection().getItem(brItemName);
	}

	/**
	 * ********************** GUI SECTION ***********************
	 */
	public void constructGUI() {
		GUIPage mainPage = new GUIPage(this.getName(), 1, false);
		guiPage = mainPage;
		
		//
		GUIIcon typeIcon = new GUIIcon(type.getGuiIcon());
		typeIcon.addAction(GUIClick.LEFT, new GUIAction() {

			@Override
			public boolean execute() {
				Iterator<CustomType> iter = Arrays.stream(CustomType.values()).iterator();
				while(iter.next() != type) {}
				type = (iter.hasNext() ? iter.next() : CustomType.values()[0]);
				typeIcon.icon = type.getGuiIcon();
				mainPage.updateIcon(typeIcon);
				return true;
			}
		});
		mainPage.addIcon(1, typeIcon);
		//
		GUIPage effectsPage = new GUIPage(this.getName() + " Effects", 1, false);
		effectsPage.addBackIcon(effectsPage.getInventory().getSize()-1, mainPage);
		
		// implement 'icon dragging'
//		GUIIcon item = new GUIIcon();
		GUIIcon effectIcon = new GUIIcon(ItemsUtil.addFlavourText(ItemsUtil.createItem(Material.BOOK, Math.max(effects.size(), 1), "&6Effects: &2"+effects.size()), "Click to edit"));
		effectIcon.addAction(GUIClick.LEFT, new GUIAction() {

			@Override
			public boolean execute() {
				mainPage.open(effectsPage);
				return true;
			}
			
		});
		mainPage.addIcon(2, effectIcon);
		//
	}
	/**
	 * **********************************************************
	 */
	public static enum CustomType {
		THROWABLE(Material.SPLASH_POTION, "Right click whilst holding to throw this item. Effects apply on impacted location or target."),
		HITTABLE(Material.IRON_SWORD, "Hit a block or target to apply the effects at that location."),
		CLICKABLE(Material.STONE_BUTTON, "Right click whilst holding to activate this item. Effects apply from the users location.");
		
		private ItemStack icon;
		private ItemStack guiItem;
		private String description;
		
		CustomType(Material material, String description){
			this.icon = ItemsUtil.addFlags(ItemsUtil.createItem(material, 1, StringsUtil.formaliseString(this.name()), StringsUtil.wordSplitter(description, 5, "&7")));
			this.guiItem = ItemsUtil.addFlavourText(ItemsUtil.setItemNameLore(icon,"&6Item Type: &2"+ItemsUtil.getDisplayName(icon)), "Click to change");
			this.description = description;
		}
		public String getDescription() {
			return description;
		}
		public ItemStack getIcon() {
			return icon;
		}
		public ItemStack getGuiIcon() {
			return guiItem;
		}
	}

}
