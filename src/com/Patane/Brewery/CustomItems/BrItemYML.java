package com.Patane.Brewery.CustomItems;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.yaml.snakeyaml.error.YAMLException;

import com.Patane.Brewery.Brewery;
import com.Patane.Brewery.CustomEffects.BrEffect;
import com.Patane.Brewery.CustomEffects.BrEffectYML;
import com.Patane.Brewery.CustomItems.BrItem.CustomType;
import com.Patane.Brewery.YAML.BreweryYAML;
import com.Patane.util.general.Check;
import com.Patane.util.general.Messenger;
import com.Patane.util.general.Messenger.Msg;
import com.Patane.util.general.StringsUtil;
import com.Patane.util.ingame.ItemsUtil;

public class BrItemYML extends BreweryYAML{

	public BrItemYML() {
		super("items.yml", "items", "YML File for each item\nExample:");
	}
	@Override
	public void save() {}

	@Override
	public void load() {
		setSelect(getPrefix());
		for(String itemName : getPrefix().getKeys(false)){
			Messenger.debug(Msg.INFO, "Attempting to load Item '"+itemName+"' ...");
			setSelect(getPrefix());
			load(getSection(itemName));
		}
		setSelect(getPrefix());
		Messenger.info("Successfully loaded Items: "+StringsUtil.stringJoiner(Brewery.getItemCollection().getAllIDs(), ", "));
	}
	public BrItem load(ConfigurationSection section){
		try{
			// Making sure the section is not null.
			Check.notNull(section);
			
			// Getting the item name from the last portion of the section.
			String itemName = extractLast(section);

			// Ensures itemName is of valid format.
			safeFormatCheck(itemName);

			Messenger.debug(Msg.INFO, "=+ "+itemName);
			/*
			 * ==================> TYPE <==================
			 */
			setSelect(itemName);
			
			CustomType type = null;
			try{
				type = getEnumFromString(getSelect().getString("type"), CustomType.class);
				Messenger.debug(Msg.INFO, " + Type["+type.name()+"]");
			} catch (NullPointerException e){}
			
			/*
			 * ==================> ITEM <==================
			 */
			setSelect(itemName, "item");
			
			// Find material from Minecraft Material enum.
			Material material = null;
			try{
				material = getEnumFromString(getSelect().getString("material"), Material.class);
			} catch (NullPointerException e){}
			
			// Setting the item name.
			String name = getSelect().getString("name");
			
			// Setting the item lore.
			List<String> lore = new ArrayList<String>();
			try{
				lore.addAll(getSelect().getStringList("lore"));
			} catch (NullPointerException e) {}
			
			
			// Creating the item with all flags hidden.
			ItemStack itemStack = ItemsUtil.hideFlags(ItemsUtil.createItem(material, 1, (short) 0, name, lore.toArray(new String[0])));
			
			// Printing for debug.
			Messenger.debug(Msg.INFO, " + Item["+material.name()+"]");
			Messenger.debug(Msg.INFO, " +--[name: "+name+"]");
			Messenger.debug(Msg.INFO, " +--[lore: "+lore+"]");

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
					BrEffect effect = BrEffectYML.retrieve(getSectionAndWarn(getSelect(), effectName), BrEffect.YML().getSection(effectName), false);
					
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
			BrItem item = new BrItem(itemName, type, itemStack, effects, 10f);

			// If item isnt already in the collection, it adds it.
			if(!Brewery.getItemCollection().contains(item.getID()))
				Brewery.getItemCollection().add(item);
			return item;
		} catch(YAMLException e){
			Messenger.warning("An item failed to be found and load:");
			e.printStackTrace();
		} catch (Exception e){
			Messenger.warning("'"+extractLast(section)+"' Item failed to load:");
			e.printStackTrace();
		}
		return null;
	}
	
}
