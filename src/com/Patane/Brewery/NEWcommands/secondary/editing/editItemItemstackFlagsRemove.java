package com.Patane.Brewery.NEWcommands.secondary.editing;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

import com.Patane.Brewery.CustomItems.BrItem;
import com.Patane.Commands.CommandInfo;
import com.Patane.util.general.Messenger;
import com.Patane.util.general.StringsUtil;
import com.Patane.util.ingame.ItemsUtil;

import net.md_5.bungee.api.chat.TextComponent;

@CommandInfo(
	name = "edit item item flags remove",
	aliases = {"rem", "delete", "del"},
	description = "Removes an Item Flag from a physical Minecraft Item for a Brewery Item.",
	usage = "/brewery edit item <item name> item flags remove [item flag]",
	maxArgs = 1,
	hideCommand = true
)
public class editItemItemstackFlagsRemove extends editItemItemstackFlags {

	@Override
	public boolean execute(CommandSender sender, String[] args, Object... objects) {
		
		// Checking if flag name is grabbable
		if(args.length < 1) {
			Messenger.send(sender, "&ePlease specify an item flag.");
			return true;
		}
		
		// Grabbing the brItem from objects
		BrItem item = (BrItem) objects[0];
		
		// Grabbing the itemstack from brItem
		ItemStack currentItem = item.getItemStack();
		
		String successMsg = "&aRemoved Item Flag from &7"+item.getName()+"&a. Hover for details!";
		// Saves all current flags onto hover text
		String successHoverText = "&f&l"+item.getNameLimited(15)+"&f&l's item flags";
		
		ItemFlag itemFlag = null;
		
		// Checking if all flags need to be removed
		if(args[0].equalsIgnoreCase("all")) {
			// Success message updated
			successMsg = "&aRemoved all Item Flags from &7"+item.getName()+"&a. Hover for details!";
			// Printing all the flags being removed in 'remove' format
			successHoverText += StringsUtil.singleColumnFormatter(0, s -> "\n&4> &8&m"+s[0], StringsUtil.enumValueStrings(ItemsUtil.getFlags(currentItem).toArray(new ItemFlag[0])));
			
			// Remove all item flags
			item.setItemStack(ItemsUtil.removeFlags(currentItem));
		} 
		// If specific flag has been provided
		else {
			// Try to find the itemFlag from given argument
			try {
				itemFlag = StringsUtil.constructEnum(args[0], ItemFlag.class);
			} 
			// If cannot be found, return with failed message
			catch (IllegalArgumentException e) {
				Messenger.send(sender, "&7"+args[0]+" &cis not a valid Item Flag.");
				return true;
			}
			
			// If specified itemflag isnt in ite, send appropriate message and do nothing
			if(!ItemsUtil.getFlags(currentItem).contains(itemFlag)) {
				// Sends hover message showing all current flags
				Messenger.send(sender, StringsUtil.hoverText("&7"+item.getName()+"&e does not have that Item Flag. Hover to view which ones it does have!"
						, (ItemsUtil.getFlags(currentItem).isEmpty() ? successHoverText+"\n&8No Flags!" : successHoverText+StringsUtil.singleColumnFormatter(0, s -> "\n&2> &7"+s[0], StringsUtil.enumValueStrings(ItemsUtil.getFlags(currentItem).toArray(new ItemFlag[0]))))));
				return true;
			}
			// Save the itemstack to item
			item.setItemStack(ItemsUtil.removeFlags(currentItem, itemFlag));
			
			// Item Flag list has been updated, so update hover text PLUS adding the removed flag with 'remove' format
			successHoverText += StringsUtil.singleColumnFormatter(0, s -> "\n&2> &7"+s[0], StringsUtil.enumValueStrings(ItemsUtil.getFlags(currentItem).toArray(new ItemFlag[0])))
							  + "\n&4> &8&m"+itemFlag;
		}
		// Allows the user to view the details onhover
		TextComponent successMsgComponent = StringsUtil.hoverText(successMsg, successHoverText);
		
		// Save YML
		BrItem.YML().save(item);
		
		// Send successmsg
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
				// Saves all flags item has into list
				List<String> flags = Arrays.asList(StringsUtil.enumValueStrings(ItemsUtil.getFlags(brItem.getItemStack()).toArray(new ItemFlag[0])));
				// If item HAS flags, also add the 'ALL' option
				if(!flags.isEmpty()) {
					flags = new ArrayList<String>(flags);
					flags.add("ALL");
				}
				return flags;
		}
		return Arrays.asList();
	}
}
