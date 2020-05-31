package com.Patane.Brewery.NEWcommands.secondary.editing;

import java.util.Arrays;
import java.util.List;

import org.bukkit.command.CommandSender;

import com.Patane.Brewery.CustomEffects.BrEffect;
import com.Patane.Brewery.CustomItems.BrItem;
import com.Patane.Commands.CommandInfo;
import com.Patane.util.general.Messenger;
import com.Patane.util.general.StringsUtil;

import net.md_5.bungee.api.chat.TextComponent;
@CommandInfo(
	name = "edit item effects set radius",
	description = "Sets or Removes the Radius of an Effect for a Brewery Item. These changes are seperate from the original Effect.",
	usage = "/brewery edit item <item name> effects set <effect name> radius <amount>"
)
public class editItemEffectsSetRadius extends editItemEffectsSet {

	@Override
	public boolean execute(CommandSender sender, String[] args, Object... objects) {
		
		// Checking radius is given
		if(args.length < 1) {
			Messenger.send(sender, "&ePlease specify an amount.");
			return true;
		}
		
		Float amount = null;
		
		// Attempting to parse the amount as a float
		try {
			amount = Float.parseFloat(args[0]);
		} catch (NumberFormatException e) {
			// If amount is not recognised as a number (eg. "5/")
			Messenger.send(sender, "&7"+args[0]+" &cis not a valid amount.");
			return true;
		}
		
		// Checking amount is positive or 0
		if(amount < 0) {
			Messenger.send(sender, "&cRadius must be a positive number or 0.");
			return true;
		}
		
		// Find Item
		BrItem item = (BrItem) objects[0]; 
		
		// Find Effect
		BrEffect effect = (BrEffect) objects[1];
		
		// Saving previous amount for later use
		float previousAmount = effect.getRadius();
		
		String successMsg = "&aSet the Radius of &7"+item.getName()+"&a's instance of &7"+effect.getName()+"&a. Hover to view the details!";
		
		String successHoverText = generateEditingTitle(item, effect);
		
		// If the radius amount has not changed, do nothing and send appropriate message
		if(amount == previousAmount) {
			Messenger.send(sender, StringsUtil.hoverText("&e&7"+item.getName()+"&e's instance of &7"+effect.getName()+"&e already has that radius. Hover to view!"
					, successHoverText + "&2Radius: &7"+previousAmount));
			return true;
		}
		
		// Add hover text to show radius amount being compared to previous
		successHoverText += StringsUtil.singleRowCompareFormatter(0,
							s -> "&2"+s[0]+"&2: &7"+s[1]
						  , s -> "&2"+s[0]+"&2: &8"+s[1]+" &7-> "+s[2]
						  , "Radius", Float.toString(previousAmount), Float.toString(amount));
		
		// Set radius to effect
		effect.setRadius(amount);
		
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
			case 1: return Arrays.asList("<amount>");
		}
		return Arrays.asList();
	}
}
