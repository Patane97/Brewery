package com.Patane.Brewery.NEWcommands.secondary.editing;

import java.util.Arrays;
import java.util.List;

import org.bukkit.command.CommandSender;

import com.Patane.Brewery.CustomEffects.BrEffect;
import com.Patane.Brewery.CustomEffects.BrEffect.BrTag;
import com.Patane.Brewery.CustomItems.BrItem;
import com.Patane.Commands.CommandInfo;
import com.Patane.util.general.Messenger;
import com.Patane.util.general.StringsUtil;
import com.Patane.util.ingame.Commands;

import net.md_5.bungee.api.chat.TextComponent;
@CommandInfo(
	name = "edit item effects set tag",
	description = "Sets the Tag of an Effect for a Brewery Item. These changes are seperate from the original Effect.",
	usage = "/brewery edit item <item name> effects set <effect name> tag <name>",
	maxArgs = 1
)
public class editItemEffectsSetTag extends editItemEffectsSet {

	@Override
	public boolean execute(CommandSender sender, String[] args, Object... objects) {
		
		// Checking tag name is given
		if(args.length < 1) {
			Messenger.send(sender, "&ePlease provide a tag name.");
			return true;
		}
		
		BrTag tag = new BrTag(Commands.combineArgs(args));
		
		// Grabbing item
		BrItem item = (BrItem) objects[0];
		
		// Grabbing effect
		BrEffect effect = (BrEffect) objects[1];

		String successMsg = "&aAdded new Tag to &7"+item.getName()+"&a's instance of &7"+effect.getName()+"&a. Hover to view the details!";
		
		String successHoverText = generateEditingTitle(item, effect);
			
		BrTag previousTag = effect.getTag();
		
		// If the tags are the same, do nothing and message appropriately
		if(tag.equals(previousTag)) {
			Messenger.send(sender, StringsUtil.hoverText("&7"+item.getName()+"&e's instance of &7"+effect.getName()+"&e already has that Tag. Hover to view it!"
														, successHoverText + effect.getTag().toChatString(0, true)));
			return true;
		}
		
		if(previousTag != null) {
			// If its different, then it is changing
			successMsg = "&aChanged the Tag for &7"+item.getName()+"&a's instance of &7"+effect.getName()+"&a. Hover to view the details!";
			successHoverText += StringsUtil.singleRowCompareFormatter(0,
								s -> "&2"+s[0]+"&2: &7"+s[1]
							  , s -> "&2"+s[0]+"&2: &8"+s[1]+" &7-> "+s[2]
							  , tag.className() , previousTag.name , tag.name);
		}
		else
			successHoverText += tag.toChatString(0, true);
		
		// Sets the tag to effect
		effect.setTag(tag);

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
			case 1: return Arrays.asList("<name>");
		}
		return Arrays.asList();
	}
}
