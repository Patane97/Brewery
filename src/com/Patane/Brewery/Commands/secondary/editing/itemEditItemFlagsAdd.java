package com.Patane.Brewery.Commands.secondary.editing;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

import com.Patane.Brewery.CustomItems.BrItem;
import com.Patane.Brewery.Editing.EditSession;
import com.Patane.Commands.CommandHandler.CommandPackage;
import com.Patane.Commands.CommandInfo;
import com.Patane.util.general.Messenger;
import com.Patane.util.general.StringsUtil;
import com.Patane.util.ingame.ItemsUtil;
@CommandInfo(
	name = "edit item flags add",
	description = "Adds an Item Flag to an item.",
	usage = "/brewery edit item flags add [item flag]",
	maxArgs = 1,
	hideCommand = true
)
public class itemEditItemFlagsAdd extends itemEditItemFlags {

	@Override
	public boolean execute(CommandSender sender, String[] args, Object... objects) {
		ItemFlag itemFlag = null;

		BrItem brItem = (BrItem) EditSession.get(sender.getName());
		ItemStack currentItem = brItem.getItemStack();
		
		String successmsg = null;
		
		if(args[0].equalsIgnoreCase("all")) {
			brItem.setItemStack(ItemsUtil.addFlags(currentItem));
			successmsg = "&aAdded &7all &aFlags to item.";
		} else {
			try {
				itemFlag = StringsUtil.constructEnum(args[0], ItemFlag.class);
			} catch (IllegalArgumentException e) {
				Messenger.send(sender, "&7"+args[0]+" &cis an invalid Item Flag.");
				return true;
			}
			
			brItem.setItemStack(ItemsUtil.addFlags(currentItem, itemFlag));
			successmsg = "&aAdded &7"+itemFlag+" &aFlag to item.";
		}
		
		BrItem.YML().save(brItem);
		
		Messenger.send(sender, successmsg);
		return true;
	}
	
	@Override
	public List<String> tabComplete(CommandSender sender, String[] args, CommandPackage thisPackage) {
		List<String> flags = Arrays.asList(StringsUtil.enumValueStrings(ItemFlag.class));
		// This is here to avoid an UnsupportedOperationException. When using Arrays.asList, the returned list actually cannot use the .add() method.
		flags = new ArrayList<String>(flags);
		flags.add("ALL");
		return flags;
	}
}
