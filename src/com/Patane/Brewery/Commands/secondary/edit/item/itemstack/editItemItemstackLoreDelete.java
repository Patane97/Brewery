package com.Patane.Brewery.Commands.secondary.edit.item.itemstack;

import java.util.Arrays;
import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.inventory.ItemStack;

import com.Patane.Brewery.CustomItems.BrItem;
import com.Patane.Commands.CommandInfo;
import com.Patane.util.general.Messenger;
import com.Patane.util.general.StringsUtil;
import com.Patane.util.ingame.ItemsUtil;

import net.md_5.bungee.api.chat.TextComponent;

@CommandInfo(
	name = "edit item item lore delete",
	aliases = {"del", "remove", "rem"},
	description = "Deletes a line of Lore for a physical Minecraft Item for a Brewery Item.",
	usage = "/brewery edit item <item name> item lore delete <line>",
	maxArgs = 1,
	hideCommand = true
)
public class editItemItemstackLoreDelete extends editItemItemstackLore {

	@Override
	public boolean execute(CommandSender sender, String[] args, Object... objects) {
		
		// Checking if line number is grabbable
		if(args.length < 1) {
			Messenger.send(sender, "&ePlease provide a line number. This must be a positive number.");
			return true;
		}
		Integer line = null;
		try {
			line = Integer.parseInt(args[0]);
		} catch (Exception e) {
			Messenger.send(sender, "&7"+args[0]+" &cis not a valid line number. It must be a positive number.");
			return true;
		}
		if(line <= 0) {
			Messenger.send(sender, "&cLine number must be above 0.");
			return true;
		}
		
		// Grabbing the brItem from objects
		BrItem brItem = (BrItem) objects[0];
		
		// Grabbing the itemstack from brItem
		ItemStack currentItem = brItem.getItemStack();
		
		// Grabbing the lore list
		List<String> lore = ItemsUtil.getLore(currentItem);
		
		// If there is no lore, create empty array to start it
		if(lore == null || line > lore.size()) {
			Messenger.send(sender, StringsUtil.hoverItem("&eThere is no line &7"+line+"&e for this lore. Hover to view the item!", currentItem));
			return true;
		}
		
		// Removing the line of lore
		lore.remove(line-1);

		// Save successmsg
		String successMsg = "&aRemoved line from lore for &7"+brItem.getName()+"&a. Hover to view the item!";
		
		// If the lore is empty, remove it completely from itemstack
		if(lore.isEmpty())
			brItem.setItemStack(ItemsUtil.removeLore(currentItem));
		// Otherwise just update the itemstack
		else
			brItem.setItemStack(ItemsUtil.setLore(currentItem, lore));
		
		
		// Allows the user to see the entire updated item on hover
		TextComponent successMsgComponent = StringsUtil.hoverItem(successMsg, brItem.getItemStack());
		
		// Save YML
		BrItem.YML().save(brItem);

		// Refreshes all inventory brItems.
		BrItem.refreshAllInventories();
		
		// Send successmsg
		Messenger.send(sender, successMsgComponent);
		return true;
	}
	
	@Override
	public List<String> tabComplete(CommandSender sender, String[] args, Object... objects) {
		BrItem brItem = (BrItem) objects[0];
		if(brItem == null)
			return Arrays.asList();
		
		switch(args.length) {
			case 1:
				// Saves current lore for later use
				List<String> lore = ItemsUtil.getLore(brItem.getItemStack());
				
				if(lore == null || lore.isEmpty())
					return Arrays.asList();
				else
					return StringsUtil.listCount(lore);
		}
		return Arrays.asList();
	}
}
