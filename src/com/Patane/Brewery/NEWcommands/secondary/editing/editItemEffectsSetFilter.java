package com.Patane.Brewery.NEWcommands.secondary.editing;

import java.util.Arrays;
import java.util.List;

import org.bukkit.command.CommandSender;

import com.Patane.Brewery.CustomEffects.Filter.FilterTypes;
import com.Patane.Commands.CommandInfo;
import com.Patane.util.general.Messenger;
import com.Patane.util.general.StringsUtil;
@CommandInfo(
	name = "edit item effects set filter",
	description = "Edits the Filter of an Effect for a Brewery Item. These changes are seperate from the original Effect.",
	usage = "/brewery edit item <item name> effects set <effect name> filter [target|ignore] [add|remove|clear]",
	maxArgs = 1
)
public class editItemEffectsSetFilter extends editItemEffectsSet {

	@Override
	public boolean execute(CommandSender sender, String[] args, Object... objects) {
		
		// Checking filter is given
		if(args.length < 1) {
			Messenger.send(sender, "&ePlease specify a filter type.");
			return true;
		}
		FilterTypes filterType = null;
		
		// Find/Save filter type
		try {
			filterType = StringsUtil.constructEnum(args[0], FilterTypes.class);
		} catch (IllegalArgumentException e) {
			Messenger.send(sender, "&7"+args[0]+" &cis not a valid filter type.");
			return true;
		}
		
		return this.gotoChild(1, sender, args, objects[0], objects[1], filterType);
	}
	
	@Override
	public List<String> tabComplete(CommandSender sender, String[] args, Object... objects) {
		switch(args.length) {
			case 1: return Arrays.asList(StringsUtil.enumValueStrings(FilterTypes.class));
		}
		
		FilterTypes filterType = null;
		
		// Find/Save filter type
		try {
			filterType = StringsUtil.constructEnum(args[0], FilterTypes.class);
		} catch (IllegalArgumentException e) {
			return Arrays.asList();
		}
		
		return tabCompleteCore(sender, args, objects[0], objects[1], filterType);
		
	}
}
