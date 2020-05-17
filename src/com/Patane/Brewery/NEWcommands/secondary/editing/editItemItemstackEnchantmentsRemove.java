package com.Patane.Brewery.NEWcommands.secondary.editing;

import java.util.Arrays;
import java.util.List;

import org.bukkit.NamespacedKey;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;

import com.Patane.Brewery.CustomItems.BrItem;
import com.Patane.Commands.CommandInfo;
import com.Patane.util.general.Messenger;
import com.Patane.util.general.StringsUtil;

import net.md_5.bungee.api.chat.TextComponent;

@CommandInfo(
	name = "edit item item enchantments remove",
	aliases = {"rem", "delete", "del"},
	description = "Removes an Enchantment from a physical Minecraft Item for a Brewery Item.",
	usage = "/brewery edit item <item name> item enchantment remove [enchantment]",
	maxArgs = 1,
	hideCommand = true
)
public class editItemItemstackEnchantmentsRemove extends editItemItemstackEnchantments {
	
	@Override
	public boolean execute(CommandSender sender, String[] args, Object... objects) {

		// Checking if enchantment name is grabbable
		if(args.length < 1) {
			Messenger.send(sender, "&ePlease provide an enchantment name.");
			return true;
		}

		// Attempting to find the attachment through its Namespacedkey.
		// Enchantment name must abide by minecrafts ID scheme for enchantments.
		// "Minecraft ID Name" section of https://www.digminecraft.com/lists/enchantment_list_pc.php
		Enchantment enchantment = Enchantment.getByKey(NamespacedKey.minecraft(args[0].toLowerCase().replace(" ", "_")));
		
		/*
		 * This code is much less necessary with TabComplete literally giving the commandsender the options they have
		// If the enchantment could not be found by its proper name, check if they gave a Bukkit enchantment name
		if(enchantment == null) {
			// Checking if they provided a bukkit name (depreciated but could still work)
			enchantment = Enchantment.getByName(args[0].toUpperCase().replace(" ", "_"));
			// If the enchantment cant be recognized as a bukkit or minecraft ID, then its not an enchantment.
			
		}
		*/
		// Checking if enchantment string was an enchantment
		if(enchantment == null) {
			Messenger.send(sender, "&7" + args[0] + " &cis not a valid Enchantment.");
			return true;
		}// Getting BrItem from objects
		BrItem brItem = (BrItem) objects[0];
		
		// Grabbing BrItem Itemstack
		ItemStack currentItem = brItem.getItemStack();
				
		if(!currentItem.containsEnchantment(enchantment)) {
			// Doesnt REALLY need hover text here.
			Messenger.send(sender, "&eThat enchantment is not present on &7"+brItem.getName()+"&e.");
			return true;
		}
		String successMsg = "&aRemoved enchantment from &7"+brItem.getName()+"&a. Hover for details!";
		String successHoverText = StringsUtil.toHoverString(enchantment, currentItem.getEnchantmentLevel(enchantment), s ->"&c"+s[0]+": &8&m"+s[1]+"&r");
		
		// Allows the user to view the details onhover
		TextComponent successMsgComponent = StringsUtil.hoverText(successMsg, successHoverText);
		
		// Removing the enchantment
		currentItem.removeEnchantment(enchantment);
		
		// Saving the itemstack to item
		brItem.setItemStack(currentItem);
		
		// Save YML
		BrItem.YML().save(brItem);
		
		// Send success message
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
				// If the item has no enchantments, return empty list
				if(brItem.getItemStack().getEnchantments().isEmpty())
					return Arrays.asList();
				// Otherwise, gather and send all enchantments
				return StringsUtil.getMCEnchantmentNames(brItem.getItemStack().getEnchantments().keySet().toArray(new Enchantment[0]));
		}
		return Arrays.asList();
		
	}
}
