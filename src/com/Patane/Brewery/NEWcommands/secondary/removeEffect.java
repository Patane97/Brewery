package com.Patane.Brewery.NEWcommands.secondary;

import java.util.Arrays;
import java.util.List;

import org.bukkit.command.CommandSender;

import com.Patane.Brewery.Brewery;
import com.Patane.Brewery.CustomEffects.BrEffect;
import com.Patane.Brewery.NEWcommands.primary.removeCommand;
import com.Patane.Commands.CommandInfo;
import com.Patane.util.general.Messenger;
import com.Patane.util.general.StringsUtil;
import com.Patane.util.ingame.Commands;

import net.md_5.bungee.api.chat.TextComponent;
@CommandInfo(
	name = "remove effect",
	description = "Removes an effect from Brewery.",
	usage = "/brewery remove effect <effect name>",
	maxArgs = 1
)
public class removeEffect extends removeCommand {

	@Override
	public boolean execute(CommandSender sender, String[] args, Object... objects) {
		
		// Checking effect name is given
		if(args.length < 1) {
			Messenger.send(sender, "&cPlease specify an effect name.");
			return true;
		}
		
		// Setting name
		String effectName = Commands.combineArgs(args);
		
		BrEffect effect = null;
		
		// If no effect with that name exists, do nothing and message appropriately
		if(!Brewery.getEffectCollection().hasItem(effectName)) {
			Messenger.send(sender, StringsUtil.hoverText("&eThere is no Brewery Effect named &7"+effectName+"&e. Hover to view all Effects!"
														, StringsUtil.stringJoiner(Brewery.getEffectCollection().getAllIDs(), "\n&e> &f&l", "&e> &f&l", "")));
			return true;
		}

		String successMsg = "&aRemoved an existing Effect. Hover to view the details!";
		String successHoverText = null;
		
		try {
			// Grabbing the effect
			effect = Brewery.getEffectCollection().getItem(effectName);
			
			// Attempt to clear the effect from YML. If this gives us exceptions then we dont remove the effect from the collection
			BrEffect.YML().clearSection(effect.getName());
			
			// Remove effect from collection
			Brewery.getEffectCollection().remove(effect.getName());
			
			// Save removed effect onto hover text with removed layout/title layouts
			successHoverText = effect.toChatString(0, false);
		
		} catch (Exception e) {
			// Save the error message onto successMsg (oh the irony)
			successMsg = "&cThere was an error with removing this effect. Hover for error details!";
			// Save the exception message on hover. Dat shi ugly
			successHoverText = "&7"+e.getMessage();
			e.printStackTrace();
		}
		// Allows the user to view the details on hover
		TextComponent successMsgComponent = StringsUtil.hoverText(successMsg, successHoverText);
		
		// Send the hover message to sender
		Messenger.send(sender, successMsgComponent);
		return true;
	}

	@Override
	public List<String> tabComplete(CommandSender sender, String[] args, Object... objects) {
		switch(args.length) {
			case 1: return StringsUtil.encase(Brewery.getEffectCollection().getAllIDs(), "'", "'");
		}
		return Arrays.asList();
	}
}
