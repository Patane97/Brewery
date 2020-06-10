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
	name = "edit effect set ignoreuser",
	description = "Sets whether the user of this Effect is ignored by it.",
	usage = "/brewery edit effect <effect name> set ignoreuser <true|false>",
	maxArgs = 1
)
public class editEffectSetIgnoreuser extends editEffectSet {

	@Override
	public boolean execute(CommandSender sender, String[] args, Object... objects) {
		
		// Checking true or false is given
		if(args.length < 1) {
			Messenger.send(sender, "&ePlease provide a true or false value.");
			return true;
		}
		boolean ignoreUser = true;
		try {
			ignoreUser = StringsUtil.parseBoolean(args[0]);
		} catch (IllegalArgumentException e) {
			Messenger.send(sender, String.format("&7 &cis not a valid value. This must be either true or false", args[0]));
			return true;
		}
		
		// Grabbing effect
		BrEffect effect = (BrEffect) objects[0];

		String successMsg = String.format("&aChanged the ignore user value for &7%s&a. Hover to view the details!", effect.getName());
		
		String successHoverText = generateEditingTitle(effect);
		
		// Grabbing previous value
		boolean previousIgnoreUser = effect.ignoreUser();
		
		if(ignoreUser == previousIgnoreUser) {
			Messenger.send(sender, StringsUtil.hoverText(String.format("&eIgnore user for &7%s&e is already the given value. Hover to view it!", effect.getName())
					, successHoverText + "&2Ignore User: &7"+previousIgnoreUser));
			return true;
		}
		
		// Shows changing the value
		successHoverText += "&2Ignore User: &8"+previousIgnoreUser+" &7-> "+ignoreUser;
		
		// Sets the ignore user value to effect
		effect.setIgnoreUser(ignoreUser);

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
			case 1: return Arrays.asList("true", "false");
		}
		return Arrays.asList();
	}
}
