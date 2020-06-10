package com.Patane.Brewery.Commands.secondary.edit.item.effects;

import java.util.Arrays;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import com.Patane.Brewery.Brewery;
import com.Patane.Brewery.CustomEffects.BrEffect;
import com.Patane.Brewery.CustomEffects.BrEffect.BrSoundEffect;
import com.Patane.Brewery.CustomItems.BrItem;
import com.Patane.Commands.CommandInfo;
import com.Patane.util.general.Chat;
import com.Patane.util.general.Messenger;
import com.Patane.util.general.StringsUtil;

import net.md_5.bungee.api.chat.TextComponent;
@CommandInfo(
	name = "edit item effects modify remove sounds",
	description = "Removes the Sound Effect of an Effect for a Brewery Item. These changes are seperate from the original Effect.",
	usage = "/brewery edit item <item name> effects modify <effect name> remove sounds"
)
public class editItemEffectsModifyRemoveSounds extends editItemEffectsModifyRemove {

	@Override
	public boolean execute(CommandSender sender, String[] args, Object... objects) {
		
		// Find Item
		BrItem item = (BrItem) objects[0]; 
		
		// Find Effect
		BrEffect effect = (BrEffect) objects[1];
		
		// Default message assumes there is no previous sounds, thus 'set' message is given
		String successMsg = "&aRemoved sounds for &7"+item.getName()+"&a's instance of &7"+effect.getName()+"&a. Hover to view the details!";
		
		String successHoverText = generateEditingTitle(item, effect);

		BrSoundEffect currentSounds = effect.getSoundEffect();
		// Grab the previous sounds for later use
		BrSoundEffect defaultSounds = Brewery.getEffectCollection().getItem(effect.getName()).getSoundEffect();
		
		if(defaultSounds == null) {
			// If both default and current sounds are ALREADY removed, do nothing and message appropriately
			if(currentSounds == null) {
				Messenger.send(sender, StringsUtil.hoverText("&eBoth &7"+item.getName()+"&e's instance of &7"+effect.getName()+"&e and the original effect already have no sound effects. Hover to view the items effect!"
						, generateEditingTitle(item) + effect.toChatString(0, false)));
				return true;
			}
			// If there is no default sounds but there IS a current sounds, then remove the current sounds
			else {
				// Uses original successMsg
				successHoverText += currentSounds.toChatString(0, true, s -> "&c"+Chat.replace(s[0], "&c")+"&c: &8&m"+Chat.replace(s[1], "&8&m")+"&r");
				
				// Removing the sounds from effect
				effect.setSoundEffect(null);
			}
		} 
		// If there IS a default sounds the current sounds and default do not match, then we are actually removing AND reverting to the default sounds rather than just removing
		else if(!currentSounds.equals(defaultSounds)) {
			successMsg = "&aReverted sound effects for &7"+item.getName()+"&a's instance of &7"+effect.getName()+"&a to original effects sounds. Hover to view the details!";
			successHoverText += currentSounds.toChatString(0, true, s -> "&c"+Chat.replace(s[0], "&c")+"&c: &8&m"+Chat.replace(s[1], "&8&m")+"&r")
							  + "\n"
							  + defaultSounds.toChatString(0, true, s -> Chat.add("&2"+s[0]+"&2: &7"+s[1], ChatColor.BOLD));
			
			// Setting sounds to default sounds
			effect.setSoundEffect(defaultSounds);
		}
		// If there IS a default sounds and current/default soundss are the same, then we cannot remove the sounds from here. Must be done in standard effect edit command
		else {
			Messenger.send(sender, StringsUtil.hoverText("&7"+item.getName()+"&e's instance of &7"+effect.getName()+"&e is using the original effects sounds. You must edit the original effect to remove it. Hover to view the sound effects!"
					, successHoverText + currentSounds.toChatString(0, true)));
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
