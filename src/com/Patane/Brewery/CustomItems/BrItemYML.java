package com.Patane.Brewery.CustomItems;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.attribute.AttributeModifier.Operation;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.yaml.snakeyaml.error.YAMLException;

import com.Patane.Brewery.Brewery;
import com.Patane.Brewery.CustomEffects.BrEffect;
import com.Patane.Brewery.CustomEffects.BrEffectYML;
import com.Patane.Brewery.CustomItems.BrItem.CustomType;
import com.Patane.Brewery.YAML.BreweryYAML;
import com.Patane.util.general.Chat;
import com.Patane.util.general.Check;
import com.Patane.util.general.Messenger;
import com.Patane.util.general.StringsUtil;
import com.Patane.util.ingame.ItemsUtil;
import com.google.common.collect.Multimap;

public class BrItemYML extends BreweryYAML{

	public BrItemYML() {
		super("items", "items");
	}
	@Override
	public void save() throws IllegalStateException {
		List<String> savedNames = new ArrayList<String>();
		setSelect(getPrefix());
		for(BrItem item : Brewery.getItemCollection().getAllItems()) {
			Messenger.debug("Attempting to save Item '"+item.getName()+"' ...");
			setSelect(getPrefix());
			if(save(item))
				savedNames.add(item.getName());
		}
		setSelect(getPrefix());
		// No need to save file as each item saves it individually.
		Messenger.info("Successfully saved Items: "+StringsUtil.stringJoiner(savedNames, ", "));
	}
	
	
	@Override
	public void load() throws IllegalStateException {
		List<String> loadedNames = new ArrayList<String>();
		setSelect(getPrefix());
		for(String itemName : getPrefix().getKeys(false)){
			Messenger.debug("Attempting to load Item '"+itemName+"' ...");
			setSelect(getPrefix());
			BrItem loadedItem = load(getSection(itemName));
			if(loadedItem != null)
				loadedNames.add(loadedItem.getName());
		}
		setSelect(getPrefix());
		Messenger.info("Successfully loaded Items: "+StringsUtil.stringJoiner(loadedNames, ", "));
	}
	
