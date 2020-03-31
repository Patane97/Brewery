package com.Patane.Brewery.commands.secondary.editing;

import java.util.Arrays;
import java.util.List;

import org.bukkit.command.CommandSender;

import com.Patane.Brewery.CustomEffects.BrEffect;
import com.Patane.Brewery.CustomItems.BrItem;
import com.Patane.Brewery.Editing.EditSession;
import com.Patane.Commands.CommandHandler.CommandPackage;
import com.Patane.Commands.CommandInfo;
import com.Patane.util.general.Messenger;
@CommandInfo(
	name = "edit effects edit <effectname> set radius",
	description = "Sets the Radius for an Effect that is attached to a Brewery Item.",
	usage = "/brewery edit effects edit <effect name> set radius <amount>",
	maxArgs = 1
)
public class itemEditEffectsEditSetRadius extends itemEditEffectsEditSet {

	@Override
	public boolean execute(CommandSender sender, String[] args, Object... objects) {
		
		if(args.length < 1) {
			Messenger.send(sender, "&cPlease provide an amount for the radius.");
			return false;
		}
		Float amount = null;
		try {
			amount = Float.parseFloat(args[0]);
		} catch (NumberFormatException e) {
			Messenger.send(sender, "&7"+args[0]+" &cis an invalid amount.");
			return true;
		}

		BrEffect brEffect = (BrEffect) objects[0];
		
		brEffect.setRadius(amount);
		
		BrItem brItem = (BrItem) EditSession.get(sender.getName());
		
		BrItem.YML().save(brItem);
		
		Messenger.send(sender, "&aSet Radius to &7"+amount+"&a.");
		return true;
	}
	
	@Override
	public List<String> tabComplete(CommandSender sender, String[] args, CommandPackage thisPackage) {
		return Arrays.asList("<amount>");
	}
}
