package com.Patane.Brewery.Commands.secondary;

import org.bukkit.command.CommandSender;

import com.Patane.Brewery.Brewery;
import com.Patane.Brewery.Commands.primary.editSessionCommand;
import com.Patane.Brewery.CustomItems.BrItem;
import com.Patane.Brewery.Editing.EditSession;
import com.Patane.Commands.CommandInfo;
import com.Patane.util.general.Messenger;
import com.Patane.util.ingame.Commands;

@CommandInfo(
	name = "editsession item",
	description = "Starts an editing session for a Brewery Item.",
	usage = "/br editsession item <item name>"
)
public class editSessionItem extends editSessionCommand {
	
	@Override
	public boolean execute(CommandSender sender, String[] args, Object... objects) {
		// Setting name
		String name = Commands.combineArgs(args);
		
		if(!Brewery.getItemCollection().hasItem(name)) {
			Messenger.send(sender, "&cThere is no item named &7"+name+"&c!");
			return true;
		}
		
		BrItem brItem = Brewery.getItemCollection().getItem(name);
		EditSession.start(sender.getName(), brItem);
		return true;
	}
}