	public boolean save(BrItem item) throws IllegalStateException {
		try {
			// Making sure item is not null.
			Check.notNull(item, "Item is null");

			/*
			 * ==================> NAME <==================
			 */
			
			setSelect(item.getName());
			
			/*
			 * ==================> TYPE <==================
			 */
			
			getSelect().set("type", item.getType().name());
			

			/*
			 * ==================> ITEM <==================
			 */
			
			setSelect(item.getName(), "item");

			// >>> Material
			getSelect().set("material", item.getItemStack().getType().name());

			// >>> Name
			
			// If there is no display name, ignore.
			if(ItemsUtil.hasDisplayName(item.getItemStack()))
				getSelect().set("name", Chat.deTranslate(ItemsUtil.getDisplayName(item.getItemStack())));
			else
				getSelect().set("name", null);
			// >>> Lore
			
			// If there is no lore, ignore.
			if(ItemsUtil.hasLore(item.getItemStack()))
				getSelect().set("lore", Chat.deTranslate(ItemsUtil.getLore(item.getItemStack())));
			else
				getSelect().set("lore", null);

			// >>> Stack Size
			int stackSize = item.getItemStack().getAmount();
			
			// If stack size is 1, ignore.
			if(stackSize > 1)
				getSelect().set("stack-size", stackSize);
			else
				getSelect().set("stack-size", null);

			// >>> Unbreakable
			boolean unbreakable = ItemsUtil.isUnbreakable(item.getItemStack());
			
			// If unbreakable is false, ignore.
			if(unbreakable)
				getSelect().set("unbreakable", unbreakable);
			else
				getSelect().set("unbreakable", null);

			// >>> Flags
			Set<ItemFlag> flags = ItemsUtil.getFlags(item.getItemStack());
			
			// If there are no flags, ignore.
			if(flags.size() > 0) {
				// If there are all flags present, simply say 'ALL' (this saves lots of room in .yml file)
				if(flags.size() == ItemFlag.values().length)
					getSelect().set("flags", "ALL");
				else {
					// Converts the ItemFlags into strings suitable for the .yml file
					List<String> flagStrings = new ArrayList<String>();
					for(ItemFlag flag : flags)
						flagStrings.add(flag.name());
					
					getSelect().set("flags", flagStrings);
				}
			}
			else
				getSelect().set("flags", null);

			// >>> Enchantments
			Map<Enchantment, Integer> enchantments = item.getItemStack().getEnchantments();
			
			// If there are no enchantments, ignore.
			if(enchantments.size() > 0) {
				setSelect(item.getName(), "item", "enchantments");
				// Saves all enchantments to Map
				for(Enchantment enchantment : enchantments.keySet())
					/* Enchantment names are saved as 'namespacedkey's. This is because bukkit naming scheme for enchantments is bad.
					 * The 'getKey().getKey()' simply gets the key (name of the enchantment) from the namespacedkey.
					 * Current format in YML:
					 * 			  NAME: LEVEL
					 * eg. RESTORATION: 2
					 */  
					getSelect().set(enchantment.getKey().getKey(), enchantments.get(enchantment));
			}
			else 
				getSelect().set("enchantments", null);
			
			setSelect(item.getName(), "item");
			
			// This happens no matter what due to needing to remove old attributes. Eventually, this (along with each other element) should save, load and remove individually.
			getSelect().set("attributes", null);
			
			// >>> Attribute Modifiers
			if(ItemsUtil.hasAttributes(item.getItemStack())) {
				Multimap<Attribute, AttributeModifier> attributes = ItemsUtil.getAttributes(item.getItemStack());
				setSelect(item.getName(), "item", "attributes");
				// Looping through each attribute.
				for(Attribute attribute : attributes.keySet()) {
					/* Each attribute can have multiple modifiers
					 * Eg. An item can have:
					 *
					 * +1 Max Health
					 * +2 Max Health
					 * +150% Max Health
					 *
					 * at the same time.
					 * All three are for the 'Max Health' attribute, but are different modifiers.
					 */
					
					// Looping through each modifier attached to attribute (as explained above).
					for(AttributeModifier modifier : attributes.get(attribute)) {
						String name = modifier.getName();
						// This checks if there are any other modifiers attached to this attribute with the exact same name.
						// If so, it will set the name to an incremented 'modifier_x' with x being a number. 
						// Otherwise, it is safe to leave the name as is.
						for(AttributeModifier otherModifier : attributes.get(attribute)) {
							// If the modifier has the same name as another modifier (UUID is checked to ensure its not checking itself)
							if(modifier.getName().equals(otherModifier.getName()) && !modifier.getUniqueId().equals(otherModifier.getUniqueId())) {
								name = getNextIteration("modifier_", item.getName(), "item", "attributes", attribute.name());
								break;
							}
						}
						// If the name contains a '.', it will conflict with the YML naming structure.
						// This is an important because minecraft defaults the modifier names to 'generic.NAME' which clearly conflicts.
						if(name.contains(".")) 
							name = getNextIteration("modifier_", item.getName(), "item", "attributes", attribute.name());
						
						// Creating the modifier.
						setSelect(item.getName(), "item", "attributes", attribute.name(), name);
						
						// Saving the Operation type (ADD_NUMBER, ADD_SCALAR or MULTIPLY_SCALAR_1)
						getSelect().set("operation", modifier.getOperation().name());
						
						// Saving the Amount
						getSelect().set("amount", modifier.getAmount());
						
						// Saving the slot if there is any (eg. OFF_HAND)
						if(modifier.getSlot() != null)
							getSelect().set("slot", modifier.getSlot().name());
					}
				}
			}
			else 
				getSelect().set("attributes", null);

			/*
			 * ==================> COOLDOWN <==================
			 */
			 setSelect(item.getName());
			 if(item.hasCooldown())
				 getSelect().set("cooldown", item.getCooldown());
			 else
				 getSelect().set("cooldown", null);
			/*
			 * ==================> EFFECTS <==================
			 */
			getSelect().set("effects", null);
			if(item.hasEffects()) {
				setSelect(item.getName(), "effects");
				for(BrEffect effect : item.getEffects())
					BrEffectYML.post(getSelect(), effect, BrEffect.YML().getPrefix(), Brewery.getEffectCollection().getItem(effect.getName()));
//				BrEffect.YML().save();
			}
			else
				// If the item has no effects then clear its effects section from the yml
				getSelect().set("effects", null);
			
			// Save the file!
			configHandler.saveConfig();
			
			return true;
		} catch (IllegalStateException e) {
			throw e;
		} catch (Exception e) {
			Messenger.warning("'"+item.getName()+"' Item failed to save:");
			e.printStackTrace();
		}
		return false;
	}
	
