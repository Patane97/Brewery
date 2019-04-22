package com.Patane.Brewery.Commands.secondary.editing;

import org.bukkit.command.CommandSender;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

import com.Patane.Brewery.CustomItems.BrItem;
import com.Patane.Brewery.Editing.EditSession;
import com.Patane.Commands.CommandInfo;
import com.Patane.util.YAML.types.YAMLFile;
import com.Patane.util.general.Messenger;
import com.Patane.util.ingame.ItemsUtil;
@CommandInfo(
	name = "edit item flags remove",
	aliases = {"rem", "delete", "del"},
	description = "Removes an Item Flag from an item.",
	usage = "/br edit item flags remove [item flag]",
	hideCommand = true
)
public class itemEditItemFlagsRemove extends itemEditItemFlags {

	@Override
	public boolean execute(CommandSender sender, String[] args, Object... objects) {
		
		ItemFlag itemFlag = null;
		try {
			itemFlag = YAMLFile.getEnumFromString(args[0], ItemFlag.class);
		} catch (Exception e) {}
		
		if(itemFlag == null) {
			Messenger.send(sender, "&7"+args[0]+" &cis an invalid Item Flag.");
			return true;
		}
		
		BrItem brItem = (BrItem) EditSession.get(sender.getName());
		ItemStack currentItem = brItem.getItemStack();
		
		brItem.setItemStack(ItemsUtil.removeFlags(currentItem, itemFlag));
		
		BrItem.YML().save(brItem);
		
		Messenger.send(sender, "&aRemoved &7"+itemFlag+" &aFlag from item.");
		return true;
	}
}
