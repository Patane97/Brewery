package com.Patane.Brewery.commands.secondary.editing;

import java.util.ArrayList;
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
import com.Patane.util.collections.PatCollectable;
import com.Patane.util.general.Messenger;
import com.Patane.util.general.StringsUtil;
@CommandInfo(
	name = "edit effects edit <effectname> set filter [target|ignore] remove",
	aliases = {"rem","delete", "del"},
	description = "Removes a filter from a Filter Type for an Effect that is attached to a Brewery Item.",
	usage = "/brewery edit effects edit <effect name> set filter [target|ignore] remove [type] <value>",
	maxArgs = 2
)
public class itemEditEffectsEditSetFilterRemove extends itemEditEffectsEditSetFilter {

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
			Messenger.send(sender, "&cPlease provide a filter value to remove.");
			return false;
		}
		
		BrEffect brEffect = (BrEffect) objects[0];

		Filter filter = brEffect.getFilter();
		
		try {
			brEffect.getFilter().remove(filterGroup, filterType, args[1]);
		} catch (IllegalArgumentException e) {
			Messenger.send(sender, e.getMessage());
			return true;
		}
		
		String successMsg = "";
		
		switch(filterType) {
			case "entities":
				successMsg = "&aRemoved &7"+args[1].toUpperCase()+"&a Entity Type from the effects &7"+filterGroup+"&a filter list.";
				break;
			case "players":
				successMsg = "&aRemoved Player &7"+args[1]+"&a from the effects &7"+filterGroup+"&a filter list.";
				break;
			case "permissions":
				successMsg = "&aRemoved &7"+args[1]+"&a Permission from the effects &7"+filterGroup+"&a filter list.";
				break;
			case "tags":
				successMsg = "&aRemoved &7"+args[1]+"&a Tag from the effects &7"+filterGroup+"&a filter list.";
				break;
		}		

		brEffect.setFilter(filter);
		
		BrItem brItem = (BrItem) EditSession.get(sender.getName());
		
		BrItem.YML().save(brItem);
		
		Messenger.send(sender, successMsg);
		return true;
	}
	
	// *** This needs to be more efficient! Possibly saving some of these values within FilterGroups? << They could update each time a method is run to add/remove elements to each filter type.
	// *** Additionally, could rework the entire tab complete system to be similar to execute where it run's code for the command then, if satisfied, moves onto the child 'tabComplete' and so on...
	@Override
	public List<String> tabComplete(CommandSender sender, String[] args, CommandPackage thisPackage) {
		PatCollectable collectable = EditSession.get(sender.getName());
		if(collectable == null || !(collectable instanceof BrItem))
			return Arrays.asList();
		BrEffect brEffect = ((BrItem) collectable).getEffect(args[3]);
		if(brEffect == null)
			return Arrays.asList();
		FilterGroup filterGroup = null;
		if(args[6].toLowerCase().equals("target"))
			filterGroup = brEffect.getFilter().getTarget();
		else if(args[6].toLowerCase().equals("ignore"))
			filterGroup = brEffect.getFilter().getIgnore();
		
		switch(args.length) {
		case 9:
			List<String> nonEmptyFilters = new ArrayList<String>();
			if(!filterGroup.getEntities().isEmpty())
				nonEmptyFilters.add("entities");
			if(!filterGroup.getPlayers().isEmpty())
				nonEmptyFilters.add("players");
			if(!filterGroup.getPermissions().isEmpty())
				nonEmptyFilters.add("permissions");
			if(!filterGroup.getTags().isEmpty())
				nonEmptyFilters.add("tags");
			
			if(nonEmptyFilters.isEmpty())
				nonEmptyFilters.add("&cFilters empty");
			return nonEmptyFilters;
		default:
			switch(args[8].toLowerCase()) {
			case "entities":
				return Arrays.asList(StringsUtil.enumValueStrings(filterGroup.getEntities().toArray(new EntityType[0])));
			case "players":
				return filterGroup.getPlayers();
			case "permissions":
				return filterGroup.getPermissions();
			case "tags":
				return filterGroup.getTags();
			default:
				return Arrays.asList();
			}
		}
	}
}
