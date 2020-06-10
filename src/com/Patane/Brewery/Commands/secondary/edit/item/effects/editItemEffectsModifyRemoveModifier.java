package com.Patane.Brewery.Commands.secondary.edit.item.effects;

import java.util.Arrays;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import com.Patane.Brewery.Brewery;
import com.Patane.Brewery.CustomEffects.BrEffect;
import com.Patane.Brewery.CustomEffects.Modifier;
import com.Patane.Brewery.CustomItems.BrItem;
import com.Patane.Commands.CommandInfo;
import com.Patane.util.general.Chat;
import com.Patane.util.general.Messenger;
import com.Patane.util.general.StringsUtil;

import net.md_5.bungee.api.chat.TextComponent;
@CommandInfo(
	name = "edit item effects modify remove modifier",
	aliases = {"mod"},
	description = "Removes the Modifier of an Effect for a Brewery Item. These changes are seperate from the original Effect.",
	usage = "/brewery edit item <item name> effects modify <effect name> remove modifier"
)
public class editItemEffectsModifyRemoveModifier extends editItemEffectsModifyRemove {

	@Override
	public boolean execute(CommandSender sender, String[] args, Object... objects) {
		
		// Find Item
		BrItem item = (BrItem) objects[0]; 
		
		// Find Effect
		BrEffect effect = (BrEffect) objects[1];
		
		// Default message assumes there is no previous modifier, thus 'set' message is given
		String successMsg = "&aRemoved modifier for &7"+item.getName()+"&a's instance of &7"+effect.getName()+"&a. This Effect is now incomplete. Hover to view the details!";
		
		String successHoverText = generateEditingTitle(item, effect);

		Modifier currentModifier = effect.getModifier();
		// Grab the previous modifier for later use
		Modifier defaultModifier = Brewery.getEffectCollection().getItem(effect.getName()).getModifier();
		
		if(defaultModifier == null) {
			// If both default and current modifier are ALREADY removed, do nothing and message appropriately
			if(currentModifier == null) {
				Messenger.send(sender, StringsUtil.hoverText("&eBoth &7"+item.getName()+"&e's instance of &7"+effect.getName()+"&e and the original effect already have no modifier. Hover to view the items effect!"
						, generateEditingTitle(item) + effect.toChatString(0, false)));
				return true;
			}
			// If there is no default modifier but there IS a current modifier, then remove the current modifier
			else {
				// Uses original successMsg
				successHoverText += currentModifier.toChatString(0, true, s -> "&c"+Chat.replace(s[0], "&c")+"&c: &8&m"+Chat.replace(s[1], "&8&m")+"&r");
				
				// Removing the modifier from effect
				effect.setModifier(null);
			}
		} 
		// If there IS a default modifier the current modifier and default do not match, then we are actually removing AND reverting to the default modifier rather than just removing
		else if(!currentModifier.equals(defaultModifier)) {
			successMsg = "&aReverted modifier for &7"+item.getName()+"&a's instance of &7"+effect.getName()+"&a to original effects modifier. Hover to view the details!";
			successHoverText += currentModifier.toChatString(0, true, s -> "&c"+Chat.replace(s[0], "&c")+"&c: &8&m"+Chat.replace(s[1], "&8&m")+"&r")
							  + "\n"
							  + defaultModifier.toChatString(0, true, s -> Chat.add("&2"+s[0]+"&2: &7"+s[1], ChatColor.BOLD));
			
			// Setting modifier to default modifier
			effect.setModifier(defaultModifier);
		}
		// If there IS a default modifier and current/default modifiers are the same, then we cannot remove the modifier from here. Must be done in standard effect edit command
		else {
			Messenger.send(sender, StringsUtil.hoverText("&7"+item.getName()+"&e's instance of &7"+effect.getName()+"&e is using the original effects modifier. You must edit the original effect to remove it. Hover to view the modifier!"
					, successHoverText + currentModifier.toChatString(0, true)));
			return true;
		}
	
		// Save the Item to the YML. This will also save the instance of the effect to the item
		BrItem.YML().save(item);
		
		// Allows the user to view the details on hover
		TextComponent successMsgComponent = StringsUtil.hoverText(successMsg, successHoverText);
		
		Messenger.send(sender, successMsgComponent);
		return true;
	}
	
	@Override
	public List<String> tabComplete(CommandSender sender, String[] args, Object... objects) {
		return Arrays.asList();
	}
}
