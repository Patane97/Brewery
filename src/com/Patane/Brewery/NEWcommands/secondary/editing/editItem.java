package com.Patane.Brewery.NEWcommands.secondary.editing;

import java.util.List;

import org.bukkit.command.CommandSender;

import com.Patane.Brewery.Brewery;
import com.Patane.Brewery.CustomItems.BrItem;
import com.Patane.Brewery.NEWcommands.primary.editCommand;
import com.Patane.Commands.CommandInfo;
import com.Patane.util.general.Messenger;
import com.Patane.util.general.StringsUtil;

@CommandInfo(
	name = "edit item",
	description = "Edits an item within Brewery.",
	usage = "/brewery edit item <item name> [property]",
	permission = "brewery.edit.item",
	maxArgs = 1
)
public class editItem extends editCommand {
	
	@Override
	public boolean execute(CommandSender sender, String[] args, Object... objects) {
		
		// Checking itemname is given
		if(args.length < 1) {
			Messenger.send(sender, "&ePlease specify an item name.");
			return true;
		}
		// Find Item
		BrItem brItem = Brewery.getItemCollection().getItem(args[0]);
		
		// Check if Item exists
		if(brItem == null) {
			Messenger.send(sender, "&cThere is no item with the name &7"+args[0]+"&c.");
			return true;
		}
		
		// Check if next argument/child command is provided
		if(args.length < 2) {
			Messenger.send(sender, "&ePlease specify a property to edit.");
			return true;
		}
		
		return this.gotoChild(1, s -> "&7"+s+" &cis not a valid property to edit.", sender, args, brItem);
	}
	
	@Override
	public List<String> tabComplete(CommandSender sender, String[] args, Object... objects){
		switch(args.length){
		case 1:
			return StringsUtil.encase(Brewery.getItemCollection().getAllIDs(), "'", "'");
		}
		//
		BrItem brItem = Brewery.getItemCollection().getItem(args[0]);
		
		return tabCompleteCore(sender, args, brItem);
	}
}
