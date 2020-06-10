package com.Patane.Brewery.Commands.secondary.edit.item.effects;

import java.util.Arrays;
import java.util.List;

import org.bukkit.command.CommandSender;

import com.Patane.Brewery.CustomEffects.BrEffect;
import com.Patane.Brewery.CustomItems.BrItem;
import com.Patane.Commands.CommandInfo;
import com.Patane.util.general.Messenger;
import com.Patane.util.general.StringsUtil;

import net.md_5.bungee.api.chat.TextComponent;
@CommandInfo(
	name = "edit item effects modify set ignoreuser",
	description = "Sets whether the user of the Brewery item is ignored by this Effect. These changes are seperate from the original Effect.",
	usage = "/brewery edit item <item name> effects modify <effect name> set ignoreuser <true|false>",
	maxArgs = 1
)
public class editItemEffectsModifySetIgnoreuser extends editItemEffectsModifySet {

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
			Messenger.send(sender, "&7"+args[0]+" &cis not a valid value. This must be either true or false");
			return true;
		}
		// Grabbing item
		BrItem item = (BrItem) objects[0];
		
		// Grabbing effect
		BrEffect effect = (BrEffect) objects[1];

		String successMsg = "&aChanged the ignore user value for &7"+item.getName()+"&a's instance of &7"+effect.getName()+"&a. Hover to view the details!";
		
		String successHoverText = generateEditingTitle(item, effect);
		
		// Grabbing previous value
		boolean previousIgnoreUser = effect.ignoreUser();
		
		if(ignoreUser == previousIgnoreUser) {
			Messenger.send(sender, StringsUtil.hoverText("&eIgnore user for &7"+item.getName()+"&e's instance of &7"+effect.getName()+"&e is already the given value. Hover to view it!"
					, successHoverText + "&2Ignore User: &7"+previousIgnoreUser));
			return true;
		}
		
		// Shows changing the value
		successHoverText += "&2Ignore User: &8"+previousIgnoreUser+" &7-> "+ignoreUser;
		
		// Sets the ignore user value to effect
		effect.setIgnoreUser(ignoreUser);

		// Save the Item to the YML. This will also save the instance of the effect to the item
		BrItem.YML().save(item);

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
