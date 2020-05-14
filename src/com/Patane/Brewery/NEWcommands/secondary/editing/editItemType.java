package com.Patane.Brewery.NEWcommands.secondary.editing;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.command.CommandSender;

import com.Patane.Brewery.CustomItems.BrItem;
import com.Patane.Brewery.CustomItems.BrItem.CustomType;
import com.Patane.Commands.CommandInfo;
import com.Patane.util.general.Messenger;
import com.Patane.util.general.StringsUtil;

@CommandInfo(
	name = "edit item type",
	description = "Sets the Type for a Brewery Item.",
	usage = "/brewery edit item <item name> type [type]",
	maxArgs = 1,
	hideCommand = true
)
public class editItemType extends editItem {
	
	@Override
	public boolean execute(CommandSender sender, String[] args, Object... objects) {
		
		// Find Type
		CustomType type = null;
		try {
			type = StringsUtil.constructEnum(args[0], CustomType.class);
		} catch (IllegalArgumentException e) {
			// Check if type is valid
			Messenger.send(sender, "&7"+args[0]+" &cis not a valid Item Type.");
			return true;
		}
		
		// Find Item
		BrItem brItem = (BrItem) objects[0];

		// If type did not change, say so and do nothing
		if(brItem.getType() == type) {
			Messenger.send(sender, "&7"+brItem.getName()+" &ais already &7"+type+"&a.");
			return true;
		}
		
		// Setting type
		brItem.setType(type);
		
		// Saving the item to YML
		BrItem.YML().save(brItem);
		
		// Sending the success message
		Messenger.send(sender, "&aSet &7"+brItem.getName()+" &aType to &7"+type+"&a.");
		return true;
	}
	
	@Override
	public List<String> tabComplete(CommandSender sender, String[] args, Object... objects){
		switch(args.length){
		case 1:
			// Showing all Types
			return Arrays.asList(StringsUtil.enumValueStrings(CustomType.class));
		default:
			return new ArrayList<String>();
		}	
	}
}
