package com.Patane.Brewery.commands.secondary.editing;

import java.util.Arrays;
import java.util.List;

import org.bukkit.NamespacedKey;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;

import com.Patane.Brewery.CustomItems.BrItem;
import com.Patane.Brewery.Editing.EditSession;
import com.Patane.Commands.CommandHandler.CommandPackage;
import com.Patane.Commands.CommandInfo;
import com.Patane.util.general.Messenger;
import com.Patane.util.general.StringsUtil;
@CommandInfo(
	name = "edit item enchantment add",
	aliases = {"set"},
	description = "Adds an Enchantment to an item.",
	usage = "/brewery edit item enchantment add [enchantment] <level>",
	maxArgs = 2,
	hideCommand = true
)
public class itemEditItemEnchantmentAdd extends itemEditItemEnchantment {

	@SuppressWarnings("deprecation")
	@Override
	public boolean execute(CommandSender sender, String[] args, Object... objects) {
		Enchantment enchantment = null;
		
		// Attempting to find the attachment through its Namespacedkey.
		// Enchantment name must abide by minecrafts ID scheme for enchantments.
		// "Minecraft ID Name" section of https://www.digminecraft.com/lists/enchantment_list_pc.php
		enchantment = Enchantment.getByKey(NamespacedKey.minecraft(args[0].toLowerCase().replace(" ", "_")));
		
		// If the enchantment cannot be found
		if(enchantment == null) {
			// Checking if they provided a bukkit name (depreciated but could still work)
			enchantment = Enchantment.getByName(args[0].toUpperCase().replace(" ", "_"));
			// If the enchantment cant be recognized as a bukkit or minecraft ID, then its not an enchantment.
			if(enchantment == null) {
				Messenger.send(sender, "&7" + args[0] + " &cis not a valid enchantment.");
				return true;
			}
		}

		Integer level = null;
		if(args.length < 2)
			level = 1;
		else {
			try {
				level = Integer.parseInt(args[1]);
			} catch (Exception e) {
				Messenger.send(sender, "&7"+args[1]+" &cis an invalid enchantment level number.");
				return true;
			}
		}
		
		BrItem brItem = (BrItem) EditSession.get(sender.getName());
		ItemStack currentItem = brItem.getItemStack();
		
		// Just to show if enchantment was added or updated.
		String successMsg = "&aAdded &7"+enchantment.getKey().getKey()+" &aEnchantment at level &7"+level+" &ato item.";
		if(currentItem.containsEnchantment(enchantment)) {
			successMsg = "&aSet &7"+enchantment.getKey().getKey()+" &aEnchantment to level &7"+level+" &afor item.";
		}
		
		currentItem.addUnsafeEnchantment(enchantment, level);
		
		brItem.setItemStack(currentItem);
		
		BrItem.YML().save(brItem);
		
		Messenger.send(sender, successMsg);
		return true;
	}
	
	@Override
	public List<String> tabComplete(CommandSender sender, String[] args, CommandPackage thisPackage) {
		switch(args.length) {
			case 5: return StringsUtil.getMCEnchantmentNames();
			default: return Arrays.asList("<level>");
		}
	}
}
