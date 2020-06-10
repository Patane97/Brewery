package com.Patane.Brewery.Commands.secondary.edit.effect;

import java.util.Arrays;
import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;

import com.Patane.Brewery.CustomEffects.BrEffect;
import com.Patane.Brewery.CustomEffects.Filter;
import com.Patane.Brewery.CustomEffects.Filter.FilterGroups;
import com.Patane.Brewery.CustomEffects.Filter.FilterTypes;
import com.Patane.Commands.CommandInfo;
import com.Patane.util.general.Messenger;
import com.Patane.util.general.StringsUtil;

import net.md_5.bungee.api.chat.TextComponent;
@CommandInfo(
	name = "edit effect set filter add",
	description = "Adds a value to the Filter of an original Effect.",
	usage = "/brewery edit effect <effect name> set filter add [target|ignore] [group] <value>",
	maxArgs = 3
)
public class editEffectSetFilterAdd extends editEffectSetFilter {

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
			Messenger.send(sender, String.format("&7%s &cis not a valid filter type.", args[0]));
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
			Messenger.send(sender, String.format("&7%s &cis not a valid filter group.", args[1]));
			return true;
		}
		
		// Checking if value is given
		if(args.length < 3) {
			Messenger.send(sender, "&ePlease specify a value to add to this filter group.");
			return true;
		}
		
		// Grabbing the value
		String value = args[2];
		
		// Grabbing the effect
		BrEffect effect = (BrEffect) objects[0];
		
		// Grabbing the Filter
		Filter filter = effect.getFilter();
		
		String successMsg = String.format("&aUpdated the Filter for &7%s&a. Hover to view the details!", effect.getName());
		
		String successHoverText = generateEditingTitle(effect);
		
		// If filter already contains that value in that type+group, do nothing and send appropriate message
		if(filter.contains(filterType, filterGroup, value)) {
			Messenger.send(sender, StringsUtil.hoverText(String.format("&eFilter for &7%s&e already has that value. Hover to view the filter!", effect.getName())
														, successHoverText += filter.toChatString(0, true)));
			return true;
		}
		
		// Add filter + added value to hover text
		successHoverText += filter.toChatStringInsert(0, filterType, filterGroup, value, null, s -> "&a&l + "+s[0]+" &f&l"+s[1]+"&r");
				
		// Only returns false if EntityType cannot be found
		if(!filter.add(filterType, filterGroup, value)) {
			Messenger.send(sender, String.format("&7%s &cis not a valid Entity Type", value));
			return true;
		}
		
		// Save the new filter to the effect
		effect.setFilter(filter);

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
