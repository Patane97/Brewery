package com.Patane.Brewery.NEWcommands.secondary.editing;

import java.util.Arrays;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import com.Patane.Brewery.Brewery;
import com.Patane.Brewery.CustomEffects.BrEffect;
import com.Patane.Brewery.CustomEffects.BrEffect.BrTag;
import com.Patane.Brewery.CustomItems.BrItem;
import com.Patane.Commands.CommandInfo;
import com.Patane.util.general.Chat;
import com.Patane.util.general.Messenger;
import com.Patane.util.general.StringsUtil;

import net.md_5.bungee.api.chat.TextComponent;
@CommandInfo(
	name = "edit item effects modify remove tag",
	description = "Removes the Tag of an Effect for a Brewery Item. These changes are seperate from the original Effect.",
	usage = "/brewery edit item <item name> effects modify <effect name> remove tag"
)
public class editItemEffectsModifyRemoveTag extends editItemEffectsModifyRemove {

	@Override
	public boolean execute(CommandSender sender, String[] args, Object... objects) {
		
		// Find Item
		BrItem item = (BrItem) objects[0]; 
		
		// Find Effect
		BrEffect effect = (BrEffect) objects[1];
		
		// Default message assumes there is no previous tag, thus 'set' message is given
		String successMsg = "&aRemoved tag for &7"+item.getName()+"&a's instance of &7"+effect.getName()+"&a. Hover to view the details!";
		
		String successHoverText = generateEditingTitle(item, effect);

		BrTag currentTag = effect.getTag();
		// Grab the previous tag for later use
		BrTag defaultTag = Brewery.getEffectCollection().getItem(effect.getName()).getTag();
		
		if(defaultTag == null) {
			// If both default and current tag are ALREADY removed, do nothing and message appropriately
			if(currentTag == null) {
				Messenger.send(sender, StringsUtil.hoverText("&eBoth &7"+item.getName()+"&e's instance of &7"+effect.getName()+"&e and the original effect already have no tag. Hover to view the items effect!"
						, generateEditingTitle(item) + effect.toChatString(0, false)));
				return true;
			}
			// If there is no default tag but there IS a current tag, then remove the current tag
			else {
				// Uses original successMsg
				successHoverText += currentTag.toChatString(0, true, s -> "&c&m"+Chat.replace(s[0], "&c&m")+"&c: &8&m"+Chat.replace(s[1], "&8&m")+"&r");
				
				// Removing the tag from effect
				effect.setTag(null);
			}
		} 
		// If there IS a default tag the current tag and default do not match, then we are actually removing AND reverting to the default tag rather than just removing
		else if(!currentTag.equals(defaultTag)) {
			successMsg = "&aReverted tag for &7"+item.getName()+"&a's instance of &7"+effect.getName()+"&a to original effects tag. Hover to view the details!";
			successHoverText += currentTag.toChatString(0, true, s -> "&c&m"+Chat.replace(s[0], "&c&m")+"&c: &8&m"+Chat.replace(s[1], "&8&m")+"&r")
							  + "\n"
							  + defaultTag.toChatString(0, true, s -> Chat.add("&2"+s[0]+"&2: &7"+s[1], ChatColor.BOLD));
			
			// Setting tag to default tag
			effect.setTag(defaultTag);
		}
		// If there IS a default tag and current/default tags are the same, then we cannot remove the tag from here. Must be done in standard effect edit command
		else {
			Messenger.send(sender, StringsUtil.hoverText("&7"+item.getName()+"&e's instance of &7"+effect.getName()+"&e is using the original effects tag. You must edit the original effect to remove it. Hover to view the tag!"
					, successHoverText + currentTag.toChatString(0, true)));
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
