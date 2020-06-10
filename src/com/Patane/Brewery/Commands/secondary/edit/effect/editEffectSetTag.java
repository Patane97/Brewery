package com.Patane.Brewery.Commands.secondary.edit.effect;

import java.util.Arrays;
import java.util.List;

import org.bukkit.command.CommandSender;

import com.Patane.Brewery.CustomEffects.BrEffect;
import com.Patane.Brewery.CustomEffects.BrEffect.BrTag;
import com.Patane.Commands.CommandInfo;
import com.Patane.util.general.Messenger;
import com.Patane.util.general.StringsUtil;
import com.Patane.util.ingame.Commands;

import net.md_5.bungee.api.chat.TextComponent;
@CommandInfo(
	name = "edit effect set tag",
	description = "Sets the Tag of an original Effect.",
	usage = "/brewery edit effect <effect name> set tag <name>",
	maxArgs = 1
)
public class editEffectSetTag extends editEffectSet {

	@Override
	public boolean execute(CommandSender sender, String[] args, Object... objects) {
		
		// Checking tag name is given
		if(args.length < 1) {
			Messenger.send(sender, "&ePlease provide a tag name.");
			return true;
		}
		
		BrTag tag = new BrTag(Commands.combineArgs(args));
		
		// Grabbing effect
		BrEffect effect = (BrEffect) objects[0];

		String successMsg = String.format("&aAdded new Tag to &7%s&a. Hover to view the details!", effect.getName());
		
		String successHoverText = generateEditingTitle(effect);
			
		BrTag previousTag = effect.getTag();
		
		// If the tags are the same, do nothing and message appropriately
		if(tag.equals(previousTag)) {
			Messenger.send(sender, StringsUtil.hoverText(String.format("&7%s&e already has that Tag. Hover to view it!", effect.getName())
														, successHoverText + effect.getTag().toChatString(0, true)));
			return true;
		}
		
		if(previousTag != null) {
			// If its different, then it is changing
			successMsg = String.format("&aChanged the Tag for &7%s&a. Hover to view the details!", effect.getName());
			successHoverText += StringsUtil.singleRowCompareFormatter(0,
								s -> "&2"+s[0]+"&2: &7"+s[1]
							  , s -> "&2"+s[0]+"&2: &8"+s[1]+" &7-> "+s[2]
							  , tag.className() , previousTag.getName() , tag.getName());
		}
		else
			successHoverText += tag.toChatString(0, true);
		
		// Sets the tag to effect
		effect.setTag(tag);

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
			case 1: return Arrays.asList("<name>");
		}
		return Arrays.asList();
	}
}
