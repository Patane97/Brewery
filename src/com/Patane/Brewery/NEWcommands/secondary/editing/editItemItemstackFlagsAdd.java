package com.Patane.Brewery.NEWcommands.secondary.editing;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

import com.Patane.Brewery.CustomItems.BrItem;
import com.Patane.Commands.CommandInfo;
import com.Patane.util.general.Messenger;
import com.Patane.util.general.StringsUtil;
import com.Patane.util.ingame.ItemsUtil;

import net.md_5.bungee.api.chat.TextComponent;

@CommandInfo(
	name = "edit item item flags add",
	description = "Adds an Item Flag to a physical Minecraft Item for a Brewery Item.",
	usage = "/brewery edit item <item name> item flags add [item flag]",
	maxArgs = 1,
	hideCommand = true
)
public class editItemItemstackFlagsAdd extends editItemItemstackFlags {

	@Override
	public boolean execute(CommandSender sender, String[] args, Object... objects) {
		
		// Checking if flag name is grabbable
		if(args.length < 1) {
			Messenger.send(sender, "&ePlease provide an item flag.");
			return true;
		}
		
		// Grabbing the brItem from objects
		BrItem brItem = (BrItem) objects[0];
		
		// Grabbing the itemstack from brItem
		ItemStack currentItem = brItem.getItemStack();
		
		String successMsg = "&aAdded Item Flag to &7"+brItem.getName()+"&a. Hover for details!";
		// Saves all current flags onto hover text
		String successHoverText = "&2Item Flags:" + StringsUtil.formatter(s -> "\n&e> &7"+s[0], StringsUtil.enumValueStrings(ItemsUtil.getFlags(currentItem).toArray(new ItemFlag[0])));
		
		ItemFlag itemFlag = null;
		// If all itemflags are already present, send appropriate message and do nothing
		if(ItemsUtil.getFlags(currentItem).size() == ItemFlag.values().length){
			Messenger.send(sender, StringsUtil.hoverText("&eAll item flags are already present on &7"+brItem.getName()+"&e. Hover to see!"
					, successHoverText));
			return true;
		}
		
		// Checking if all flags need to be added
		if(args[0].equalsIgnoreCase("all")) {
			// If so, add all flags and change successmsg to it
			brItem.setItemStack(ItemsUtil.addFlags(currentItem));
			successMsg = "&aAdded all Item Flags to &7"+brItem.getName()+"&a. Hover for details!";
			
			// Printing all item flags in 'added' format
			successHoverText = "&2Item Flags:" + StringsUtil.formatter(s -> "\n&a> &f&l"+s[0], StringsUtil.enumValueStrings(ItemFlag.values()));
		} 
		// If specific flag has been provided
		else {
			// Try to find the itemFlag from given argument
			try {
				itemFlag = StringsUtil.constructEnum(args[0], ItemFlag.class);
			} 
			// If cannot be found, return with failed message
			catch (IllegalArgumentException e) {
				Messenger.send(sender, "&7"+args[0]+" &cis not a valid Item Flag.");
				return true;
			}
			
			// If the specified itemflag is already present, send appropriate message and do nothing
			if(ItemsUtil.getFlags(currentItem).contains(itemFlag)){
				// Sends hover message showing all current flags
				Messenger.send(sender, StringsUtil.hoverText("&eThat Item Flagflag is already present on &7"+brItem.getName()+"&e. Hover to see them all!", successHoverText));
				return true;
			}
			
			// Hover text at this point has all of the items current flags.
			// This adds the new one on the end with an 'adding' format of green dial, white & bolded text!
			successHoverText += "\n&a> &f&l"+itemFlag;
			
			// Sasve the itemstack to item
			brItem.setItemStack(ItemsUtil.addFlags(currentItem, itemFlag));
		}
		// Allows the user to view the details onhover
		TextComponent successMsgComponent = StringsUtil.hoverText(successMsg, successHoverText);
		
		// Save YML
		BrItem.YML().save(brItem);
		
		// Send successmsg
		Messenger.send(sender, successMsgComponent);
		return true;
	}
	
	@Override
	public List<String> tabComplete(CommandSender sender, String[] args, Object... objects) {
		switch(args.length) {
			case 1: 
				List<String> flags = Arrays.asList(StringsUtil.enumValueStrings(ItemFlag.class));
				// This is here to avoid an UnsupportedOperationException. When using Arrays.asList, the returned list actually cannot use the .add() method.
				flags = new ArrayList<String>(flags);
				flags.add("ALL");
				return flags;
		}
		return Arrays.asList();
	}
}
