package com.Patane.Brewery.NEWcommands.secondary.editing;

import java.util.Arrays;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.NamespacedKey;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;

import com.Patane.Brewery.CustomItems.BrItem;
import com.Patane.Commands.CommandInfo;
import com.Patane.util.general.Chat;
import com.Patane.util.general.Messenger;
import com.Patane.util.general.StringsUtil;

import net.md_5.bungee.api.chat.TextComponent;

@CommandInfo(
	name = "edit item item enchantments add",
	aliases = {"set"},
	description = "Adds an Enchantment to a physical Minecraft Item for a Brewery Item.",
	usage = "/brewery edit item <item name> item enchantments add [enchantment] (level)",
	maxArgs = 2,
	hideCommand = true
)
public class editItemItemstackEnchantmentsAdd extends editItemItemstackEnchantments {

	@Override
	public boolean execute(CommandSender sender, String[] args, Object... objects) {
		
		// Checking if enchantment name is grabbable
		if(args.length < 1) {
			Messenger.send(sender, "&ePlease specify an enchantment.");
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
		}
		
		Integer level = null;

		// If no level was provided, default to 1
		if(args.length < 2)
			level = 1;
		// ... otherwise, find it below
		else {
			try {
				level = Integer.parseInt(args[1]);
			} catch (Exception e) {
				Messenger.send(sender, "&7"+args[1]+" &cis not a valid Enchantment Level. It must be a positive number.");
				return true;
			}
		}
		
		// Getting BrItem from objects
		BrItem item = (BrItem) objects[0];
		
		// Grabbing BrItem Itemstack
		ItemStack currentItem = item.getItemStack();

		String successMsg = "&aAdded enchantment to &7"+item.getName()+"&a. Hover for details!";
		String successHoverText = "&f&l"+item.getNameLimited(15)+"&f&l's enchantments\n"
								+ StringsUtil.toChatString(0, true, s -> "&2"+s[0]+"&2: &7"+s[1], currentItem.getEnchantments());
		
		if(currentItem.containsEnchantment(enchantment)) {
			Integer oldLevel = currentItem.getEnchantmentLevel(enchantment);
			// If the enchantment was previously the same level, do nothing and message appropriately
			if(oldLevel.equals(level)) {
				Messenger.send(sender, StringsUtil.hoverText("&eThat enchantment and level for &7"+item.getName()+"&e already exist. Hover for details!", successHoverText));
				return true;
			}
			successMsg = "&aUpdated existing enchantment for &7"+item.getName()+"&a. Hover for details!";
			// If the level has changed, show it changing on hover
			successHoverText = "&2Enchantment: &7"+enchantment.getKey().getKey() 
							+"\n" + StringsUtil.singleRowCompareFormatter(1, s -> "&2"+s[0]+": &7"+s[1]
																		, s -> "&2"+s[0]+": &8"+s[1]+" &7-> "+s[2]
																		, "Level", oldLevel.toString(), level.toString());
		}
		else
			successHoverText += "\n"+Chat.add(StringsUtil.toChatString(0, true, s -> "&2"+s[0]+"&2: &7"+s[1], enchantment, level), ChatColor.BOLD);
		
		// Allows the user to view the details onhover
		TextComponent successMsgComponent = StringsUtil.hoverText(successMsg, successHoverText);
		
		// Adding the enchantment to itemstack. We use unsafe as it does not limit us on levels.
		currentItem.addUnsafeEnchantment(enchantment, level);
		// Save itemstack to item
		item.setItemStack(currentItem);
		
		// Save the YML
		BrItem.YML().save(item);
		
		// Send the success message
		Messenger.send(sender, successMsgComponent);
		return true;
	}
	
	@Override
	public List<String> tabComplete(CommandSender sender, String[] args, Object... objects) {
		switch(args.length) {
			case 1: return StringsUtil.getMCEnchantmentNames();
			case 2: return Arrays.asList("<level>");
		}
		return Arrays.asList();
	}
}
