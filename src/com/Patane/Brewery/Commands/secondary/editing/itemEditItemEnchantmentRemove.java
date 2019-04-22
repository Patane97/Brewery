package com.Patane.Brewery.Commands.secondary.editing;

import org.bukkit.NamespacedKey;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;

import com.Patane.Brewery.CustomItems.BrItem;
import com.Patane.Brewery.Editing.EditSession;
import com.Patane.Commands.CommandInfo;
import com.Patane.util.general.Messenger;
@CommandInfo(
	name = "edit item enchantment remove",
	aliases = {"rem", "delete", "del"},
	description = "Removes an Enchantment from an item.",
	usage = "/br edit item enchantment remove [enchantment]",
	hideCommand = true
)
public class itemEditItemEnchantmentRemove extends itemEditItemEnchantment {

	@Override
	public boolean execute(CommandSender sender, String[] args, Object... objects) {
		
		BrItem brItem = (BrItem) EditSession.get(sender.getName());
		ItemStack currentItem = brItem.getItemStack();
		
		Enchantment enchantment = null;
		
		
		// Attempting to find the attachment through its Namespacedkey.
		// Enchantment name must abide by minecrafts ID scheme for enchantments.
		// "Minecraft ID Name" section of https://www.digminecraft.com/lists/enchantment_list_pc.php
		enchantment = Enchantment.getByKey(NamespacedKey.minecraft(args[0].toLowerCase()));
		
		// If the enchantment cannot be found
		if(enchantment == null) {
			Messenger.send(sender, "&7" + args[0] + " &cis not a valid enchantment. Please ensure it is a valid Minecraft ID Enchantment name. &oeg. 'silk_touch'");
			return true;
		}

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
}
