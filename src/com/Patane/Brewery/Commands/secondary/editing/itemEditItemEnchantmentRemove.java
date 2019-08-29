package com.Patane.Brewery.Commands.secondary.editing;

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
	name = "edit item enchantment remove",
	aliases = {"rem", "delete", "del"},
	description = "Removes an Enchantment from an item.",
	usage = "/brewery edit item enchantment remove [enchantment]",
	maxArgs = 1,
	hideCommand = true
)
public class itemEditItemEnchantmentRemove extends itemEditItemEnchantment {

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

		
		BrItem brItem = (BrItem) EditSession.get(sender.getName());
		ItemStack currentItem = brItem.getItemStack();
		
		if(!currentItem.containsEnchantment(enchantment)) {
			Messenger.send(sender, "&cItem does not contain &7" + enchantment.getKey().getKey() + " &cenchantment.");
			return true;
		}
		
		currentItem.removeEnchantment(enchantment);
		
		brItem.setItemStack(currentItem);
		
		BrItem.YML().save(brItem);

		Messenger.send(sender, "&aRemoved &7"+enchantment.getKey().getKey()+" &aenchantment from item.");
		return true;
	}
	
	@Override
	public List<String> tabComplete(CommandSender sender, String[] args, CommandPackage thisPackage) {
		if(!(EditSession.get(sender.getName()) instanceof BrItem))
			return Arrays.asList();
		BrItem brItem = (BrItem) EditSession.get(sender.getName());
		if(brItem.getItemStack().getEnchantments().isEmpty())
			return Arrays.asList();
		return StringsUtil.getMCEnchantmentNames(brItem.getItemStack().getEnchantments().keySet().toArray(new Enchantment[0]));
	}
}
