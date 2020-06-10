package com.Patane.Brewery.Commands.secondary.edit.effect;

import java.util.Arrays;
import java.util.List;

import org.bukkit.command.CommandSender;

import com.Patane.Brewery.CustomEffects.BrEffect;
import com.Patane.Commands.CommandInfo;
import com.Patane.util.general.Messenger;
import com.Patane.util.general.StringsUtil;

import net.md_5.bungee.api.chat.TextComponent;
@CommandInfo(
	name = "edit effect set radius",
	description = "Sets or removes the Radius of an original Effect.",
	usage = "/brewery edit effect <effect name> set radius <amount>"
)
public class editEffectSetRadius extends editEffectSet {

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
			Messenger.send(sender, String.format("&7%s &cis not a valid amount.", args[0]));
			return true;
		}
		
		// Checking amount is positive or 0
		if(amount < 0) {
			Messenger.send(sender, "&cRadius must be a positive number or 0.");
			return true;
		}
		
		// Find Effect
		BrEffect effect = (BrEffect) objects[0];
		
		// Saving previous amount for later use. This can be null
		Float previousAmount = effect.getRadius();
		
		String successMsg = String.format("&aSet the Radius of &7%s&a. Hover to view the details!", effect.getName());
		
		String successHoverText = generateEditingTitle(effect);
		
		if(previousAmount != null) {
			// If the radius amount has not changed, do nothing and send appropriate message
			if(amount.equals(previousAmount)) {
				Messenger.send(sender, StringsUtil.hoverText(String.format("&7%s&e already has that radius. Hover to view!", effect.getName())
						, successHoverText + "&2Radius: &7"+previousAmount));
				return true;
			}
			// Add hover text to show radius amount being compared to previous
			successHoverText += StringsUtil.singleRowCompareFormatter(0,
								s -> "&2"+s[0]+"&2: &7"+s[1]
							  , s -> "&2"+s[0]+"&2: &8"+s[1]+" &7-> "+s[2]
							  , "Radius", Float.toString(previousAmount), Float.toString(amount));
		}
		// If there was previously no radius, then add it!
		else {
			// SuccessMsg is an 'added' message
			successMsg = String.format("&aAdded a Radius to &7%s&a. Hover to view the details!", effect.getName());
			
			successHoverText += "&2&lRadius: &7&l"+amount;
		}
		
		// Set radius to effect
		effect.setRadius(amount);

		// Save the Effect to YML
		BrEffect.YML().save(effect);
		
		// Updates all items that contain references to this effect. Doing this updates any relevant changes to the items effect.
		effect.updateReferences();
		
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
