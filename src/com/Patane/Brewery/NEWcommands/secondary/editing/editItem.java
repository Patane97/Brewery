package com.Patane.Brewery.NEWcommands.secondary.editing;

import java.util.List;

import org.bukkit.command.CommandSender;

import com.Patane.Brewery.Brewery;
import com.Patane.Brewery.CustomItems.BrItem;
import com.Patane.Brewery.NEWcommands.primary.editCommand;
import com.Patane.Brewery.commands.BrCommandHandler;
import com.Patane.Commands.CommandHandler;
import com.Patane.Commands.CommandHandler.CommandPackage;
import com.Patane.Commands.CommandInfo;
import com.Patane.util.general.Messenger;
import com.Patane.util.general.StringsUtil;
import com.Patane.util.ingame.Commands;

@CommandInfo(
	name = "edit item",
	description = "Edits an item within Brewery.",
	usage = "/brewery edit item <item name> [property] ...",
	permission = "brewery.edit.item",
	maxArgs = 1
)
public class editItem extends editCommand {
	
	@Override
	public boolean execute(CommandSender sender, String[] args, Object... objects) {
		/* This check does not need to be made. 'This command requires arguments to execute' check is already made in commandHandler!
		 * *** Maybe remove that and switch to be within commands to be more specific!
		if(args.length < 1) {
			Messenger.send(sender, "&cPlease specify an item name.");
			return true;
		}
		*/
		// Find Item
		BrItem brItem = Brewery.getItemCollection().getItem(args[0]);
		
		// Check if Item exists
		if(brItem == null) {
			Messenger.send(sender, "&cThere is no item with the name &7"+args[0]+"&c.");
			return true;
		}
		
		// Check if next argument/child command is provided
		if(args.length < 2) {
			Messenger.send(sender, "&cPlease specify a property to edit.");
			return true;
		}
		
		// Find child command
		CommandPackage child = BrCommandHandler.getChildPackage(this.getClass(), args[1]);
		if(child == null) {
			Messenger.send(sender, "&7"+args[1]+" &cis not a valid property to edit.");
			return false;
		}
		
		// Handle child command with specific arguments & objects
		CommandHandler.grabInstance().handleCommand(sender, child.command(), Commands.grabArgs(args, 1, args.length), brItem);
		return true;
	}
	
	@Override
	public List<String> tabComplete(CommandSender sender, String[] args, Object... objects){
		switch(args.length){
		case 1:
			return StringsUtil.encase(Brewery.getItemCollection().getAllIDs(), "'", "'");
		}
		//
		BrItem brItem = Brewery.getItemCollection().getItem(args[0]);
		
		return tabCompleteCore(this, sender, args, brItem);
	}
}
