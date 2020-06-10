package com.Patane.Brewery.Commands.secondary.edit.item;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.command.CommandSender;

import com.Patane.Brewery.Commands.secondary.edit.editItem;
import com.Patane.Brewery.CustomItems.BrItem;
import com.Patane.Commands.CommandInfo;
import com.Patane.util.general.Messenger;
import com.Patane.util.general.StringsUtil;

import net.md_5.bungee.api.chat.TextComponent;

@CommandInfo(
	name = "edit item cooldown",
	description = "Sets the cooldown for a Brewery Item in seconds. Setting to 0 removes the cooldown.",
	usage = "/brewery edit item <item name> cooldown <amount>",
	maxArgs = 1
)
public class editItemCooldown extends editItem {
	
	@Override
	public boolean execute(CommandSender sender, String[] args, Object... objects) {
		
		// Checking amount is given
		if(args.length < 1) {
			Messenger.send(sender, "&ePlease provide a cooldown amount. This must be a positive number or 0.");
			return true;
		}
		
		// Find cooldown
		Float cooldown = null;
		try {
			cooldown = Float.parseFloat(args[0]);
		} catch (NumberFormatException e) {
			// Check if cooldown is a float
			Messenger.send(sender, "&7"+args[0]+" &cis not a valid amount. It must be a positive number or 0.");
			return true;
		}
		
		// Check if cooldown is positive or 0
		if(cooldown < 0) {
			Messenger.send(sender, "&cCooldown must be a positive number or 0.");
			return true;
		}
		
		// Find Item
		BrItem item = (BrItem) objects[0];
	
		String successMsg = "&aChanged &7"+item.getName()+"&a's cooldown. Hover to view the details!";
		
		String successHoverText = generateEditingTitle(item);
		
		if(item.getCooldown() == cooldown) {
			Messenger.send(sender, StringsUtil.hoverText("&7"+item.getName()+"&e's cooldown is already that. Hover to view!"
					, successHoverText + "&2Cooldown: &7"+(item.getCooldown() == 0 ? "&8None" : item.getCooldown())));
			return true;
		}
		
		// If cooldown is 0, remove the cooldown instead
		if(cooldown == 0) {
			cooldown = null;
			successHoverText += "&2Cooldown: &8"+item.getCooldown()+" &7-> None";
		}
		else
			successHoverText += "&2Cooldown: &8"+item.getCooldown()+" &7-> "+cooldown;
		
		// Setting cooldown
		item.setCooldown(cooldown);
		
		// Saving the item to YML
		BrItem.YML().save(item);

		// Allows the user to view the details on hover
		TextComponent successMsgComponent = StringsUtil.hoverText(successMsg, successHoverText);
		
		// Sending the success message
		Messenger.send(sender, successMsgComponent);
		return true;
	}
	
	@Override
	public List<String> tabComplete(CommandSender sender, String[] args, Object... objects) {
		switch(args.length) {
		case 1:
			return Arrays.asList("<amount>");
		default:
			return new ArrayList<String>();
		}
		
	}
}
