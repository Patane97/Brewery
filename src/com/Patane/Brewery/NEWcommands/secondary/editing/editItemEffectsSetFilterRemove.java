package com.Patane.Brewery.NEWcommands.secondary.editing;

import java.util.Arrays;
import java.util.List;

import org.bukkit.command.CommandSender;

import com.Patane.Brewery.CustomEffects.BrEffect;
import com.Patane.Brewery.CustomEffects.Filter;
import com.Patane.Brewery.CustomEffects.Filter.FilterGroups;
import com.Patane.Brewery.CustomEffects.Filter.FilterTypes;
import com.Patane.Brewery.CustomItems.BrItem;
import com.Patane.Commands.CommandInfo;
import com.Patane.util.general.Chat;
import com.Patane.util.general.Messenger;
import com.Patane.util.general.StringsUtil;

import net.md_5.bungee.api.chat.TextComponent;
@CommandInfo(
	name = "edit item effects set filter remove",
	aliases = {"rem","delete", "del"},
	description = "Removes a value from the Filter of an Effect for a Brewery Item. These changes are seperate from the original Effect.",
	usage = "/brewery edit item <item name> effects set <effect name> filter [target|ignore] remove [group] <value>",
	maxArgs = 2
)
public class editItemEffectsSetFilterRemove extends editItemEffectsSetFilter {

	@Override
	public boolean execute(CommandSender sender, String[] args, Object... objects) {
		
		// Checking filter group is given
		if(args.length < 1) {
			Messenger.send(sender, "&ePlease specify a filter group.");
			return true;
		}
		FilterGroups filterGroup = null;
		
		// Find/Save filter group
		try {
			filterGroup = StringsUtil.constructEnum(args[0], FilterGroups.class);
		} catch (IllegalArgumentException e) {
			Messenger.send(sender, "&7"+args[0]+" &cis not a valid filter group.");
			return true;
		}
		
		// Checking if value is given
		if(args.length < 2) {
			Messenger.send(sender, "&ePlease specify a value to remove from this filter group.");
			return true;
		}
		
		// Grabbing the value
		String value = args[1];

		// Grabbing the item
		BrItem item = (BrItem) objects[0];
		
		// Grabbing the effect
		BrEffect effect = (BrEffect) objects[1];
		
		// Checking if effect even has a filter
		if(!effect.hasFilter()) {
			Messenger.send(sender, "&eThere is no filter to edit for &7"+item.getName()+"&e's instance of &7"+effect.getName()+"&e.");
			return true;
		}
		
		// Grabbing the filterTypeString
		FilterTypes filterType = (FilterTypes) objects[2];
		
		// Grabbing the Filter
		Filter filter = effect.getFilter();
		
		String successMsg = "&aUpdated the Filter for &7"+item.getName()+"&a's instance of &7"+effect.getName()+"&a. Hover to view the details!";
		// This is a little more complicated than it needs to be, however it ensures the hover text doesnt get too long horizontally
		// It starts with item name limited at 15 characters (will add '...' if it gets too long)
		// It then either adds an arrow to a new line if both item and effect combined exceed 25 characters, or same line if under
		// Finally it prints the effect name limited at 15 characters
		String successHoverText = "&f&l"
								+ item.getNameLimited(15)
								+ ((item.getNameLimited(15)+effect.getNameLimited(15)).length() > 25 ? "\n" : "") + " &7&l\u2192 &f&l"
								+ effect.getNameLimited(15)+"\n";
		
		// If filter doesnt contain that value in that type+group, do nothing and send appropriate message
		if(!filter.contains(filterType, filterGroup, value)) {
			Messenger.send(sender, StringsUtil.hoverText("&eFilter for &7"+item.getName()+"&e's instance of &7"+effect.getName()+"&e does not contain that value. Hover to view the filter!"
														, successHoverText += filter.toChatString(0, true)));
			return true;
		}	
		// Only returns false if EntityType cannot be found
		if(!filter.remove(filterType, filterGroup, value)) {
			Messenger.send(sender, "&7"+value+" &cis not a valid Entity Type");
			return true;
		}

		// Add filter + removed value to hover text
		successHoverText += filter.toChatStringInsert(0, filterType, filterGroup, value, null, s -> "&c&l - "+Chat.replace(s[0], "&8&m")+" &8&m"+s[1]+"&r");
			
		// Save the new filter to the effect
		effect.setFilter(filter);
		
		// Save the Item to the YML. This will also save the instance of the effect to the item
		BrItem.YML().save(item);

		// Allows the user to view the details on hover
		TextComponent successMsgComponent = StringsUtil.hoverText(successMsg, successHoverText);
		
		Messenger.send(sender, successMsgComponent);
		return true;
		
	}
	
	@Override
	public List<String> tabComplete(CommandSender sender, String[] args, Object... objects) {
		// Grabbing effect
		BrEffect effect = (BrEffect) objects[1];
		if(effect == null) 
			return Arrays.asList();
		
		// Grabbing filterType
		FilterTypes filterType = (FilterTypes) objects[2];
		if(filterType == null) 
			return Arrays.asList();
		
		
		switch(args.length) {
			case 1:
				// Gets all non-empty filter groups as strings
				return StringsUtil.enumValueStrings(effect.getFilter().getType(filterType).getNonEmptyGroups());
			case 2:
				// Gets checks/saves filter group
				FilterGroups filterGroup = null;
				
				// Find/Save filter group
				try {
					filterGroup = StringsUtil.constructEnum(args[0], FilterGroups.class);
				} catch (IllegalArgumentException e) {
					return Arrays.asList();
				}
				// Gets all present values as strings
				return effect.getFilter().getType(filterType).getAsString(filterGroup);
		}
		return Arrays.asList();
	}
}