	public BrItem load(ConfigurationSection section) throws IllegalStateException{
		try{
			// Making sure the section is not null.
			Check.notNull(section);

			/*
			 * ==================> NAME <==================
			 */
			// Getting the item name from the last portion of the section.
			String itemName = extractLast(section);
			
			/*
			 * ==================> TYPE <==================
			 */
			setSelect(itemName);
			
			CustomType type = null;
			try{
				type = getEnumFromString(getSelect().getString("type"), CustomType.class);
			} catch (NullPointerException e){}
			
			/*
			 * ==================> ITEM <==================
			 */
			setSelect(itemName, "item");
			
			// >>> Material
			Material material = null;
			try{
				material = getEnumFromString(getSelect().getString("material"), Material.class);
			} catch (NullPointerException e){}
			
			// >>> Name
			String name = getSelect().getString("name");
			
			// >>> Lore
			// Any invalid strings for lore list in YML will simply be ignored with no errors.
			// This unfortunately cannot be avoided as 'getStringList' handles that.
			String[] lore = null;
			try{
				lore = getSelect().getStringList("lore").toArray(new String[0]);
			} catch (NullPointerException e) {}

			// >>> Stack Size
			Integer amount = null;
			try {
				amount = getInteger("amount", getSelect());
				if(amount == null) 
					amount = 1;
			} catch (NumberFormatException e) {}
			
			// Creating the ItemStack. Remaining values can be edited directly from this point on!
			ItemStack itemStack = ItemsUtil.createItem(material, amount, name, lore);
			
			// >>> Unbreakable
			try {
				// If provided value is not 'true' or 'false', then IllegalArgumentException will be thrown, as handled below.
				Boolean unbreakable = getBoolean("unbreakable", getSelect());
				if(unbreakable != null)
					ItemsUtil.setUnbreakable(itemStack, unbreakable);
			} catch (IllegalArgumentException e) {
				Messenger.warning("'unbreakable' value is invalid for '"+itemName+"' Item. Unbreakable will be set to 'false'");
				e.printStackTrace();
			}
			
			// >>> Flags
			try {
				List<String> flagStrings = getSelect().getStringList("flags");
				// Checking if 'flags' is a list or a single string. If the list it attempts to grab is empty then it is not a list.
				if(flagStrings.isEmpty()) {
					String allFlag = getString("flags", getSelect());
					// If there is no list and the flags are null, then there is no 'flags' section.
					// Otherwise, there is one and it SHOULD say 'ALL' at this stage.
					if(allFlag != null) {
						// Ensures given string is actually the words 'ALL'.
						if(!allFlag.equalsIgnoreCase("ALL"))
							throw new IllegalArgumentException("Flags value is invalid");
						else
							// If 'ALL' is provided, hide all flags.
							itemStack = ItemsUtil.addFlags(itemStack);
					}
				}
				else {
					// Confirmed to be a string list with values, we loop through each.
					for(String flagString : flagStrings) {
						// Try to add each flag. If it is not a valid ItemFlag name then it will NullPointerException.
						try {
							itemStack = ItemsUtil.addFlags(itemStack, getEnumFromString(flagString, ItemFlag.class));
						} catch (NullPointerException e) {
							Messenger.warning("'"+flagString+"' is not a valid ItemFlag");
							e.printStackTrace();
						}
						
					}
				}
			} catch (Exception e ) {
				Messenger.warning("Failed to load Flags for '"+itemName+"' Item.  No flags will be set for this item:");
				e.printStackTrace();
			}
			
			// >>> Enchantments
			if(getSelect().isConfigurationSection("enchantments")) {
				try {
					setSelect(itemName, "item", "enchantments");
					Enchantment enchantment = null;
					
					// Looping through each enchantment name, eg. "silk_touch"
					for(String enchantmentName : getSelect().getKeys(false)) {
						// Attempting to find the attachment through its Namespacedkey.
						// Enchantment name must abide by minecrafts ID scheme for enchantments.
						// "Minecraft ID Name" section of https://www.digminecraft.com/lists/enchantment_list_pc.php
						enchantment = Enchantment.getByKey(NamespacedKey.minecraft(enchantmentName.toLowerCase()));
						// If the enchantment cannot be found
						if(enchantment == null)
							Messenger.warning("'" + enchantmentName + "' is not a valid enchantment. Please ensure it is a valid Minecraft ID Enchantment name. eg. 'silk_touch'");
						else {
							try {
								// Adds enchantment in an unsafe manner.
								// This simply means it does not check if the item shouldnt have that enchantment, and ignores the maxlevel cap for enchantment.
								itemStack.addUnsafeEnchantment(enchantment, getInteger(enchantmentName, getSelect()));
							}
							// Occurs if level is not a number.
							catch (NumberFormatException e) {
								Messenger.warning("'"+enchantmentName+"' does not have a valid level. Please ensure it is a number");
								e.printStackTrace();
							}
						}
						
					}
				} catch (Exception e) {
					Messenger.warning("Failed to load Enchantments for '"+itemName+"' Item");
					e.printStackTrace();
				}
			}
			setSelect(itemName, "item");
			
			// >>> Attribute Modifiers
			if(getSelect().isConfigurationSection("attributes")) {
				try {
					setSelect(itemName, "item", "attributes");
					
					Attribute attribute = null;
					AttributeModifier attributeModifier = null;
					// Looping through each attribute name, eg. "GENERIC_MAX_HEALTH".
					for(String attributeName : getSelect().getKeys(false)) {
						try {
							// Attempting to find the Attribute enum from given string name.
							attribute = getEnumFromString(attributeName, Attribute.class);
							
							setSelect(itemName, "item", "attributes", attributeName);
							// Looping through each modifier name for given Attribute, eg. "modifier_1"
							for(String modifierName : getSelect().getKeys(false)) {
								try {
									setSelect(itemName, "item", "attributes", attributeName, modifierName);
									// Checks/grabs each Operation(enum), amount(int) and slot(enum) and ensures it is not null for construction.
									Operation operation = Check.notNull(getEnumFromString(getString("operation", getSelect()), Operation.class));
									Integer modAmount = Check.notNull(getInteger("amount", getSelect()));
									EquipmentSlot slot = null;
									try{
										slot = getEnumFromString(getString("slot", getSelect()), EquipmentSlot.class);
									} catch (NullPointerException e) {}
									
									// Constructs the new AttributeModifier with a random UUID and non-null values.
									attributeModifier = new AttributeModifier(UUID.randomUUID(), modifierName, modAmount, operation, slot);
									
									// Adding attribute modifier to the item
									itemStack = ItemsUtil.addAttributeModifier(itemStack, attribute, attributeModifier);
								} catch (Exception e) {
									Messenger.warning("Failed to load '"+modifierName+"' for '"+attributeName+"' attribute for '"+itemName+"' Item");
									e.printStackTrace();
								}
							}
						} catch (Exception e) {
							Messenger.warning("Failed to load '"+attributeName+"' attribute for '"+itemName+"' Item");
							e.printStackTrace();
						}
					}
				} catch (Exception e) {
					Messenger.warning("Failed to load Attributes for '"+itemName+"' Item");
					e.printStackTrace();
				}
			}
			
			
			/*
			 * ==================> COOLDOWN <==================
			 */
			setSelect(itemName);
			
			Float cooldown = null;
			try{
				cooldown = getFloat("cooldown", getSelect());
			} catch (NullPointerException e){}
			
			/*
			 * ==================> EFFECTS <==================
			 */
			setSelect(itemName, "effects");
			
			// Creating effects ArrayList
			List<BrEffect> effects = new ArrayList<BrEffect>();
			
			// Loops through each key in effect section
			for(String effectName : getSelect().getKeys(false)){
				// Surrounded in try/catch to ensure that one failed effect
				// doesnt stop other effects from being retrieved.
				try{
					// Retrieves the effect using the yml given, the default BrEffectYML and the retrieve() function.
					BrEffect effect = BrEffectYML.retrieve(getSectionAndWarn(getSelect(), effectName), BrEffect.YML().getSection(effectName));
					
					// Checks if the effect is null or not.
					Check.notNull(effect);
					
					// Adds the effect to the effects list.
					effects.add(effect);
				}
				catch(NullPointerException e){
					Messenger.warning("'"+effectName+"' Effect could not been added to '"+itemName+"'. Did the effect fail to load?");
				}
				// Generally NullPointerExceptions, however some other can come from retieve if handled incorrectly.
				catch(Exception e){
					Messenger.warning("'"+effectName+"' Effect for '"+itemName+" Item failed to be retireved:");
					e.printStackTrace();
				}
			}
			// Creates the item with all given values.
			BrItem item = new BrItem(itemName, type, itemStack, effects, cooldown);

			// If item isnt already in the collection, it adds it.
			if(!Brewery.getItemCollection().hasItem(item.getName()))
				Brewery.getItemCollection().add(item);
			return item;
		} catch(YAMLException e){
			Messenger.warning("An item failed to be found and loaded:");
			e.printStackTrace();
		} catch (IllegalStateException e) {
			throw e;
		} catch (Exception e) {
			Messenger.warning("'"+extractLast(section)+"' Item failed to load:");
			e.printStackTrace();
		}
		return null;
	}
	
}
