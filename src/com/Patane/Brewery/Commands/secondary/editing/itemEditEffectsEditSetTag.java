package com.Patane.Brewery.Commands.secondary.editing;

import java.util.Arrays;
import java.util.List;

import org.bukkit.command.CommandSender;

import com.Patane.Brewery.CustomEffects.BrEffect;
import com.Patane.Brewery.CustomItems.BrItem;
import com.Patane.Brewery.Editing.EditSession;
import com.Patane.Commands.CommandHandler.CommandPackage;
import com.Patane.Commands.CommandInfo;
import com.Patane.util.general.Messenger;
import com.Patane.util.ingame.Commands;
@CommandInfo(
	name = "edit effects edit <effectname> set tag",
	description = "Sets the Tag for an Effect that is attached to a Brewery Item.",
	usage = "/brewery edit effects edit <effect name> set tag <name>",
	maxArgs = 1
)
public class itemEditEffectsEditSetTag extends itemEditEffectsEditSet {

	@Override
	public boolean execute(CommandSender sender, String[] args, Object... objects) {
		
		if(args.length < 1) {
			Messenger.send(sender, "&cPlease provide a tag name.");
			return false;
		}
		String tag = Commands.combineArgs(args);

		BrEffect brEffect = (BrEffect) objects[0];
		
		brEffect.setTag(tag);
		
		BrItem brItem = (BrItem) EditSession.get(sender.getName());
		
		BrItem.YML().save(brItem);
		
		Messenger.send(sender, "&aSet Tag to &7"+tag+"&a.");
		return true;
	}
	
	@Override
	public List<String> tabComplete(CommandSender sender, String[] args, CommandPackage thisPackage) {
		return Arrays.asList("<name>");
	}
}
