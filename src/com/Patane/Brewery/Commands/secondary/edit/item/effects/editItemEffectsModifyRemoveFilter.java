package com.Patane.Brewery.Commands.secondary.edit.item.effects;

import java.util.Arrays;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import com.Patane.Brewery.Brewery;
import com.Patane.Brewery.CustomEffects.BrEffect;
import com.Patane.Brewery.CustomEffects.Filter;
import com.Patane.Brewery.CustomItems.BrItem;
import com.Patane.Commands.CommandInfo;
import com.Patane.util.general.Chat;
import com.Patane.util.general.Messenger;
import com.Patane.util.general.StringsUtil;

import net.md_5.bungee.api.chat.TextComponent;
@CommandInfo(
	name = "edit item effects modify remove filter",
	description = "Removes the Filter of an Effect for a Brewery Item. These changes are seperate from the original Effect.",
	usage = "/brewery edit item <item name> effects modify <effect name> remove filter"
)
public class editItemEffectsModifyRemoveFilter extends editItemEffectsModifyRemove {

	@Override
	public boolean execute(CommandSender sender, String[] args, Object... objects) {
		
		// Find Item
		BrItem item = (BrItem) objects[0]; 
		
		// Find Effect
		BrEffect effect = (BrEffect) objects[1];
		
		// Default message assumes there is no previous filter, thus 'set' message is given
		String successMsg = "&aRemoved filter for &7"+item.getName()+"&a's instance of &7"+effect.getName()+"&a. Hover to view the details!";
		
		String successHoverText = generateEditingTitle(item, effect);

		Filter currentFilter = effect.getFilter();
		// Grab the previous filter for later use
		Filter defaultFilter = Brewery.getEffectCollection().getItem(effect.getName()).getFilter();
		
		if(!defaultFilter.isActive()) {
			// If both default and current filter are ALREADY removed, do nothing and message appropriately
			if(!currentFilter.isActive()) {
				Messenger.send(sender, StringsUtil.hoverText("&eBoth &7"+item.getName()+"&e's instance of &7"+effect.getName()+"&e and the original effect already have no filter. Hover to view the items effect!"
						, generateEditingTitle(item) + effect.toChatString(0, false)));
				return true;
			}
			// If there is no default filter but there IS a current filter, then remove the current filter
			else {
				// Uses original successMsg
				successHoverText += currentFilter.toChatString(0, false, s -> "&c"+Chat.replace(s[0], "&c")+"&c: &8&m"+Chat.replace(s[1], "&8&m")+"&r");
				
				// Removing the filter from effect
				effect.setFilter(null);
			}
		} 
		// If there IS a default filter the current filter and default do not match, then we are actually removing AND reverting to the default filter rather than just removing
		else if(!currentFilter.equals(defaultFilter)) {
			successMsg = "&aReverted filter for &7"+item.getName()+"&a's instance of &7"+effect.getName()+"&a to original effects filter. Hover to view the details!";
			successHoverText += currentFilter.toChatString(0, false, s -> "&c"+Chat.replace(s[0], "&c")+"&c: &8&m"+Chat.replace(s[1], "&8&m")+"&r")
							  + "\n"
							  + defaultFilter.toChatString(0, false, s -> Chat.add("&2"+s[0]+"&2: &7"+s[1], ChatColor.BOLD));
			
			// Setting filter to default filter
			effect.setFilter(defaultFilter);
		}
		// If there IS a default filter and current/default filters are the same, then we cannot remove the filter from here. Must be done in standard effect edit command
		else {
			Messenger.send(sender, StringsUtil.hoverText("&7"+item.getName()+"&e's instance of &7"+effect.getName()+"&e is using the original effects filter. You must edit the original effect to remove it. Hover to view the filter!"
					, successHoverText + currentFilter.toChatString(0, true)));
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
