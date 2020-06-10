package com.Patane.Brewery.Commands.secondary.edit.item.itemstack;

import java.util.Arrays;
import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.inventory.ItemStack;

import com.Patane.Brewery.Commands.secondary.edit.item.editItemItemstack;
import com.Patane.Brewery.CustomItems.BrItem;
import com.Patane.Commands.CommandInfo;
import com.Patane.util.general.Messenger;
import com.Patane.util.general.StringsUtil;
import com.Patane.util.ingame.Commands;
import com.Patane.util.ingame.ItemsUtil;

import net.md_5.bungee.api.chat.TextComponent;

@CommandInfo(
	name = "edit item item name",
	description = "Sets the display name of a physical Minecraft Item for a Brewery Item. Blank names remove the display name.",
	usage = "/brewery edit item <item name> item name (display name)",
	maxArgs = 1,
	hideCommand = true
)
public class editItemItemstackName extends editItemItemstack {

	@Override
	public boolean execute(CommandSender sender, String[] args, Object... objects) {
		
		
		
		// Grabbing the brItem from objects
		BrItem brItem = (BrItem) objects[0];
		
		// Grabbing the itemstack from brItem
		ItemStack currentItem = brItem.getItemStack();
		
		String successMsg = "&aUpdated display name for &7"+brItem.getName()+"&a. Hover to view the item!";
		
		// If there is no display name give, remove the display name
		if(args.length == 0) {
			brItem.setItemStack(ItemsUtil.removeDisplayName(currentItem));
			successMsg = "&aRemoved display name for &7"+brItem.getName()+"&a. Hover to view the item!";
		}
		else {
			// Otherwise, combine the args to get the full display name
			String displayName = Commands.combineArgs(args);
			// And save it to the item
			brItem.setItemStack(ItemsUtil.setDisplayName(currentItem,  displayName));
		}
		
		// Allows the user to see the entire updated item on hover
		TextComponent successMsgComponent = StringsUtil.hoverItem(successMsg,  brItem.getItemStack());
		
		// Save YML
		BrItem.YML().save(brItem);
		
		// Send successmsg
		Messenger.send(sender, successMsgComponent);
		return true;
	}
	
	@Override
	public List<String> tabComplete(CommandSender sender, String[] args, Object... objects) {
		switch(args.length) {
			case 1: return Arrays.asList("(display name)");
		}
		return Arrays.asList();
	}
}
