package com.Patane.Brewery.NEWcommands.secondary;

import java.util.Arrays;
import java.util.List;

import org.bukkit.command.CommandSender;

import com.Patane.Brewery.Brewery;
import com.Patane.Brewery.CustomItems.BrItem;
import com.Patane.Brewery.NEWcommands.primary.removeCommand;
import com.Patane.Commands.CommandInfo;
import com.Patane.util.general.Messenger;
import com.Patane.util.general.StringsUtil;
import com.Patane.util.ingame.Commands;

import net.md_5.bungee.api.chat.TextComponent;
@CommandInfo(
	name = "remove item",
	description = "Removes an item from Brewery.",
	usage = "/brewery remove item <item name>",
	maxArgs = 1
)
public class removeItem extends removeCommand {

	@Override
	public boolean execute(CommandSender sender, String[] args, Object... objects) {
		
		// Checking if item name is given
		if(args.length < 1) {
			Messenger.send(sender, "&cPlease specify an item name.");
			return true;
		}
		
		// Setting name
		String itemName = Commands.combineArgs(args);
		
		BrItem item = null;
		
		// If no item with that name exists, do nothing and message appropriately
		if(!Brewery.getItemCollection().hasItem(itemName)) {
			Messenger.send(sender, StringsUtil.hoverText("&eThere is no Brewery Item named &7"+itemName+"&e. Hover to view all Items!"
														, "&8Not implemented yet"));
			return true;
		}

		String successMsg = "&aRemoved an existing Brewery Item. Hover to view the details!";
		String successHoverText = null;
		
		try {
			// Grabbing the item
			item = Brewery.getItemCollection().getItem(itemName);
			
			// Attempt to remove the item from YML. If this gives us exceptions then we dont remove the item from the collection
			BrItem.YML().clearSection(item.getName());
			
			// Remove item from collection
			Brewery.getItemCollection().remove(item.getName());
			
			// Save removed effect onto hover text with removed layout/title layouts
			// *** Not implemented yet
//			successHoverText = BrItem.manyToChatString(s -> "&c&l- &8&l&m"+s[0], s -> "&c"+Chat.replace(s[0], "&c")+"&c: &8"+Chat.replace(s[1], "&8"), false, item);
			successHoverText = "&8Not implemented yet";
		} catch (Exception e) {
			// Save the error message onto successMsg (oh the irony)
			successMsg = "&cThere was an error with removing this item. Hover for error details!";
			// Save the exception message on hover. Dat shi ugly
			successHoverText = "&7"+e.getMessage();
			e.printStackTrace();
		}
		// Allows the user to view the details on hover
		TextComponent successMsgComponent = StringsUtil.hoverText(successMsg, successHoverText);
		
		// Send the hover message to sender
		Messenger.send(sender, successMsgComponent);
		return true;
	}

	@Override
	public List<String> tabComplete(CommandSender sender, String[] args, Object... objects) {
		switch(args.length) {
			case 1: return StringsUtil.encase(Brewery.getItemCollection().getAllIDs(), "'", "'");
		}
		return Arrays.asList();
	}
}
