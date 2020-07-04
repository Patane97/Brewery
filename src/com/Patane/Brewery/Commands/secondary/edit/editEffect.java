package com.Patane.Brewery.Commands.secondary.edit;

import java.util.Arrays;
import java.util.List;

import org.bukkit.command.CommandSender;

import com.Patane.Brewery.Brewery;
import com.Patane.Brewery.Commands.primary.editCommand;
import com.Patane.Brewery.CustomEffects.BrEffect;
import com.Patane.Commands.CommandInfo;
import com.Patane.util.general.Messenger;
import com.Patane.util.general.StringsUtil;

@CommandInfo(
	name = "edit effect",
	description = "Edits an effect within Brewery.",
	usage = "/brewery edit effect <effect name> [property]",
	permission = "brewery.edit.effect",
	maxArgs = 1
)
public class editEffect extends editCommand {
	
	@Override
	public boolean execute(CommandSender sender, String[] args, Object... objects) {
		
		// Checking effect name is given
		if(args.length < 1) {
			Messenger.send(sender, "&ePlease specify an effect name.");
			return true;
		}
		// Find Effect
		BrEffect effect = Brewery.getEffectCollection().getItem(args[0]);
		
		// Check if Effect exists
		if(effect == null) {
			Messenger.send(sender, "&cThere is no effect with the name &7"+args[0]+"&c.");
			return true;
		}
		
		// Check if next argument/child command is provided
		if(args.length < 2) {
			Messenger.send(sender, "&ePlease specify a property to edit.");
			return true;
		}
		
		return this.gotoChild(1, s -> "&7"+s+" &cis not a valid property to edit.", sender, args, effect);
	}
	
	@Override
	public List<String> tabComplete(CommandSender sender, String[] args, Object... objects) {
		switch(args.length) {
			case 1: return StringsUtil.encase(Brewery.getEffectCollection().getAllIDs(), "'", "'");
		}
		// Grabbing the effect
		BrEffect effect = Brewery.getEffectCollection().getItem(args[0]);
		
		if(effect == null)
			return Arrays.asList();
		
		return tabCompleteCore(sender, args, effect);
	}
	
	protected String generateEditingTitle(BrEffect effect) {
		// This is a little more complicated than it needs to be, however it ensures the hover text doesnt get too long horizontally
		// It starts with item name limited at 15 characters (will add '...' if it gets too long)
		return "&f&l" + effect.getNameLimited(15)+"\n";
	}
}
