package com.Patane.Brewery.Commands.secondary.edit.item.itemstack;

import java.util.Arrays;
import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.Patane.Brewery.Commands.secondary.edit.item.editItemItemstack;
import com.Patane.Brewery.CustomItems.BrItem;
import com.Patane.Commands.CommandInfo;
import com.Patane.util.general.Messenger;
import com.Patane.util.general.StringsUtil;
import com.Patane.util.ingame.InventoriesUtil;

import net.md_5.bungee.api.chat.TextComponent;

@CommandInfo(
	name = "edit item item material",
	description = "Sets the material of a physical Minecraft Item for a Brewery Item. Hold an item to set it to that items material.",
	usage = "/brewery edit item <item name> item material",
	hideCommand = true
)
public class editItemItemstackMaterial extends editItemItemstack {

	@Override
	public boolean execute(CommandSender sender, String[] args, Object... objects) {
		// This command required a player holding an item, thus cannot be done if youre not a player
		if(!(sender instanceof Player)) {
			Messenger.send(sender, "You must be a player to change the material of a Brewery Item via commands.");
			return true;
		}
		
		// Saving the player
		Player player = (Player) sender;
		
		// Grabbing the brItem from objects
		BrItem brItem = (BrItem) objects[0];
				
		ItemStack handItem = InventoriesUtil.getHand(player.getInventory(), true);

		// Making sure they are holding an item
		if(handItem == null) {
			Messenger.send(player, "&ePlease hold an item you with to use as the new itemstack material.");
			return true;
		}
		
		String successMsg = String.format("&aUpdated material for &7%s&a. Hover to view the item!", brItem.getName());
		
		// Set the new item as the brItem itemstack
		brItem.setItemStack(handItem);
		
		// Allows the user to see the entire updated item on hover
		TextComponent successMsgComponent = StringsUtil.hoverItem(successMsg,  brItem.getItemStack());
		
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
		switch(args.length) {
			case 1: return Arrays.asList("(display name)");
		}
		return Arrays.asList();
	}
}
