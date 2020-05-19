package com.Patane.Brewery.NEWcommands.secondary.editing;

import java.util.Arrays;
import java.util.List;

import org.bukkit.command.CommandSender;

import com.Patane.Brewery.Brewery;
import com.Patane.Brewery.CustomEffects.BrEffect;
import com.Patane.Brewery.CustomEffects.modifiers.None;
import com.Patane.Brewery.CustomEffects.triggers.Instant;
import com.Patane.Brewery.CustomItems.BrItem;
import com.Patane.Commands.CommandInfo;
import com.Patane.util.general.Chat;
import com.Patane.util.general.Messenger;
import com.Patane.util.general.StringsUtil;
import com.Patane.util.general.StringsUtil.LambdaStrings;
import com.Patane.util.ingame.Commands;

import net.md_5.bungee.api.chat.TextComponent;

@CommandInfo(
	name = "edit item effects add",
	description = "Adds and/or Creates an Effect for a Brewery Item.",
	usage = "/brewery edit effects add <effect name>",
	maxArgs = 1
)
public class editItemEffectsAdd extends editItemEffects {
	
	@Override
	public boolean execute(CommandSender sender, String[] args, Object... objects) {

		// Checks for effect name
		if(args.length < 1) {
			Messenger.send(sender, "&ePlease provide an effect name.");
			return true;
		}
		
		
		// Save effect name
		String effectName = Commands.combineArgs(args);
		
		// Grabbing the brItem
		BrItem brItem = (BrItem) objects[0];
		
		// Storing these as its used multiple times and looks messy :3
		LambdaStrings title = s -> "&f&l"+s[0];
		LambdaStrings layout = s -> "&2"+s[0]+": &7"+s[1];
		
		// If effect is already on this item, do nothing and send appropriate message
		if(brItem.hasEffect(effectName)) {
			Messenger.send(sender, StringsUtil.hoverText("&eThis Effect is already present on &7"+brItem.getName()+"&a. Hover to view all effects for this item!"
					, BrEffect.manyToChatString(title, layout, false, brItem.getEffects().toArray(new BrEffect[0]))));
			return true;
		}
		String successMsg = "&aEffect added to &7"+brItem.getName()+"&a. Hover to view details!";
		String successHoverText = BrEffect.manyToChatString(title, layout, false, brItem.getEffects().toArray(new BrEffect[0]));
		
		BrEffect brEffect = null;
		
		// If the effect doesnt already exist
		if(!Brewery.getEffectCollection().hasItem(effectName)){			
			// Create a new empty one and save to brEffect and collection
			brEffect = new BrEffect(effectName, new None(), new Instant(), null, null, null, null, null, null, null);
			Brewery.getEffectCollection().add(brEffect);
			// *** Created and added incomplete effect to item. Hover to view effect details

			successMsg = "&aEffect created and added to &7"+brItem.getName()+"&a. Hover to view details!";
		}
		else {
			brEffect = Brewery.getEffectCollection().getItem(effectName);
		}
		
		successHoverText = (brItem.hasEffects() ? successHoverText+"\n\n" : "") 
				+ brEffect.toChatString(s -> "&a&l+ &f&l"+s[0], s -> "&2&l"+Chat.add(s[0], "&l")+"&2&l: &7&l"+Chat.add(s[1], "&l"), false);
		
		// Add effect to brItem
		brItem.addEffect(brEffect);
		
		// Save to YML
		BrItem.YML().save(brItem);
		
		// Allows the user to view the details on hover
		TextComponent successMsgComponent = StringsUtil.hoverText(successMsg, successHoverText);
		
		Messenger.send(sender, successMsgComponent);
		return true;
	}
		
	@Override
	public List<String> tabComplete(CommandSender sender, String[] args, Object... objects) {
		switch(args.length) {
			case 1: 
				List<String> effects = Brewery.getEffectCollection().getAllIDs();
				effects.add("<new effect>");
				return effects;
		}
		return Arrays.asList();
	}
}
