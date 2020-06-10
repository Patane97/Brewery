package com.Patane.Brewery.Commands.secondary.edit.effect;

import java.util.Arrays;
import java.util.List;

import org.bukkit.command.CommandSender;

import com.Patane.Brewery.CustomEffects.BrEffect;
import com.Patane.Brewery.CustomEffects.BrEffect.BrSoundEffect;
import com.Patane.Commands.CommandInfo;
import com.Patane.util.general.Chat;
import com.Patane.util.general.Messenger;
import com.Patane.util.general.StringsUtil;

import net.md_5.bungee.api.chat.TextComponent;
@CommandInfo(
	name = "edit effect remove sounds",
	description = "Removes the Sound Effects of an original Effect.",
	usage = "/brewery edit effect <effect name> remove sounds"
)
public class editEffectRemoveSounds extends editEffectRemove {

	@Override
	public boolean execute(CommandSender sender, String[] args, Object... objects) {
		
		// Find Effect
		BrEffect effect = (BrEffect) objects[0];
		
		// Default message assumes there is no previous sounds, thus 'set' message is given
		String successMsg = String.format("&aRemoved sounds for &7%s&a. Hover to view the details!", effect.getName());
		
		String successHoverText = generateEditingTitle(effect);

		BrSoundEffect currentSounds = effect.getSoundEffect();
		
		// If both default and current sounds are ALREADY removed, do nothing and message appropriately
		if(currentSounds == null) {
			Messenger.send(sender, StringsUtil.hoverText(String.format("&7%s&e already has no sound effects. Hover to view the effect!", effect.getName())
					, effect.toChatString(0, false)));
			return true;
		}
		// Uses original successMsg
		successHoverText += currentSounds.toChatString(0, true, s -> "&c"+Chat.replace(s[0], "&c")+"&c: &8&m"+Chat.replace(s[1], "&8&m")+"&r");
		
		// Removing the sounds from effect
		effect.setSoundEffect(null);

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
		return Arrays.asList();
	}
}
