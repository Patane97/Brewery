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
import com.Patane.util.general.StringsUtil;
@CommandInfo(
	name = "edit effects edit <effectname> set ignore-user",
	description = "Sets whether the user is ignored for this Effect that is attached to a Brewery Item.",
	usage = "/brewery edit effects edit <effect name> set ignore-user [true|false]",
	maxArgs = 1
)
public class itemEditEffectsEditSetIgnoreUser extends itemEditEffectsEditSet {

	@Override
	public boolean execute(CommandSender sender, String[] args, Object... objects) {
		
		if(args.length < 1) {
			Messenger.send(sender, "&cPlease provide a true or false value.");
			return false;
		}
		
		Boolean ignore_user = null;
		try {
			ignore_user = StringsUtil.parseBoolean(args[0]);
		} catch (IllegalArgumentException e) {
			Messenger.send(sender, "&7"+args[0]+" &cis an invalid value.");
			return true;
		}
		
		BrEffect brEffect = (BrEffect) objects[0];
		
		brEffect.setIgnoreUser(ignore_user);
		
		BrItem brItem = (BrItem) EditSession.get(sender.getName());
		
		BrItem.YML().save(brItem);
		
		Messenger.send(sender, "&aSet Ignore User to &7"+ignore_user+"&a.");
		return true;
	}
	
	@Override
	public List<String> tabComplete(CommandSender sender, String[] args, CommandPackage thisPackage) {
		return Arrays.asList("true", "false");
	}
}
