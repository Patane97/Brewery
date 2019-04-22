package com.Patane.Brewery.Commands.secondary.editing;

import org.bukkit.attribute.Attribute;
import org.bukkit.command.CommandSender;
import org.bukkit.inventory.ItemStack;

import com.Patane.Brewery.CustomItems.BrItem;
import com.Patane.Brewery.Editing.EditSession;
import com.Patane.Commands.CommandInfo;
import com.Patane.util.general.Messenger;
import com.Patane.util.general.StringsUtil;
import com.Patane.util.ingame.ItemsUtil;
@CommandInfo(
	name = "edit item attributes [attribute] remove",
	aliases = {"rem", "delete", "del"},
	description = "Removes an Attribute Modifier from an item.",
	usage = "/br edit item attributes [attribute] remove <modifier name>",
	hideCommand = true
)
public class itemEditItemAttributesRemove extends itemEditItemAttributes {

	@Override
	public boolean execute(CommandSender sender, String[] args, Object... objects) {
		
		Attribute attribute = (Attribute) objects[0];
		Messenger.debug(StringsUtil.stringJoiner(args, ", "));
		if(args.length < 1) {
			Messenger.send(sender, "&cPlease provide a name for this modifier.");
			return false;
		}
		
		
		String modifierName = args[0];
		
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
}
