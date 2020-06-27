package com.Patane.Brewery.Commands.secondary.edit.item.effects;

import java.util.Arrays;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import com.Patane.Brewery.CustomEffects.BrEffect;
import com.Patane.Brewery.CustomItems.BrItem;
import com.Patane.Commands.CommandInfo;
import com.Patane.util.formables.Radius;
import com.Patane.util.formables.Radius.RadiusType;
import com.Patane.util.general.Chat;
import com.Patane.util.general.Messenger;
import com.Patane.util.general.StringsUtil;

import net.md_5.bungee.api.chat.TextComponent;
@CommandInfo(
	name = "edit item effects modify set radius",
	description = "Sets or removes the Radius of an Effect for a Brewery Item. These changes are seperate from the original Effect.",
	usage = "/brewery edit item <item name> effects modify <effect name> set radius <type> <amount>",
	maxArgs = 2
)
public class editItemEffectsModifySetRadius extends editItemEffectsModifySet {

	@Override
	public boolean execute(CommandSender sender, String[] args, Object... objects) {

		// Checking type is given
		if(args.length < 1) {
			Messenger.send(sender, "&ePlease specify a type.");
			return true;
		}
		
		// Checking/Saving radius type
		RadiusType type = null;
		try {
			type = StringsUtil.constructEnum(args[0], RadiusType.class);
		} catch (IllegalArgumentException e) {
			Messenger.send(sender, String.format("&7%s &cis not a valid Radius Type.", args[0]));
			return true;
		}
		
		// Checking radius is given
		if(args.length < 2) {
			Messenger.send(sender, "&ePlease specify an amount.");
			return true;
		}
		
		Float amount = null;
		
		// Attempting to parse the amount as a float
		try {
			amount = Float.parseFloat(args[1]);
		} catch (NumberFormatException e) {
			// If amount is not recognised as a number (eg. "5/")
			Messenger.send(sender, String.format("&7%s &cis not a valid amount.", args[1]));
			return true;
		}
		
		// Checking amount is positive or 0
		if(amount <= 0) {
			Messenger.send(sender, "&cRadius must be a positive number.");
			return true;
		}
		Radius radius = new Radius(type, amount);
		
		// Find Item
		BrItem item = (BrItem) objects[0]; 
		
		// Find Effect
		BrEffect effect = (BrEffect) objects[1];

		// Saving the previous radius for later use
		Radius previousRadius = effect.getRadius();
		
		String successMsg = String.format("&aAdded a Radius to &7%s&a's instance of &7%s&a. Hover to view the details!", item.getName(), effect.getName());
		
		String successHoverText = generateEditingTitle(item, effect);
		
		if(previousRadius != null) {
			// If the radius values are the same, do nothing and message appropriately
			if(radius.equals(previousRadius)) {
				Messenger.send(sender, StringsUtil.hoverText(String.format("&7%s&e's instance of &7%s&e already has a radius with those values. Hover to view!", item.getName(), effect.getName())
															, successHoverText + previousRadius.toChatString(0, true)));
				return true;
			}
			// If its different, then it is changing
			successMsg = String.format("&aChanged the Radius for &7%s&a's instance of &7%s&a. Hover to view the details!", item.getName(), effect.getName());
			// Add hover text to show radius amount being compared to previous
			successHoverText += "&2"+radius.className()+":\n"
							  + StringsUtil.tableCompareFormatter(0,
								s -> "&2  "+s[0]+"&2: &7"+s[1]
							  , s -> "&2  "+s[0]+"&2: &8"+s[1]+" &7-> "+s[2]
							  , StringsUtil.getFieldNames(Radius.class) , StringsUtil.prepValueStrings(previousRadius) , StringsUtil.prepValueStrings(radius));
		}
		// If there was previously no radius, then add it!
		else {
			successHoverText += Chat.add(radius.toChatString(0, true), ChatColor.BOLD);
		}
		
		// Set radius to effect
		effect.setRadius(radius);
		
		// Save to YML
		BrItem.YML().save(item);
		
		// Allows the user to view the details on hover
		TextComponent successMsgComponent = StringsUtil.hoverText(successMsg, successHoverText);
		
		Messenger.send(sender, successMsgComponent);
		return true;
	}
	
	@Override
	public List<String> tabComplete(CommandSender sender, String[] args, Object... objects) {
		switch(args.length) {
			case 1: return Arrays.asList(StringsUtil.enumValueStrings(RadiusType.class));
			case 2: return Arrays.asList("<amount>");
		}
		return Arrays.asList();
	}
}
