package com.Patane.Brewery.CustomItems;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import com.Patane.Brewery.Brewery;
import com.Patane.Brewery.Chat;
import com.Patane.Brewery.Messenger;
import com.Patane.Brewery.Messenger.Msg;
import com.Patane.Brewery.CustomItems.BrItem.CustomType;
import com.Patane.Brewery.CustomItems.BrItem.EffectContainer;
import com.Patane.Brewery.YML.BasicYML;
import com.Patane.Brewery.util.ErrorHandler;
import com.Patane.Brewery.util.ErrorHandler.BrLoadException;
import com.Patane.Brewery.util.ErrorHandler.Importance;
import com.Patane.Brewery.util.ItemUtilities;
import com.Patane.Brewery.util.StringUtilities;
import com.Patane.Brewery.util.YMLUtilities;

public class BrItemYML extends BasicYML{

	public BrItemYML(Plugin plugin) {
		super(plugin, "items.yml", "items");
	}
	/**
	 * NOT CURRENTLY USED (NOT SURE IF IT WILL BE)
	 */
	@Override
	public void save() {
		for(BrItem item : Brewery.getItemCollection().getAllItems()){
			String itemName = item.getName();
//			if(isSection(itemName)){
//				Messenger.debug(Msg.INFO, itemName+" ALREADY EXISTS IN YML!");
//				load(itemName);
//			}
			setHeader(clearCreateSection(itemName));
			// TYPE
			header.set("type", item.getType().name());
			// ITEM
			setHeader(clearCreateSection(itemName, "item"));
			header.set("material", item.getItem().getType().name());
			if(item.getItem().hasItemMeta()){
				String hiddenID = ItemUtilities.encodeItemData(ItemUtilities.getTag(item.getID()));
				header.set("name", Chat.deTranslate(item.getItem().getItemMeta().getDisplayName().replace(hiddenID, "")));
				header.set("lore", Chat.deTranslate(item.getItem().getItemMeta().getLore())); // make method to convert back and forth
			}
			// EFFECTS
			setHeader(clearCreateSection(itemName, "effects"));
			for(EffectContainer effectContainer : item.getEffectContainers()){
				setHeader(clearCreateSection(itemName, "effects", effectContainer.getEffect().getID(), "trigger"));
				header.set("type", effectContainer.getType().name());
				for(Field field : effectContainer.getType().getClass().getFields()){
					try {
						header.set(field.getName(), field.get(effectContainer.getType()));
					} catch (IllegalArgumentException | IllegalAccessException e) {
						e.printStackTrace();
					}
				}
				setHeader(itemName, "effects", effectContainer.getEffect().getID());
				header.set("entities", YMLUtilities.getEntityTypeNames(effectContainer.getEntities()));
				Messenger.debug(Msg.INFO, "Added "+effectContainer.getEffect().getID()+" to "+item.getID()+" in YML");
			}
			Messenger.debug(Msg.INFO, "Successfully saved Item: " + itemName);
		}
		config.save();
	}

	@Override
	public void load() {
		setHeader(getRootSection());
		for(String itemName : header.getKeys(false)){
			load(itemName);
		}
		Messenger.info("Successfully loaded Items: "+StringUtilities.stringJoiner(Brewery.getItemCollection().getAllIDs(), ", "));
	}
	public void load(String itemName){
		try{
			setHeader(itemName);
			Messenger.debug(Msg.INFO, "Attempting to load "+itemName+" item...");
			if(!itemName.equals(itemName.replace(" ", "_").toUpperCase()))
				ErrorHandler.optionalLoadError(Msg.WARNING, Importance.REQUIRED, "Failed to load "+itemName+": Name must be in upper case with no spacing, eg. '"+itemName.replace(" ", "_").toUpperCase()+"'");
			// TYPE
			CustomType type = getEnumFromString(Importance.REQUIRED, CustomType.class, header.getString("type"), "type", itemName);
			Messenger.debug(Msg.INFO, "     + Type["+type.name()+"]");
			// ITEM
			setHeader(itemName, "item");
			Material material = getEnumFromString(Importance.REQUIRED, Material.class, header.getString("material"), "material", itemName+"'s item");
			String name = header.getString("name");
			List<String> lore = header.getStringList("lore");
			ItemStack item = ItemUtilities.hideFlags(ItemUtilities.createItem(material, 1, (short) 0, name, lore.toArray(new String[0])));
			Messenger.debug(Msg.INFO, "     + Item["+material.name()+", "+name+", "+lore+"]");
			// EFFECTS
			setHeader(itemName, "effects");
			List<EffectContainer> effects = new ArrayList<EffectContainer>();
			for(String effectName : header.getKeys(false)){
				try{
					EffectContainer container = grabEffectContainer(Importance.REQUIRED, header, effectName);
					effects.add(container);
					Messenger.debug(Msg.INFO, "     + Effect["+container.getEffect().getID()+", "+container.getRadius()+", "+container.getType().name()+", "+container.getEntities().toString()+"]");
				} catch (BrLoadException e){
					Messenger.warning(e.getMessage());
				}
			}
			new BrItem(itemName, type, item, effects);
		} catch (BrLoadException e){
			Messenger.warning(e.getMessage());
		}
	}
	
}
