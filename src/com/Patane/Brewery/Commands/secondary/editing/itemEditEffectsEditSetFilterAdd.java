package com.Patane.Brewery.Commands.secondary.editing;

import java.util.Arrays;
import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;

import com.Patane.Brewery.CustomEffects.BrEffect;
import com.Patane.Brewery.CustomEffects.Filter;
import com.Patane.Brewery.CustomEffects.Filter.FilterGroup;
import com.Patane.Brewery.CustomItems.BrItem;
import com.Patane.Brewery.Editing.EditSession;
import com.Patane.Commands.CommandHandler.CommandPackage;
import com.Patane.Commands.CommandInfo;
import com.Patane.util.general.Messenger;
import com.Patane.util.general.StringsUtil;
@CommandInfo(
	name = "edit effects edit <effectname> set filter [target|ignore] add",
	description = "Adds to a Filter Type for an Effect that is attached to a Brewery Item.",
	usage = "/brewery edit effects edit <effect name> set filter [target|ignore] add [type] <value>",
	maxArgs = 2
)
public class itemEditEffectsEditSetFilterAdd extends itemEditEffectsEditSetFilter {

	@Override
	public boolean execute(CommandSender sender, String[] args, Object... objects) {
		
		if(args.length < 1) {
			Messenger.send(sender, "&cPlease provide a filter type.");
			return false;
		}
		
		String filterGroup = (String) objects[1];
		
		String filterType = args[0].toLowerCase();
		
		if(!Arrays.asList(FilterGroup.types).contains(filterType)){
			Messenger.send(sender, "&7"+args[0]+" &cis not a valid Filter Type.");
			return true;
		}
		if(args.length < 2) {
			Messenger.send(sender, "&cPlease provide a filter value to add.");
			return false;
		}
		
		BrEffect brEffect = (BrEffect) objects[0];
		
		Filter filter = brEffect.getFilter();
		
		try {
			filter.add(filterGroup, filterType, args[1]);
		} catch (IllegalArgumentException e) {
			Messenger.send(sender, e.getMessage());
			return true;
		}
		
		String successMsg = "";
		
		switch(filterType) {
			case "entities":
				successMsg = "&aAdded &7"+args[1].toUpperCase()+"&a Entity Type to the effects &7"+filterGroup+"&a filter list.";
				break;
			case "players":
				successMsg = "&aAdded Player &7"+args[1]+"&a to the effects &7"+filterGroup+"&a filter list.";
				break;
			case "permissions":
				successMsg = "&aAdded &7"+args[1]+"&a Permission to the effects &7"+filterGroup+"&a filter list.";
				break;
			case "tags":
				successMsg = "&aAdded &7"+args[1]+"&a Tag to the effects &7"+filterGroup+"&a filter list.";
				break;
		}

		brEffect.setFilter(filter);
		
		BrItem brItem = (BrItem) EditSession.get(sender.getName());
		
		BrItem.YML().save(brItem);
		
		Messenger.send(sender, successMsg);
		return true;
	}
	
	@Override
	public List<String> tabComplete(CommandSender sender, String[] args, CommandPackage thisPackage) {
		switch(args.length) {
		case 9:
			return Arrays.asList("entities", "players", "permissions", "tags");
		default:
			if(args[8].equalsIgnoreCase("entities"))
				return Arrays.asList(StringsUtil.enumValueStrings(EntityType.class));
			return Arrays.asList("<value>");
		}
	}
}
