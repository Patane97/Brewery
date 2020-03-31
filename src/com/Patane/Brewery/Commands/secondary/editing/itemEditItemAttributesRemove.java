package com.Patane.Brewery.commands.secondary.editing;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.command.CommandSender;
import org.bukkit.inventory.ItemStack;

import com.Patane.Brewery.CustomItems.BrItem;
import com.Patane.Brewery.Editing.EditSession;
import com.Patane.Commands.CommandHandler.CommandPackage;
import com.Patane.Commands.CommandInfo;
import com.Patane.util.general.Messenger;
import com.Patane.util.general.StringsUtil;
import com.Patane.util.ingame.ItemsUtil;
@CommandInfo(
	name = "edit item attributes remove",
	aliases = {"rem", "delete", "del"},
	description = "Removes an Attribute Modifier from an item.",
	usage = "/brewery edit item attributes remove [attribute] <modifier name>",
	maxArgs = 2,
	hideCommand = true
)
public class itemEditItemAttributesRemove extends itemEditItemAttributes {

	@Override
	public boolean execute(CommandSender sender, String[] args, Object... objects) {
		
		if(args.length < 1) {
			Messenger.send(sender, "&cPlease provide an item attribute to modify.");
			return false;
		}
		Attribute attribute = null;

		try {
			// Attempting to find the Attribute enum from given string name.
			attribute = StringsUtil.constructEnum(args[0], Attribute.class);
		} catch (IllegalArgumentException e) {
			Messenger.send(sender,  "&7"+args[0]+" &cis not a valid Attribute.");
			return true;
		}
		
		if(args.length < 2) {
			Messenger.send(sender, "&cPlease provide a name for this modifier.");
			return false;
		}		
		String modifierName = args[1];
		
		BrItem brItem = (BrItem) EditSession.get(sender.getName());
		ItemStack currentItem = brItem.getItemStack();
		
		String successMsg = "&aRemoved &7"+modifierName+" &amodifier from &7"+attribute.name()+" &aattribute.";
		
		if(!ItemsUtil.hasAttributeModifier(currentItem, attribute, modifierName)) {
			Messenger.send(sender, "&cThere is no modifier named &7"+modifierName+" &cfor &7"+attribute.name()+" &cattribute.");
			return true;
		}
		
		brItem.setItemStack(ItemsUtil.removeAttributeModifier(currentItem, attribute, modifierName));
		
		if(!ItemsUtil.hasAttributes(brItem.getItemStack()))
			successMsg = "&aRemoved &7"+modifierName+" &amodifier from &7"+attribute.name()+" &aattribute. This item now has no attributes!";
		BrItem.YML().save(brItem);
		
		Messenger.send(sender, successMsg);
		return true;
	}
	
	@Override
	public List<String> tabComplete(CommandSender sender, String[] args, CommandPackage thisPackage) {
		BrItem item = (BrItem) EditSession.get(sender.getName());
		if(item == null)
			return Arrays.asList();
		switch(args.length) {
			case 5: 
				return Arrays.asList(StringsUtil.enumValueStrings(ItemsUtil.getAttributes(item.getItemStack()).keys().toArray(new Attribute[0])));
			default:
				Attribute attribute = StringsUtil.constructEnum(args[4], Attribute.class);
				
				List<String> strings = new ArrayList<String>();
				
				for(AttributeModifier attribMod : ItemsUtil.getAttributes(item.getItemStack()).get(attribute))
					strings.add(attribMod.getName());
				
				return strings;
		}
	}
}
