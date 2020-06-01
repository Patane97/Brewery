package com.Patane.Brewery.NEWcommands.secondary.editing;

import java.util.Arrays;
import java.util.List;

import org.bukkit.command.CommandSender;

import com.Patane.Brewery.Brewery;
import com.Patane.Brewery.CustomEffects.BrEffect;
import com.Patane.Brewery.CustomItems.BrItem;
import com.Patane.Commands.CommandInfo;
import com.Patane.util.general.Messenger;
import com.Patane.util.general.StringsUtil;

import net.md_5.bungee.api.chat.TextComponent;
@CommandInfo(
	name = "edit item effects modify remove radius",
	description = "Removes the Radius of an Effect for a Brewery Item. These changes are seperate from the original Effect.",
	usage = "/brewery edit item <item name> effects modify <effect name> remove radius"
)
public class editItemEffectsModifyRemoveRadius extends editItemEffectsModifyRemove {

	@Override
	public boolean execute(CommandSender sender, String[] args, Object... objects) {
		
		// Find Item
		BrItem item = (BrItem) objects[0]; 
		
		// Find Effect
		BrEffect effect = (BrEffect) objects[1];
		
		// Default message assumes there is no previous radius, thus 'set' message is given
		String successMsg = "&aRemoved radius for &7"+item.getName()+"&a's instance of &7"+effect.getName()+"&a. Hover to view the details!";
		
		String successHoverText = generateEditingTitle(item, effect);

		Float currentRadius = effect.getRadius();
		// Grab the previous radius for later use
		Float defaultRadius = Brewery.getEffectCollection().getItem(effect.getName()).getRadius();
		
		if(defaultRadius == null) {
			// If both default and current radius are ALREADY removed, do nothing and message appropriately
			if(currentRadius == null) {
				Messenger.send(sender, StringsUtil.hoverText("&eBoth &7"+item.getName()+"&e's instance of &7"+effect.getName()+"&e and the original effect already have no radius. Hover to view the items effect!"
						, generateEditingTitle(item) + effect.toChatString(0, false)));
				return true;
			}
			// If there is no default radius but there IS a current radius, then remove the current radius
			else {
				// Uses original successMsg
				successHoverText += "&c&mRadius&c: &8&m"+currentRadius+"&r";
				
				// Removing the radius from effect
				effect.setRadius(null);
			}
		} 
		// If there IS a default radius the current radius and default do not match, then we are actually removing AND reverting to the default radius rather than just removing
		else if(!currentRadius.equals(defaultRadius)) {
			successMsg = "&aReverted radius for &7"+item.getName()+"&a's instance of &7"+effect.getName()+"&a to original effects radius. Hover to view the details!";
			successHoverText += "&c&mRadius&c: &8&m" + currentRadius +"&r"
							  + "\n"
							  + "&2&lRadius&2: &7" + defaultRadius;
			
			// Setting radius to default radius
			effect.setRadius(defaultRadius);
		}
		// If there IS a default radius and current/default radiuss are the same, then we cannot remove the radius from here. Must be done in standard effect edit command
		else {
			Messenger.send(sender, StringsUtil.hoverText("&7"+item.getName()+"&e's instance of &7"+effect.getName()+"&e is using the original effects radius. You must edit the original effect to remove it. Hover to view the radius!"
					, successHoverText + "&2Radius: &7"+currentRadius));
			return true;
		}
	
		// Save the Item to the YML. This will also save the instance of the effect to the item
		BrItem.YML().save(item);
		
		// Allows the user to view the details on hover
		TextComponent successMsgComponent = StringsUtil.hoverText(successMsg, successHoverText);
		
		Messenger.send(sender, successMsgComponent);
		return true;
	}
	
	@Override
	public List<String> tabComplete(CommandSender sender, String[] args, Object... objects) {
		return Arrays.asList();
	}
}
