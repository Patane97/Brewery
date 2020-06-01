package com.Patane.Brewery.NEWcommands.secondary.editing;

import java.util.Arrays;
import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;

import com.Patane.Brewery.CustomEffects.BrEffect;
import com.Patane.Brewery.CustomEffects.Filter;
import com.Patane.Brewery.CustomEffects.Filter.FilterGroups;
import com.Patane.Brewery.CustomEffects.Filter.FilterTypes;
import com.Patane.Brewery.CustomItems.BrItem;
import com.Patane.Commands.CommandInfo;
import com.Patane.util.general.Messenger;
import com.Patane.util.general.StringsUtil;

import net.md_5.bungee.api.chat.TextComponent;
@CommandInfo(
	name = "edit item effects modify set filter add",
	description = "Adds a value to the Filter of an Effect for a Brewery Item. These changes are seperate from the original Effect.",
	usage = "/brewery edit item <item name> effects modify <effect name> set filter add [target|ignore] [group] <value>",
	maxArgs = 3
)
public class editItemEffectsModifySetFilterAdd extends editItemEffectsModifySetFilter {

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
		
		// Checking filter group is given
		if(args.length < 2) {
			Messenger.send(sender, "&ePlease specify a filter group.");
			return true;
		}
		FilterGroups filterGroup = null;
		
		// Find/Save filter group
		try {
			filterGroup = StringsUtil.constructEnum(args[1], FilterGroups.class);
		} catch (IllegalArgumentException e) {
			Messenger.send(sender, "&7"+args[1]+" &cis not a valid filter group.");
			return true;
		}
		
		// Checking if value is given
		if(args.length < 3) {
			Messenger.send(sender, "&ePlease specify a value to add to this filter group.");
			return true;
		}
		
		// Grabbing the value
		String value = args[2];

		// Grabbing the item
		BrItem item = (BrItem) objects[0];
		
		// Grabbing the effect
		BrEffect effect = (BrEffect) objects[1];
		
		// Grabbing the Filter
		Filter filter = effect.getFilter();
		
		String successMsg = "&aUpdated the Filter for &7"+item.getName()+"&a's instance of &7"+effect.getName()+"&a. Hover to view the details!";
		
		String successHoverText = generateEditingTitle(item, effect);
		
		// If filter already contains that value in that type+group, do nothing and send appropriate message
		if(filter.contains(filterType, filterGroup, value)) {
			Messenger.send(sender, StringsUtil.hoverText("&eFilter for &7"+item.getName()+"&e's instance of &7"+effect.getName()+"&e already has that value. Hover to view the filter!"
														, successHoverText += filter.toChatString(0, true)));
			return true;
		}
		
		// Add filter + added value to hover text
		successHoverText += filter.toChatStringInsert(0, filterType, filterGroup, value, null, s -> "&a&l + "+s[0]+" &f&l"+s[1]);
				
		// Only returns false if EntityType cannot be found
		if(!filter.add(filterType, filterGroup, value)) {
			Messenger.send(sender, "&7"+value+" &cis not a valid Entity Type");
			return true;
		}
		
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
		
		
		switch(args.length) {
			case 1: return Arrays.asList(StringsUtil.enumValueStrings(FilterTypes.class));
			case 2: return Arrays.asList(StringsUtil.enumValueStrings(FilterGroups.class));
			case 3: 
				FilterGroups filterGroup = null;
				
				// Find/Save filter group
				try {
					filterGroup = StringsUtil.constructEnum(args[1], FilterGroups.class);
				} catch (IllegalArgumentException e) {
					return Arrays.asList();
				}
				if(filterGroup == FilterGroups.ENTITIES)
					return Arrays.asList(StringsUtil.enumValueStrings(EntityType.class));
				return Arrays.asList("<value>");
		}
		return Arrays.asList();
	}
}
