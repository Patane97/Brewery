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
	name = "edit effect remove radius",
	description = "Removes the Radius of an original Effect.",
	usage = "/brewery edit effect <effect name> remove radius"
)
public class editEffectRemoveRadius extends editEffectRemove {

	@Override
	public boolean execute(CommandSender sender, String[] args, Object... objects) {
		
		// Find Effect
		BrEffect effect = (BrEffect) objects[0];
		
		// Default message assumes there is no previous radius, thus 'set' message is given
		String successMsg = String.format("&aRemoved radius for &7%s&a. Hover to view the details!", effect.getName());
		
		String successHoverText = generateEditingTitle(effect);

		Float currentRadius = effect.getRadius();
		
		// If both default and current radius are ALREADY removed, do nothing and message appropriately
		if(currentRadius == null) {
			Messenger.send(sender, StringsUtil.hoverText(String.format("&7%s&e already has no radius. Hover to view the effect!", effect.getName())
					, effect.toChatString(0, false)));
			return true;
		}
		// Uses original successMsg
		successHoverText += "&cRadius&c: &8&m"+currentRadius+"&r";
		
		// Removing the radius from effect
		effect.setRadius(null);

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
