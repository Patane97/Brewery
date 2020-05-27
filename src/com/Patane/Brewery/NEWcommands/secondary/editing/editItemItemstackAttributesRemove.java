package com.Patane.Brewery.NEWcommands.secondary.editing;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.command.CommandSender;
import org.bukkit.inventory.ItemStack;

import com.Patane.Brewery.CustomItems.BrItem;
import com.Patane.Commands.CommandInfo;
import com.Patane.util.general.Messenger;
import com.Patane.util.general.StringsUtil;
import com.Patane.util.ingame.ItemsUtil;
import com.google.common.collect.Multimap;

import net.md_5.bungee.api.chat.TextComponent;

@CommandInfo(
	name = "edit item item attributes remove",
	aliases = {"rem", "delete", "del"},
	description = "Removes an Attribute Modifier from a physical Minecraft Item for a Brewery Item.",
	usage = "/brewery edit item <item name> item attributes remove [attribute] <modifier name>",
	maxArgs = 2,
	hideCommand = true
)
public class editItemItemstackAttributesRemove extends editItemItemstackAttributes {
	
	@Override
	public boolean execute(CommandSender sender, String[] args, Object... objects) {
		
		// Checks for attribute
		if(args.length < 1) {
			Messenger.send(sender, "&ePlease specify an item attribute.");
			return true;
		}
		Attribute attribute = null;
		
		// Find/Save attribute
		try {
			attribute = StringsUtil.constructEnum(args[0], Attribute.class);
		} catch (IllegalArgumentException e) {
			Messenger.send(sender, "&7"+args[0]+" &cis not a valid Attribute.");
			return true;
		}
		
		// Check for attribute name
		if(args.length < 2) {
			Messenger.send(sender, "&ePlease provide a pre-existing modifier name to remove.");
			return true;
		}
		String modifierName = args[1];
		
		// Grabbing BrItem from objects
		BrItem brItem = (BrItem) objects[0];
		
		// Grabbing ItemStack to remove attribute from
		ItemStack currentItem = brItem.getItemStack();
		
		// If there is no modifier for attribute
		if(!ItemsUtil.hasAttributeModifier(currentItem, attribute, modifierName)) {
			// Sending the user a 'no found modifier' message whilst providing all other modifiers attached to asked attribute onHover
			Messenger.send(sender, StringsUtil.hoverText(
					"&eThere is no modifier named &7"+modifierName+" &efor that attribute for &7"+brItem.getName()+"&e. Hover to see others!",
					StringsUtil.toHoverString(attribute, ItemsUtil.getAttributeModifiers(currentItem, attribute), s -> "&2"+s[0]+": &7"+s[1])));
			return true;
		}
		
		// Constructing the success message with hover events and correct wording
		String successMsg = "&aRemoved &7"+modifierName+" &aattribute modifier from &7"+brItem.getName()+"&a. Hover for details!";
		
		AttributeModifier modifier = ItemsUtil.getAttributeModifier(currentItem, attribute, modifierName);
		
		String successHoverText = StringsUtil.toHoverString(attribute, modifier, s ->"&c"+s[0]+": &8&m"+s[1]+"&r");

		// Allows the user to view the details of the attribute they just modified!
		TextComponent successMsgComponent = StringsUtil.hoverText(successMsg, successHoverText);
		
		// Removing the attribute modifier from item
		brItem.setItemStack(ItemsUtil.removeAttributeModifier(currentItem, attribute, modifierName));
		
		// Save YML
		BrItem.YML().save(brItem);
		
		// Send success message
		Messenger.send(sender, successMsgComponent);
		return true;
	}
	
	@Override
	public List<String> tabComplete(CommandSender sender, String[] args, Object... objects) {
		BrItem brItem = (BrItem) objects[0];
		if(brItem == null)
			return Arrays.asList();
		
		Multimap<Attribute, AttributeModifier> attributes;
		switch(args.length) {
			case 1:
				// Grab & Save all attributes available in item OR return empty
				if((attributes = ItemsUtil.getAttributes(brItem.getItemStack())) == null)
					break;
				
				// Return the available attribute keys (Attribute enum) as a String Array List
				return Arrays.asList(StringsUtil.enumValueStrings(attributes.keys().toArray(new Attribute[0])));
			case 2:
				// Grab & Save attribute provided in previous arg OR return empty
				Attribute attribute;
				try {
					attribute = StringsUtil.constructEnum(args[0], Attribute.class);
				} 
				// If the given value is not an Attribute enum, return empty
				catch(IllegalArgumentException e) {
					break;
				}
				
				// Grab & Save all attributes available in item OR return empty
				if((attributes = ItemsUtil.getAttributes(brItem.getItemStack())) == null)
					break;
				
				// Grab & Save all attribute modifiers attached to previously grabbed attribute OR return empty
				Collection<AttributeModifier> modifiers;
				if((modifiers = attributes.get(attribute)) == null)
					break;
				
				// Loop and save the names of each attribute modifier found above
				List<String> modifierNames = new ArrayList<String>();
				// Gets the name of each Modifier and saves it to modifierNames arraylist
				modifiers.forEach(modifier -> modifierNames.add(modifier.getName()));
				
				// Return the available attribute modifier names
				return modifierNames;
		}
		return Arrays.asList();
		
	}
}
