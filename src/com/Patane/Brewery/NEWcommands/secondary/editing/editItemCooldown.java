package com.Patane.Brewery.NEWcommands.secondary.editing;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.command.CommandSender;

import com.Patane.Brewery.CustomItems.BrItem;
import com.Patane.Commands.CommandInfo;
import com.Patane.util.general.Messenger;

@CommandInfo(
	name = "edit item cooldown",
	description = "Sets the cooldown for a Brewery Item in seconds. Setting to 0 removes the cooldown.",
	usage = "/brewery edit item <item name> cooldown <amount>",
	maxArgs = 1
)
public class editItemCooldown extends editItem {
	
	@Override
	public boolean execute(CommandSender sender, String[] args, Object... objects) {
		
		// Checking amount is given
		if(args.length < 1) {
			Messenger.send(sender, "&ePlease specify a cooldown amount.");
			return true;
		}
		
		// Find cooldown
		Float cooldown = null;
		try {
			cooldown = Float.parseFloat(args[0]);
		} catch (NumberFormatException e) {
			// Check if cooldown is a float
			Messenger.send(sender, "&7"+args[0]+" &cis an invalid amount.");
			return true;
		}
		
		// Check if cooldown is positive
		if(cooldown < 0) {
			Messenger.send(sender, "&eCooldown must be a positive number.");
			return true;
		}
		
		// Find Item
		BrItem brItem = (BrItem) objects[0];
		
		// Construct success message (as second or seconds depending on amount > 1)
		String successMsg = "&aSet &7"+brItem.getName()+" &aCooldown to &7"+cooldown+"&a second"+(cooldown==1?"":"s")+".";

		// If cooldown is 0, remove the cooldown instead
		if(cooldown == 0) {
			cooldown = null;
			successMsg = "&aRemoved &7"+brItem.getName()+" &aCooldown.";
		}
		
		// Setting cooldown
		brItem.setCooldown(cooldown);
		
		// Saving the item to YML
		BrItem.YML().save(brItem);
		
		// Sending the success message
		Messenger.send(sender, successMsg);
		return true;
	}
	
	@Override
	public List<String> tabComplete(CommandSender sender, String[] args, Object... objects){
		switch(args.length){
		case 1:
			return Arrays.asList("<amount>");
		default:
			return new ArrayList<String>();
		}
		
	}
}
