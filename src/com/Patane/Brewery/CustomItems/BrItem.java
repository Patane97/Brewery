package com.Patane.Brewery.CustomItems;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import com.Patane.Brewery.Brewery;
import com.Patane.Brewery.CustomEffects.BrEffect;
import com.Patane.util.collections.ChatCollectable;
import com.Patane.util.general.Chat;
import com.Patane.util.general.Check;
import com.Patane.util.general.Messenger;
import com.Patane.util.general.StringsUtil;
import com.Patane.util.general.StringsUtil.LambdaStrings;
import com.Patane.util.ingame.ItemEncoder;
import com.Patane.util.ingame.ItemsUtil;
import com.Patane.util.main.PataneUtil;

import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;


public class BrItem extends ChatCollectable{

	/** =========================================================
	 *  Static YML Section
	 *  =========================================================
	 */
	private static BrItemYML yml;

	public static void setYML(BrItemYML yml) {
		BrItem.yml = yml;
	}
	public static BrItemYML YML() {
		return yml;
	}
	/**
	 *  =========================================================
	 */
	
	protected ItemStack item;
	protected CustomType type;
	protected List<BrEffect> effects;
	protected Float cooldown; // Measured in seconds
	
	protected LambdaStrings title = s -> "&f&l"+s[0]+"&r";

	/* ================================================================================
	 * Constructors
	 * ================================================================================
	 */
	public BrItem(String name, CustomType type, ItemStack item, List<BrEffect> effects, Float cooldown) {
		super(name);
		if(Brewery.getItemCollection().hasItem(getName())) {
			throw new IllegalArgumentException(getName()+" already exists!");
		}
		setType(type);
		setItemStack(item);
		this.effects = (effects == null ? new ArrayList<BrEffect>() : effects);
		this.cooldown = cooldown;
//		constructGUI();
	}
	
	/* ================================================================================
	 * Setters, Getters and Has...ers
	 * ================================================================================
	 */
	/**
	 * <b>Do not use this method to give this BrItem to a player. Use {@link #generateItem()} instead!</b>
	 */
	public ItemStack getItemStack() {
		return item;
	}
	public void setItemStack(ItemStack item) {
		this.item = Check.notNull(ItemEncoder.addTag(item, "name", getName()), "BrItem '"+this.getName()+"' has no item. Did it fail to create?");
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
		return (cooldown == null ? 0 : cooldown);
	}
	public void setCooldown(Float cooldown) {
		this.cooldown = cooldown;
	}
	/* ================================================================================
	 * Other useful methods
	 * ================================================================================
	 */
	/**
	 * Generates an item appropriate for Player use by saving a UUID to the item before returning it. Without this UUID, the item cannot be tracked for cooldown purposes.
	 */
	public ItemStack generateItem() {
		return generateWithUUID(UUID.randomUUID());
	}
	
	/**
	 * Refreshes the given item to become the latest iteration of this BrItem. The given item will retain any specific properties it previously had, such as UUID & amount.
	 * 
	 * @param item Item to refresh.
	 * @return A new ItemStack representing the latest iteration of this BrItems Itemstack.
	 */
	public ItemStack generateFrom(ItemStack oldStack) {
		String brItemUUID = ItemEncoder.getString(oldStack, "UUID");
		UUID uuid = (brItemUUID == null ? null : UUID.fromString(brItemUUID));		
		
		ItemStack newStack = generateWithUUID(uuid);
		
		// Copy any properties that need to be the same.
		newStack.setAmount(oldStack.getAmount());
		
		return newStack;
	}
	
	public ItemStack generateWithUUID(UUID uuid) {
		// Projectiles should not be unique. This way, they can be stacked!
		if(type == CustomType.PROJECTILE)
			return item.clone();
		if(uuid == null)
			return generateItem();
		return ItemEncoder.addTag(this.item.clone(), "UUID", uuid.toString());
	}
	
	/**
	 * Gets the connected BrItem from an ItemStack by checking its encoded value.
	 * @return The connected BrItem or null if there is none.
	 */
	public static BrItem getFromItemStack(ItemStack item) {
		String itemName = ItemEncoder.getString(item, "name");
		if(itemName == null)
			return null;
		return Brewery.getItemCollection().getItem(itemName);
	}
	
	public static boolean isBrItem(ItemStack item) {
		return ItemEncoder.hasTag(item, "name");
	}
	
	/**
	 * Refreshes all BrItems ItemStacks within all online inventories. This ensures each item within each inventory has the latest ItemStack for its BrItem.
	 */
	public static void refreshAllInventories() {
		Brewery.getInstance().getServer().getOnlinePlayers().forEach(p -> refreshInventory(p.getInventory()));		
	}
	
