package com.Patane.Brewery.Commands.secondary.editing;

import org.bukkit.command.CommandSender;
import org.bukkit.inventory.ItemStack;

import com.Patane.Brewery.CustomItems.BrItem;
import com.Patane.Brewery.Editing.EditSession;
import com.Patane.Commands.CommandInfo;
import com.Patane.util.general.Messenger;
import com.Patane.util.ingame.Commands;
import com.Patane.util.ingame.ItemsUtil;
@CommandInfo(
	name = "edit item name",
	description = "Edits an items display name. Setting a blank name will remove the display name.",
	usage = "/br edit item name (item name)",
	hideCommand = true
)
public class itemEditItemName extends itemEditItem {

	@Override
	public boolean execute(CommandSender sender, String[] args, Object... objects) {
		BrItem brItem = (BrItem) EditSession.get(sender.getName());
		ItemStack currentItem = brItem.getItemStack();
		
		if(args.length == 0) {
			brItem.setItemStack(ItemsUtil.removeDisplayName(currentItem));
			BrItem.YML().save(brItem);
			Messenger.send(sender, "&aDisplay name removed.");
			return true;
		}
		String itemName = Commands.combineArgs(args);
		
		brItem.setItemStack(ItemsUtil.setDisplayName(currentItem, itemName));
		BrItem.YML().save(brItem);
		
		Messenger.send(sender, "&aDisplay name set to &7"+itemName+"&a.");
		return true;
	}
}
