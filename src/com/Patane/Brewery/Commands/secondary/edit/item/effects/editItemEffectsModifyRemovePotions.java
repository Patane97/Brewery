package com.Patane.Brewery.Commands.secondary.edit.item.effects;

import java.util.Arrays;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.potion.PotionEffect;

import com.Patane.Brewery.Brewery;
import com.Patane.Brewery.CustomEffects.BrEffect;
import com.Patane.Brewery.CustomItems.BrItem;
import com.Patane.Commands.CommandInfo;
import com.Patane.util.general.Chat;
import com.Patane.util.general.Messenger;
import com.Patane.util.general.StringsUtil;
import com.Patane.util.general.StringsUtil.LambdaStrings;

import net.md_5.bungee.api.chat.TextComponent;
@CommandInfo(
	name = "edit item effects modify remove potions",
	description = "Removes the Potion Effects of an Effect for a Brewery Item. These changes are seperate from the original Effect.",
	usage = "/brewery edit item <item name> effects modify <effect name> remove potions"
)
public class editItemEffectsModifyRemovePotions extends editItemEffectsModifyRemove {

	@Override
	public boolean execute(CommandSender sender, String[] args, Object... objects) {
		
		// Find Item
		BrItem item = (BrItem) objects[0]; 
		
		// Find Effect
		BrEffect effect = (BrEffect) objects[1];
		
		// Default message assumes there is no previous potions, thus 'set' message is given
		String successMsg = "&aRemoved all potion effects for &7"+item.getName()+"&a's instance of &7"+effect.getName()+"&a. Hover to view the details!";
		
		String successHoverText = generateEditingTitle(item, effect);

		List<PotionEffect> currentPotions = effect.getPotions();
		// Grab the previous potions for later use
		List<PotionEffect> defaultPotions = Brewery.getEffectCollection().getItem(effect.getName()).getPotions();
		
		if(defaultPotions.isEmpty()) {
			// If both default and current potions are ALREADY removed, do nothing and message appropriately
			if(currentPotions.isEmpty()) {
				Messenger.send(sender, StringsUtil.hoverText("&eBoth &7"+item.getName()+"&e's instance of &7"+effect.getName()+"&e and the original effect already have no potion effects. Hover to view the items effect!"
						, generateEditingTitle(item) + effect.toChatString(0, false)));
				return true;
			}
			// If there is no default potions but there IS a current potions, then remove the current potions
			else {
				// Uses original successMsg
				successHoverText += toChatStringCustom(s -> "&c&m"+Chat.replace(Chat.add(s[0], "&m"), "&7", "&8")+"&c: &8&m"+Chat.replace(s[1], "&8&m")+"&r", currentPotions);
				
				// Removing the potions from effect
				effect.setPotions(null);
			}
		} 
		// If there IS a default potions the current potions and default do not match, then we are actually removing AND reverting to the default potions rather than just removing
		else if(!currentPotions.equals(defaultPotions)) {
			successMsg = "&aReverted all potion effects for &7"+item.getName()+"&a's instance of &7"+effect.getName()+"&a to original effects potions. Hover to view the details!";
			successHoverText += toChatStringCustom(s -> "&c&m"+Chat.replace(Chat.add(s[0], "&m"), "&7", "&8")+"&c: &8&m"+Chat.replace(s[1], "&8&m")+"&r", currentPotions)
							  + "\n"
							  + toChatStringCustom(s -> Chat.add("&2"+s[0]+"&2: &7"+s[1], ChatColor.BOLD), defaultPotions);
			
			// Setting potions to default potions
			effect.setPotions(defaultPotions);
		}
		// If there IS a default potions and current/default potionss are the same, then we cannot remove the potions from here. Must be done in standard effect edit command
		else {
			Messenger.send(sender, StringsUtil.hoverText("&7"+item.getName()+"&e's instance of &7"+effect.getName()+"&e is using the original effects potions. You must edit the original effect to remove them. Hover to view the potion effects!"
					, successHoverText + toChatStringCustom(s -> "&2"+s[0]+"&2: &7"+s[1], defaultPotions)));
			return true;
		}
	
		// Save the Item to the YML. This will also save the instance of the effect to the item
		BrItem.YML().save(item);
		
		// Allows the user to view the details on hover
		TextComponent successMsgComponent = StringsUtil.hoverText(successMsg, successHoverText);
		
		Messenger.send(sender, successMsgComponent);
		return true;
	}
	
	// This is repeated quite a bit, so we may as well save it customized with the 'Potion Effects" title
	private String toChatStringCustom(LambdaStrings layout, List<PotionEffect> potionEffects) {
		return layout.build("Potion Effects", "") + "\n" + StringsUtil.toChatString(2, true, layout, potionEffects.toArray(new PotionEffect[0]));
	}
	
	@Override
	public List<String> tabComplete(CommandSender sender, String[] args, Object... objects) {
		return Arrays.asList();
	}
}