	/**
	 * Refreshes any BrItem ItemStacks within the given inventory. This ensures each item within this inventory has the latest ItemStack for its BrItem.
	 * @param inventory Inventory to refresh
	 */
	public static void refreshInventory(Inventory inventory) {
		ItemStack[] contents = inventory.getContents();
		for(int i=0 ; i<contents.length ; i++) {
			if(contents[i] == null)
				continue;
			BrItem item = BrItem.getFromItemStack(contents[i]);
			if(item != null) {
				Messenger.debug(String.format("Refreshing %s(%d) ItemStack within a %s inventory.", item.getName(), contents[i].getAmount(), inventory.getType().toString()));
				inventory.setItem(i, item.generateFrom(contents[i]));
			}
		}
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

	@Override
	public String toChatString(int indentCount, boolean deep, LambdaStrings alternateLayout) {
		// If the alternateLayout is null, we want to use the default layout for itself
		alternateLayout = (alternateLayout == null ? layout() : alternateLayout);
		
		// Starting with the item name as a bolded title
		String itemInfo = title.build(getName());
		
		// Saving the Type
		itemInfo += "\n" + Chat.indent(indentCount) + alternateLayout.build("Type", type.toString());
		
		// Saving the ItemStack
		if(deep) {
			itemInfo += "\n" + Chat.indent(indentCount) + alternateLayout.build("Item", "")
					  + "\n" + StringsUtil.toChatString(indentCount+1, deep, alternateLayout, item);
		}
		else
			itemInfo += "\n" + Chat.indent(indentCount) + alternateLayout.build("Item", item.getType().toString());
		
		// Saving the Cooldown
		itemInfo += "\n" + Chat.indent(indentCount) + alternateLayout.build("Cooldown", (cooldown != null ? Float.toString(cooldown)+" second"+(cooldown > 1 ? "s" : "") : "None"));
		
		// Saving the effects
		if(deep && effects.size() > 0) {
			itemInfo += "\n" + Chat.indent(indentCount) + alternateLayout.build("Effects", "");
			for(BrEffect effect : effects) {
				// Prints the effect with &7 if its complete or &8&o if its not
				itemInfo += String.format("\n%s&2> %s%s", Chat.indent(indentCount+1), (effect.isComplete() ? "&7" : "&8&o"), effect.getName());
			}
		}
		else
			itemInfo += "\n" + Chat.indent(indentCount) + alternateLayout.build("Effects", Integer.toString(effects.size()));
		
		return itemInfo;
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public TextComponent[] toChatHover(int indentCount, boolean deep, LambdaStrings alternateLayout) {
		// If the alternateLayout is null, we want to use the default layout for itself
		alternateLayout = (alternateLayout == null ? layout() : alternateLayout);
		
		List<TextComponent> componentList = new ArrayList<TextComponent>();
		
		// Title
		TextComponent current = StringsUtil.createTextComponent(Chat.indent(indentCount)+title.build(getName()));
		componentList.add(current);
		
		indentCount++;
		
		// Type
		// Saving Type with layout
		current = StringsUtil.createTextComponent("\n"+Chat.indent(indentCount) + alternateLayout.build("Type", type.toString()));
		// Saving Hover as Type name and description
		current.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(Chat.translate(
				"&f&l"+type.toString() 
			  + "\n&7"+type.getDescription()))));
		componentList.add(current);
		
		// ItemStack
		
		/**
		 * TODO: When more detailed documentation about 'net.md_5.bungee.api.chat.hover.content.Item' is released, change the below two
		 * ComponentBuilders to that. It is hard to update at this time as there is no information on HOW to actually use it. ~ 12/8/20
		 */
		if(deep) {
			current = StringsUtil.createTextComponent("\n"+Chat.indent(indentCount) + alternateLayout.build("Item", "\n"));
			current.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_ITEM, new ComponentBuilder(ItemsUtil.ItemStackToJSON(item)).create()));
			componentList.add(current);
			
