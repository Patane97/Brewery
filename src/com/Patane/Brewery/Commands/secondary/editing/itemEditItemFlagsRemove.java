package com.Patane.Brewery.Commands.secondary.editing;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

import com.Patane.Brewery.CustomItems.BrItem;
import com.Patane.Brewery.Editing.EditSession;
import com.Patane.Commands.CommandHandler.CommandPackage;
import com.Patane.Commands.CommandInfo;
import com.Patane.util.YAML.types.YAMLFile;
import com.Patane.util.general.Messenger;
import com.Patane.util.general.StringsUtil;
import com.Patane.util.ingame.ItemsUtil;
@CommandInfo(
	name = "edit item flags remove",
	aliases = {"rem", "delete", "del"},
	description = "Removes an Item Flag from an item.",
	usage = "/brewery edit item flags remove [item flag]",
	maxArgs = 1,
	hideCommand = true
)
public class itemEditItemFlagsRemove extends itemEditItemFlags {

	@Override
	public boolean execute(CommandSender sender, String[] args, Object... objects) {
		ItemFlag itemFlag = null;
		
		BrItem brItem = (BrItem) EditSession.get(sender.getName());
		ItemStack currentItem = brItem.getItemStack();

		String successmsg = null;
		
		if(args[0].equalsIgnoreCase("all")) {
			brItem.setItemStack(ItemsUtil.removeFlags(currentItem));
			successmsg = "&aRemoved &7all &aFlags from item.";
		} else {
			try {
				itemFlag = YAMLFile.getEnumFromString(args[0], ItemFlag.class);
			} catch (Exception e) {}
			
			if(itemFlag == null) {
				Messenger.send(sender, "&7"+args[0]+" &cis an invalid Item Flag.");
				return true;
			}

			brItem.setItemStack(ItemsUtil.removeFlags(currentItem, itemFlag));
			successmsg = "&aRemoved &7"+itemFlag+" &aFlag from item.";
		}
		
		BrItem.YML().save(brItem);
		
		Messenger.send(sender, successmsg);
		return true;
	}
	
	@Override
	public List<String> tabComplete(CommandSender sender, String[] args, CommandPackage thisPackage) {
		if(!(EditSession.get(sender.getName()) instanceof BrItem))
			return Arrays.asList();
		BrItem brItem = (BrItem) EditSession.get(sender.getName());
		List<String> flags = Arrays.asList(StringsUtil.enumValueStrings(ItemsUtil.getFlags(brItem.getItemStack()).toArray(new ItemFlag[0])));
		if(!flags.isEmpty()) {
			flags = new ArrayList<String>(flags);
			flags.add("ALL");
		}
		return flags;
	}
}
