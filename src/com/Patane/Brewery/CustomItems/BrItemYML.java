package com.Patane.Brewery.CustomItems;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import com.Patane.Brewery.Brewery;
import com.Patane.Brewery.Chat;
import com.Patane.Brewery.Messenger;
import com.Patane.Brewery.Messenger.ChatType;
import com.Patane.Brewery.CustomEffects.CustomEffect;
import com.Patane.Brewery.CustomItems.BrItem.CustomType;
import com.Patane.Brewery.YML.BasicYML;
import com.Patane.Brewery.util.ItemUtilities;
import com.Patane.Brewery.util.YMLUtilities;

public class BrItemYML extends BasicYML{

	public BrItemYML(Plugin plugin) {
		super(plugin, "items.yml", "items");
	}

	@Override
	public void save() {
		for(BrItem item : Brewery.getItemCollection().getAllItems()){
			String itemName = item.getName();
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
			setHeader(clearCreateSection(itemName, "effect_per_entities"));
			for(CustomEffect effect : item.getEffectPerEntities().keySet()){
				Messenger.debug(ChatType.INFO, "Adding: "+effect.getID());
				header.set(effect.getID(), YMLUtilities.getEntityTypeNames(item.getEffectPerEntities().get(effect)));
			}
			Messenger.debug(ChatType.INFO, "Successfully saved Item: " + itemName);
		}
		config.save();
	}

	@Override
	public void load() {
		setHeader(getRootSection());
		for(String itemName : header.getKeys(false)){
			setHeader(itemName);
			// TYPE
			CustomType type = CustomType.valueOf(header.getString("type"));
			if(type == null){
				Messenger.warning("Failed to load item "+itemName+". 'Type' not recognised in items.yml");
				continue;
			}
			// ITEM
			setHeader(itemName, "item");
			Material material = Material.getMaterial(header.getString("material"));
			if(material == null){
				Messenger.warning("Failed to load item "+itemName+". 'Material' not recognised in items.yml");
				continue;
			}
			String name = header.getString("name");
			List<String> lore = header.getStringList("lore");
			ItemStack item = ItemUtilities.createItem(material, 1, (short) 0, name, lore.toArray(new String[0]));
			// EFFECTS
			setHeader(itemName, "effect_per_entities");
			Map<CustomEffect, EntityType[]> effectPerEntities = new HashMap<CustomEffect, EntityType[]>();
			for(String effectName : header.getKeys(false)){
				Messenger.debug(ChatType.INFO, "Adding Effect to item: "+effectName);
//				CustomEffect effect = CustomEffects.getItem(effectName);
//				EntityType[] entityTypes = header.getStringList(effectName).toArray(new EntityType[0]);
//				effectPerEntities.put(effect, entityTypes);
			}
			
			//CREATING ITEM
			new BrItem(itemName, type, item, effectPerEntities);
		}
	}

}
