package com.Patane.Brewery.Commands.secondary.edit.item.effects;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import com.Patane.Brewery.Brewery;
import com.Patane.Brewery.CustomEffects.BrEffect;
import com.Patane.Brewery.CustomEffects.Modifier;
import com.Patane.Brewery.CustomItems.BrItem;
import com.Patane.Brewery.Handlers.ModifierHandler;
import com.Patane.Commands.CommandInfo;
import com.Patane.util.YAML.MapParsable;
import com.Patane.util.general.Chat;
import com.Patane.util.general.Messenger;
import com.Patane.util.general.StringsUtil;
import com.Patane.util.ingame.Commands;

import net.md_5.bungee.api.chat.TextComponent;
@CommandInfo(
	name = "edit item effects modify set modifier",
	aliases = {"mod"},
	description = "Sets or changes the Modifier of an Effect for a Brewery Item. These changes are seperate from the original Effect.",
	usage = "/brewery edit item <item name> effects modify <effect name> set modifier [type] <values...> "
)
public class editItemEffectsModifySetModifier extends editItemEffectsModifySet {

	@Override
	public boolean execute(CommandSender sender, String[] args, Object... objects) {
		
		// Checking modifier name is given
		if(args.length < 1) {
			Messenger.send(sender, "&ePlease specify a modifier.");
			return true;
		}
		
		// Find Item
		BrItem item = (BrItem) objects[0]; 
		
		// Find Effect
		BrEffect effect = (BrEffect) objects[1];
		
		// Find modifier class through modifier name
		Class<? extends Modifier> modifierClass = ModifierHandler.get(args[0]);
		
		// Checking modifier is found
		if(modifierClass == null) {
			Messenger.send(sender, "&7"+args[0]+" &cis not a valid Modifier.");
			return true;
		}
		
		Modifier modifier = null;
		
		// Default message assumes there is no previous modifier, thus 'set' message is given
		String successMsg = "&aSet the Modifier of &7"+item.getName()+"&a's instance of &7"+effect.getName()+"&a. Hover to view the details!";
		
		String successHoverText = generateEditingTitle(item, effect);
		
		try {
			// Attempting to create the modifier using the found Class
			// and all the arguments after the first (as the first is the modifier type)
			modifier = MapParsable.create(modifierClass, Commands.grabArgs(args, 1, args.length));
		}
		// This will catch if either the value is missing OR an incorrect value was given for an argument
		catch(IllegalArgumentException|NullPointerException e) {
			// These exceptions are formatted and coloured for players to see properly
			Messenger.send(sender, e.getMessage());
			return true;
		}
		// This will catch if theres any other error with generating this mapParsable
		catch(InvocationTargetException e) {
			Messenger.printStackTrace(e);
			Messenger.send(sender, "&cModifier could not be set due to an uncommon error. Please check server console for error trace.");
			return true;
		}
		
		// Grab the previous modifier for later use
		Modifier previousModifier = effect.getModifier();
		
		// If this modifier and provided values are already on effect, do nothing and message appropriately
		if(modifier.equals(previousModifier)) {
			Messenger.send(sender, StringsUtil.hoverText("&eThat Modifier and those values are already on &7"+item.getName()+"&e's instance of &7"+effect.getName()+"&e. Hover to view the details!"
														, successHoverText + modifier.toChatString(0,  true)));
			return true;
		}
		
		// If there was a modifier previously, we are either UPDATING the modifier (same modifier, new values),
		// or CHANGING the modifier (new modifier/values). We must handle successmsg and hover differently for each...
		if(previousModifier != null) {
			// If the modifier names equal, they are the same modifier
			if(modifier.className().equals(previousModifier.className())) {
				// SuccessMsg is an 'update' message
				successMsg = "&aUpdated the Modifier for &7"+item.getName()+"&a's instance of &7"+effect.getName()+"&a. Hover to view the details!";
				// Hover text will use the 'compare' format showing exactly which values have been changed
				// compareFormatter doesnt easily show Modifier and its name, so we just add that before it for simplicity
				successHoverText += "&2Modifier: &7"+modifier.className()+"\n"
								  + StringsUtil.tableCompareFormatter(0,
									s -> "&2  "+s[0]+": &7"+s[1]
								  , s -> "&2  "+s[0]+": &8"+s[1]+" &7-> "+s[2]
								  , StringsUtil.getFieldNames(modifierClass) , StringsUtil.prepValueStrings(previousModifier) , StringsUtil.prepValueStrings(modifier));
			}
			// If the modifier names arent equal, a new modifier has been added
			else {
				// SuccessMsg is a 'changed' message
				successMsg = "&aChanged the Modifier for &7"+item.getName()+"&a's instance of &7"+effect.getName()+"&a. Hover to view the details!";
				// Hover text will show the old modifier being removed (- format) and the new one being added (+ format)
				successHoverText += previousModifier.toChatString(0, true, s -> "&c"+Chat.replace(s[0], "&c")+"&c: &8&m"+Chat.replace(s[1], "&8&m")+"&r")
								  + "\n"
								  + modifier.toChatString(0, true, s -> Chat.add("&2"+s[0]+"&2: &7"+s[1], ChatColor.BOLD));
			}
		}
		// If there was previously no modifier, then add it!
		else {
			// SuccessMsg is an 'added' message
			successMsg = "&aAdded a Modifier to &7"+item.getName()+"&a's instance of &7"+effect.getName()+"&a. Hover to view the details!";
			
			successHoverText += modifier.toChatString(0, true, s -> Chat.add("&2"+s[0]+"&2: &7"+s[1], ChatColor.BOLD));
		}
		
		// Save the new modifier to the effect
		effect.setModifier(modifier);
		
		// Save the Item to the YML. This will also save the instance of the effect to the item
		BrItem.YML().save(item);

		// Allows the user to view the details on hover
		TextComponent successMsgComponent = StringsUtil.hoverText(successMsg, successHoverText);
		
		Messenger.send(sender, successMsgComponent);
		return true;
	}
	
	@Override
	public List<String> tabComplete(CommandSender sender, String[] args, Object... objects) {		
		// If its the first argument (args.length == 1), send all Modifiers
		if(args.length < 2)
			return ModifierHandler.getKeys();
		
		// Once its more than 1 arg, we can attempt to grab the Modifier class
		Class<? extends Modifier> modifierClass = ModifierHandler.get(args[0]);
		
		// If modifier cannot be found
		if(modifierClass == null)
			return Arrays.asList();
		
		// Very specific scenario for Effect modifier.
		// TODO: Low priority: find a spot for this within effect modifier. 
		//     Is difficult as modifier ISNT an actual object by this point, only a class.
		//     Only thought is to have a "specialSuggestion" methos that runs onLoad.
		if(args[0].equalsIgnoreCase("effect"))
			return StringsUtil.encase(Brewery.getEffectCollection().getAllIDs(), "'", "'");

		// Gets the suggestion.
		return MapParsable.getSuggestion(modifierClass, Commands.grabArgs(args, 1, args.length));
	}
}
