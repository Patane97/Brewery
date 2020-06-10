package com.Patane.Brewery.Commands.secondary.edit.item.effects;

import java.util.Arrays;
import java.util.List;

import org.bukkit.command.CommandSender;

import com.Patane.Brewery.Commands.secondary.edit.item.editItemEffects;
import com.Patane.Brewery.CustomEffects.BrEffect;
import com.Patane.Brewery.CustomItems.BrItem;
import com.Patane.Commands.CommandInfo;
import com.Patane.util.general.Messenger;
import com.Patane.util.general.StringsUtil;
@CommandInfo(
	name = "edit item effects modify",
	aliases = {"change", "edit", "set"},
	description = "Modifies or changes the property of an Effect for a Brewery Item. These changes are seperate from the original Effect.",
	usage = "/brewery edit item <item name> effects modify <effect name> [set|remove]",
	maxArgs = 1
)
public class editItemEffectsModify extends editItemEffects {

	@Override
	public boolean execute(CommandSender sender, String[] args, Object... objects) {
		
		//Checking effect name is given
		if(args.length < 1) {
			Messenger.send(sender, "&ePlease specify an effect name.");
			return true;
		}
		
		// Find Item
		BrItem item = (BrItem) objects[0]; 
		
		// Find Effect
		BrEffect effect = item.getEffect(args[0]);
		
		// Check if Effect exists
		if(effect == null) {
			Messenger.send(sender, StringsUtil.hoverText("&7"+item.getName()+"&c has no effects with the name &7"+args[0]+"&c. Hover to view this items effects!"
														, StringsUtil.manyToChatString(0, 2, false, null, null, item.getEffects().toArray(new BrEffect[0]))));
			return true;
		}
		
		return this.gotoChild(1, sender, args, item, effect);
	}
	
	@Override
	public List<String> tabComplete(CommandSender sender, String[] args, Object... objects) {
		BrItem item = (BrItem) objects[0];
		if(item == null) 
			return Arrays.asList();
		
		switch(args.length) {
			case 1: return StringsUtil.encase(StringsUtil.getCollectableNames(item.getEffects()), "'", "'");
		}
		
		// Grabbing the effect
		BrEffect effect = item.getEffect(args[0]);
		
		return this.tabCompleteCore(sender, args, item, effect);
	}
}
