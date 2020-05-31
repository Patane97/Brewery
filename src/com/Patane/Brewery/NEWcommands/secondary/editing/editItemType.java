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

import net.md_5.bungee.api.chat.TextComponent;

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
		
		// Checking for type
		if(args.length < 1) {
			Messenger.send(sender, "&ePlease specify a type.");
			return true;
		}
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
		BrItem item = (BrItem) objects[0];

		String successMsg = "&aChanged &7"+item.getName()+"&a's type. Hover to view the details!";
		
		String successHoverText = generateEditingTitle(item);
		
		// If type did not change, say so and do nothing
		if(item.getType() == type) {
			Messenger.send(sender, StringsUtil.hoverText("&7"+item.getName()+" &eis already of that type. Hover to view!"
					, successHoverText + "&2Type: &7"+item.getType()));
			return true;
		}
		
		successHoverText += "&2Type: &8"+item.getType()+" &7-> "+type;
		
		// Setting type
		item.setType(type);
		
		// Saving the item to YML
		BrItem.YML().save(item);

		// Allows the user to view the details on hover
		TextComponent successMsgComponent = StringsUtil.hoverText(successMsg, successHoverText);
		
		// Sending the success message
		Messenger.send(sender, successMsgComponent);
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
