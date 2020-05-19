package com.Patane.Brewery.NEWcommands.secondary;

import java.util.Arrays;
import java.util.List;

import org.bukkit.command.CommandSender;

import com.Patane.Brewery.Brewery;
import com.Patane.Brewery.CustomEffects.BrEffect;
import com.Patane.Brewery.CustomEffects.modifiers.None;
import com.Patane.Brewery.CustomEffects.triggers.Instant;
import com.Patane.Brewery.NEWcommands.primary.createCommand;
import com.Patane.Commands.CommandInfo;
import com.Patane.util.general.Messenger;
import com.Patane.util.general.StringsUtil;
import com.Patane.util.general.StringsUtil.LambdaStrings;
import com.Patane.util.ingame.Commands;

import net.md_5.bungee.api.chat.TextComponent;
@CommandInfo(
	name = "create effect",
	description = "Creates a new effect with default values for Brewery.",
	usage = "/brewery create effect <effect name>",
	maxArgs = 1
)
public class createEffect extends createCommand {

	@Override
	public boolean execute(CommandSender sender, String[] args, Object... objects) {
		
		// Checking effect name is given
		if(args.length < 1) {
			Messenger.send(sender, "&cPlease specify a name for the effect.");
			return true;
		}
		
		// Setting name
		String effectName = Commands.combineArgs(args);

		// Storing these as its used multiple times and looks messy :3
		LambdaStrings title = s -> "&f&l"+s[0];
		LambdaStrings layout = s -> "&2"+s[0]+": &7"+s[1];
		
		BrEffect effect = null;
		
		// If an effect with that name already exists, do nothing and message appropriately
		if(Brewery.getEffectCollection().hasItem(effectName)) {
			effect = Brewery.getEffectCollection().getItem(effectName);
			Messenger.send(sender, StringsUtil.hoverText("&eThere is already a Brewery Effect named &7"+effectName+"&e. Hover to view its details!"
														, effect.toChatString(title, layout, false)));
			return true;
		}
		String successMsg = "&aCreated a new Effect. Hover to view its details!";
		String successHoverText = null;

		try {
			// Creating the new effect
			effect = new BrEffect(effectName, new None(), new Instant(), null, null, null, null, null, null, null);
			
			// Attempt to save the effect to YML. If this gives us exceptions then we dont add the effect to the collection
			BrEffect.YML().save(effect);
			
			// Add effect to collection
			Brewery.getEffectCollection().add(effect);
			
			// Save new effect onto hover text
			successHoverText = BrEffect.manyToChatString(title, layout, false, effect);
			
		} catch (Exception e) {
			// Save the error message onto successMsg (oh the irony)
			successMsg = "&cThere was an error with creating this effect. Hover for error details!";
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
			case 1: return Arrays.asList("<effect name>");
		}
		return Arrays.asList();
	}
}
