package com.Patane.Brewery.Commands.secondary.edit.item.effects;

import java.util.Arrays;
import java.util.List;

import org.bukkit.command.CommandSender;

import com.Patane.Brewery.Brewery;
import com.Patane.Brewery.Commands.secondary.edit.item.editItemEffects;
import com.Patane.Brewery.CustomEffects.BrEffect;
import com.Patane.Brewery.CustomItems.BrItem;
import com.Patane.Commands.CommandInfo;
import com.Patane.util.general.Chat;
import com.Patane.util.general.Messenger;
import com.Patane.util.general.StringsUtil;
import com.Patane.util.ingame.Commands;

import net.md_5.bungee.api.chat.TextComponent;

@CommandInfo(
	name = "edit item effects remove",
	aliases = {"rem", "delete", "del"},
	description = "Removes an Effect from a Brewery Item. This will not remove the effect from the plugin, only from the Item.",
	usage = "/brewery edit item <item name> effects remove <effect name>",
	maxArgs = 1
)
public class editItemEffectsRemove extends editItemEffects {
	
	@Override
	public boolean execute(CommandSender sender, String[] args, Object... objects) {

		// Checks for effect name
		if(args.length < 1) {
			Messenger.send(sender, "&ePlease provide an effect name.");
			return true;
		}
		
		// Save effect name
		String effectName = Commands.combineArgs(args);
		
		// Check if effect exists
		if(!Brewery.getEffectCollection().hasItem(effectName)) {
			Messenger.send(sender, "&cThere is no effect with the name &7"+effectName+"&c.");
			return true;
		}
		// Grabbing the brItem
		BrItem item = (BrItem) objects[0];
		
		// If the item does not contain the effect, do nothing and message appropriately
		if(!item.hasEffect(effectName)) {
			Messenger.send(sender, StringsUtil.hoverText("&7"+item.getName()+"&a does not have this Effect. Hover to view which ones it does has!"
					, StringsUtil.manyToChatString(0, 2, false, null, null, item.getEffects().toArray(new BrEffect[0]))));
			return true;
		}
		// Grabbing effect for later use
		BrEffect effect = item.getEffect(effectName);
		
		// Remove the effect from brItem
		item.removeEffect(effectName);
		
		String successMsg = "&aEffect removed from &7"+item.getName()+"&a. Hover to view details!";
		
		// Success hover text is all effects remaining on item PLUS the removed effect with 'slashed out' formatting
		String successHoverText = StringsUtil.manyToChatString(0, 2, false, null, null, item.getEffects().toArray(new BrEffect[0]));
		
		// Adding the removed effect.
		// If there are effects, add them and new lines dividers
		// If there arent effects, ignore the 'Nothing to see here!' from successHoverText and only show the effect being removed
		// Follows by adding removed effect with slash through the title, type AND value. the Chat. 
		// Chat.replace is there to ensure all formatting is slashed and appropriate colour
		successHoverText = (item.hasEffects() ? successHoverText+"\n\n" : "")
				+ "&c&l- &8&l&m" + effect.toChatString(0, false, s -> "&c"+Chat.replace(s[0], "&c")+"&c: &8"+Chat.replace(s[1], "&8"));
		
		// Save to YML
		BrItem.YML().save(item);
		
		// Allows the user to view the details on hover
		TextComponent successMsgComponent = StringsUtil.hoverText(successMsg, successHoverText);
		
		Messenger.send(sender, successMsgComponent);
		return true;
	}
		
	@Override
	public List<String> tabComplete(CommandSender sender, String[] args, Object... objects) {
		// Grabbing the Item
		BrItem item = (BrItem) objects[0];
		
		switch(args.length) {
			case 1: return StringsUtil.encase(StringsUtil.getCollectableNames(item.getEffects()), "'", "'");
		}
		return Arrays.asList();
	}
}
