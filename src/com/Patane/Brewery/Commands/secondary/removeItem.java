package com.Patane.Brewery.Commands.secondary;

import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.Patane.Brewery.Brewery;
import com.Patane.Brewery.Commands.primary.removeCommand;
import com.Patane.Brewery.CustomItems.BrItem;
import com.Patane.Commands.CommandHandler.CommandPackage;
import com.Patane.Commands.CommandInfo;
import com.Patane.util.general.Messenger;
import com.Patane.util.ingame.Commands;
@CommandInfo(
	name = "remove item",
	description = "Removes an item from Brewery.",
	usage = "/brewery remove item <item name>",
	maxArgs = 1
)
public class removeItem extends removeCommand {

	@Override
	public boolean execute(CommandSender sender, String[] args, Object... objects) {
		
		if(args.length == 0 || args[0] == null) {
			Messenger.send(sender, "&cPlease specify an item name.");
			return false;
		}
		
		// Setting name
		String name = Commands.combineArgs(args);

		if(!Brewery.getItemCollection().hasItem(name)) {
			Messenger.send(sender, "&cThere is no item named &7"+name+"&c!");
			return false;
		}
		
		try {
			BrItem brItem = Brewery.getItemCollection().getItem(name);
			
			// Attempt to remove the item from YML
			BrItem.YML().clearSection(brItem.getName());
			
			Brewery.getItemCollection().remove(brItem.getName());
			
			Messenger.send(sender, "&aRemoved item &7"+ brItem.getName() +"&a.");
			// Maybe print a more detailed 'create' message in console?
		} catch (Exception e) {
			if(sender instanceof Player)
				Messenger.send(sender, "&cFailed to remove item due to the following error: \n &4&o" + e.getMessage());
			else
				Messenger.warning("Failed to remove item");
			e.printStackTrace();
		}
		
		return true;
	}
	
	@Override
	public List<String> tabComplete(CommandSender sender, String[] args, CommandPackage thisPackage) {
		return Brewery.getItemCollection().getAllIDs();
	}
}
