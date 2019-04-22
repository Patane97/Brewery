package com.Patane.Brewery.Commands.secondary.editing;

import org.bukkit.command.CommandSender;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

import com.Patane.Brewery.CustomItems.BrItem;
import com.Patane.Brewery.Editing.EditSession;
import com.Patane.Commands.CommandInfo;
import com.Patane.util.general.Messenger;
import com.Patane.util.general.StringsUtil;
import com.Patane.util.ingame.ItemsUtil;
@CommandInfo(
	name = "edit item flags add",
	description = "Adds an Item Flag to an item.",
	usage = "/br edit item flags add [item flag]",
	hideCommand = true
)
public class itemEditItemFlagsAdd extends itemEditItemFlags {

	@Override
	public boolean execute(CommandSender sender, String[] args, Object... objects) {
		
		ItemFlag itemFlag = null;
		try {
			itemFlag = StringsUtil.constructEnum(args[0], ItemFlag.class);
		} catch (IllegalArgumentException e) {
			Messenger.send(sender, "&7"+args[0]+" &cis an invalid Item Flag.");
			return true;
		}
		
		if(itemFlag == null) {
		}
		
		BrItem brItem = (BrItem) EditSession.get(sender.getName());
		ItemStack currentItem = brItem.getItemStack();
		
		brItem.setItemStack(ItemsUtil.addFlags(currentItem, itemFlag));
		
		BrItem.YML().save(brItem);
		
		Messenger.send(sender, "&aAdded &7"+itemFlag+" &aFlag to item.");
		return true;
	}
}