			componentList.addAll(Arrays.asList(StringsUtil.toChatHover(indentCount+1, deep, alternateLayout, item)));
		}
		else {
			// If not deep, show item with type as its only value
			current = StringsUtil.createTextComponent("\n"+Chat.indent(indentCount) + alternateLayout.build("Item", item.getType().toString()));
			current.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_ITEM, new ComponentBuilder(ItemsUtil.ItemStackToJSON(item)).create()));
			componentList.add(current);
		}
			// Set Hover as the itemstack itself
		
		// Cooldown
		if(cooldown != null) {
			// If not empty, set Cooldown with layout
			current = StringsUtil.createTextComponent("\n"+Chat.indent(indentCount) + alternateLayout.build("Cooldown", Float.toString(cooldown)+" second"+(cooldown > 1 ? "s" : "")));
			// Show how cooldown works
			current.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(Chat.translate(
					"&f&lCooldown"
				  + "\n&7Once used, this item will take "+cooldown+" second"+(cooldown > 1 ? "s" : "")+" to recover its use again."))));
		}
		else {
			// If empty, set cooldown with none
			current = StringsUtil.createTextComponent("\n"+Chat.indent(indentCount) + alternateLayout.build("Cooldown", "None"));
			// Show how 'none' cooldown works
			current.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(Chat.translate(
					"&f&lCooldown"
				  + "\n&7This item does not take any time to recover its use."))));
		}
		componentList.add(current);
		
		// Effects
		if(deep && effects.size() > 0) {
			// Set Effects with layout
			current = StringsUtil.createTextComponent("\n"+Chat.indent(indentCount) + alternateLayout.build("Effects", ""));
			componentList.add(current);
			// Loop through effect and show its name and all its details OnHover and add to arraylist
			for(BrEffect effect : effects) {
				// Prints the effect with &7 if its complete or &8&o if its not
				current = StringsUtil.createTextComponent(String.format("\n%s&2> %s%s", Chat.indent(indentCount+1), (effect.isComplete() ? "&7" : "&8&o"), effect.getName()));
				current.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(Chat.translate("&f&l"+getName()+" &7&l\u2192&r "+effect.toChatString(0, true)))));
				componentList.add(current);
			}
		}
		else {
			// If not deep, just show the total effect number and show all effects OnHover
			current = StringsUtil.createTextComponent("\n"+Chat.indent(indentCount) + alternateLayout.build("Effects", Integer.toString(effects.size())));
			// If there are effects, show them on hover.
			if(effects.size() > 0)
				current.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(Chat.translate("&f&l"+getName()+" &7&l\u2192&r\n"+StringsUtil.manyToChatString(1, 2, true, null, null, effects.toArray(new BrEffect[0]))))));
			componentList.add(current);
		}
		
		// Return componentList as Array, not arraylist
		return componentList.toArray(new TextComponent[0]);
	}
	
	/* ================================================================================
	 * Item Executors
	 * ================================================================================
	 */
	/**
	 * Executes the item's effects in a specific location (Generally the location of impact).
	 * @param location Location to trigger the effects.
	 * @param executor LivingEntity who has triggered the item.
	 */
	public boolean execute(Location location, LivingEntity executor) {
		try {
			// Checks if either location or executor are null for any reason.
			Check.notNull(location, String.format("Impact location for %s is missing.", this.getName()));
			Check.notNull(executor, String.format("Executing entity for %s is missing.", this.getName()));
			
			long startTime = System.currentTimeMillis();
			// Loops through each effect within this BrItem.
			for(BrEffect effect : effects) {
				// If the effect has a radius, then it can be executed.
				// If the effect does not have a radius, then it cannot be executed on a specific location.
				if(effect.isComplete())
					effect.execute(location, executor);
			}

			// Collecting information and displaying it in one line in console
			String executorName = (executor instanceof Player ? "Player("+((Player) executor).getDisplayName()+")" : executor.getName());
			String locationString = String.format("(%s, %.2f, %.2f, %.2f)", location.getWorld().getName(), location.getX(), location.getY(), location.getZ());
			
			long completeTime = System.currentTimeMillis();
			Messenger.info(String.format("Brewery Item %s activated by %s at %s in %dms.", this.getName(), executorName, locationString, completeTime-startTime));
			if(PataneUtil.getDebug())
				Messenger.send(executor, String.format("&eDebug: &7You &eactivated Item &7%s &eat &7%s &ein &7%dms&e.", this.getName(), locationString, completeTime-startTime));
			return true;
		} catch (Exception e) {
			Messenger.severe(String.format("Failed to execute Brewery Item %s at Location:", this.getName()));
			Messenger.printStackTrace(e);
			return false;
		}
	}
	public boolean execute(LivingEntity executor, LivingEntity target) {
		try {
			// Checks if either executor or target are null for any reason.
			Check.notNull(executor, String.format("Executing entity for %s is missing.", this.getName()));
			Check.notNull(target, String.format("Target of impact for %s is missing.", this.getName()));

			long startTime = System.currentTimeMillis();
			// Loops through each effect within this BrItem.
			for(BrEffect effect : effects) {
				if(effect.isComplete())
					effect.execute(executor, target);
			}
			
			// Collecting information and displaying it in one line in console
			String executorName = (executor instanceof Player ? "Player("+((Player) executor).getDisplayName()+")" : executor.getName());
			String targetName = (target instanceof Player ? "Player("+((Player) target).getDisplayName()+")" : target.getName());
			String locationString = String.format("(%s, %.2f, %.2f, %.2f)", target.getEyeLocation().getWorld().getName(), target.getEyeLocation().getX(), target.getEyeLocation().getY(), target.getEyeLocation().getZ());
			
			long completeTime = System.currentTimeMillis();
			Messenger.info(String.format("Brewery Item %s activated by %s on %s at %s in %dms.", this.getName(), executorName, targetName, locationString, completeTime-startTime));
			if(PataneUtil.getDebug())
				Messenger.send(executor, String.format("&eDebug: &7You &eactivated Item &7%s &eon &7%s &eat &7%s &ein &7%dms&e.", this.getName(), targetName, locationString, completeTime-startTime));
			return true;
		} catch (Exception e) {
			Messenger.severe(String.format("Failed to execute Brewery Item %s on Living Entity:", this.getName()));
			Messenger.printStackTrace(e);
			return false;
		}
	}

	/* ================================================================================
	 * GUI Section. Still in very early development, likely rework needed
	 * ================================================================================
	 */
	
//	protected GUIPage guiPage;
//	
//	public GUIPage guiPage() {
//		return guiPage;
//	}
//	public void constructGUI() {
//		GUIPage mainPage = new GUIPage(this.getName(), 1, false);
//		guiPage = mainPage;
//		
//		//
//		GUIIcon typeIcon = new GUIIcon(type.getGuiIcon());
//		typeIcon.addAction(GUIClick.LEFT, new GUIAction() {
//
//			@Override
//			public boolean execute() {
//				Iterator<CustomType> iter = Arrays.stream(CustomType.values()).iterator();
//				while(iter.next() != type) {}
//				type = (iter.hasNext() ? iter.next() : CustomType.values()[0]);
//				typeIcon.icon = type.getGuiIcon();
//				mainPage.updateIcon(typeIcon);
//				return true;
//			}
//		});
//		mainPage.addIcon(1, typeIcon);
//		//
//		GUIPage effectsPage = new GUIPage(this.getName() + " Effects", 1, false);
//		effectsPage.addBackIcon(effectsPage.getInventory().getSize()-1, mainPage);
//		
//		// implement 'icon dragging'
////		GUIIcon item = new GUIIcon();
//		GUIIcon effectIcon = new GUIIcon(ItemsUtil.addFlavourText(ItemsUtil.createItem(Material.BOOK, Math.max(effects.size(), 1), "&6Effects: &2"+effects.size()), "Click to edit"));
//		effectIcon.addAction(GUIClick.LEFT, new GUIAction() {
//
//			@Override
//			public boolean execute() {
//				mainPage.open(effectsPage);
//				return true;
//			}
//			
//		});
//		mainPage.addIcon(2, effectIcon);
//		//
//	}

/* ================================================================================
 * Item Specific Classes
 * ================================================================================
 */
	/* ================================================================================
	 * CustomType Enum
	 * ================================================================================
	 */
	public static enum CustomType {
		THROWABLE("Throwable", "Right click whilst holding to throw this item and apply its effects on the impacted location or target. If there is no radius set, only the target that is hit will be affected.",
				true),
		HITTABLE("Hittable", "Swing and hit to apply this items effects at the desired location or target. If there is no radius set, only the target that is hit by you will be affected.",
				true),
		CLICKABLE("Clickable", "Right click whilst holding this item to trigger its effects at your location. If there is no radius set, only you will be affected.",
				true),
		CONSUMABLE("Consumable", "Right click to consume this item to trigger its effects at your location. If there is no radius set, only you will be affected.",
				true),
		PROJECTILE("Projectile", "If this item is a projectile, it will trigger its effects on the impact location or target. If there is no radius, only the target you hit with the projectile will be affected.",
				false);
		
		final private String name;
		final private String description;
		final private boolean unique;
		
		CustomType(String name, String description, boolean unique) {
//			this.icon = ItemsUtil.addFlags(ItemsUtil.createItem(material, 1, StringsUtil.formaliseString(this.name()), StringsUtil.stringSplitter(description, 5, "&7")));
//			this.guiItem = ItemsUtil.addFlavourText(ItemsUtil.setItemNameLore(icon,"&6Item Type: &2"+ItemsUtil.getDisplayName(icon)), "Click to change");
			this.name = name;
			this.description = description;
			this.unique = unique;
		}
		public String toString() {
			return getName();
		}
		
		public String getName() {
			return name;
		}
		public String getDescription() {
			return description;
		}
		
		public boolean isUnique() {
			return unique;
		}
		
//		public ItemStack getIcon() {
//			return icon;
//		}
//		public ItemStack getGuiIcon() {
//			return guiItem;
//		}
		
		/**
		 * Determines the appropriate CustomType for the given itemstack.
		 * @param itemStack
		 * @return
		 */
		public static CustomType determineType(ItemStack itemStack) {
			switch(itemStack.getType()) {
				case ARROW:
				case SPECTRAL_ARROW:
				case TIPPED_ARROW:
					return CustomType.PROJECTILE;
				default:
					if(itemStack.getType().isEdible())
						return CustomType.CONSUMABLE;
					return CustomType.HITTABLE;
			}
		}
		
	}

}
