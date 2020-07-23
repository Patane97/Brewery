package com.Patane.Brewery.Commands.secondary.edit.item.effects;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import com.Patane.Brewery.CustomEffects.BrEffect;
import com.Patane.Brewery.CustomEffects.Trigger;
import com.Patane.Brewery.CustomItems.BrItem;
import com.Patane.Brewery.Handlers.TriggerHandler;
import com.Patane.Commands.CommandInfo;
import com.Patane.util.YAML.MapParsable;
import com.Patane.util.general.Chat;
import com.Patane.util.general.Messenger;
import com.Patane.util.general.StringsUtil;
import com.Patane.util.ingame.Commands;

import net.md_5.bungee.api.chat.TextComponent;
@CommandInfo(
	name = "edit item effects modify set trigger",
	aliases = {"trig"},
	description = "Sets or changes the Trigger of an Effect for a Brewery Item. These changes are seperate from the original Effect.",
	usage = "/brewery edit item <item name> effects modify <effect name> set trigger [type] <values...> "
)
public class editItemEffectsModifySetTrigger extends editItemEffectsModifySet {

	@Override
	public boolean execute(CommandSender sender, String[] args, Object... objects) {
		
		// Checking trigger name is given
		if(args.length < 1) {
			Messenger.send(sender, "&ePlease specify a trigger.");
			return true;
		}
		
		// Find Item
		BrItem item = (BrItem) objects[0]; 
		
		// Find Effect
		BrEffect effect = (BrEffect) objects[1];
		
		// Find trigger class through trigger name
		Class<? extends Trigger> triggerClass = TriggerHandler.get(args[0]);
		
		// Checking trigger is found
		if(triggerClass == null) {
			Messenger.send(sender, "&7"+args[0]+" &cis not a valid Trigger.");
			return true;
		}
		
		Trigger trigger = null;
		
		// Default message assumes there is no previous trigger, thus 'set' message is given
		String successMsg = "&aSet the Trigger of &7"+item.getName()+"&a's instance of &7"+effect.getName()+"&a. Hover to view the details!";
		
		String successHoverText = generateEditingTitle(item, effect);
		
		try {
			// Attempting to create the trigger using the found Class
			// and all the arguments after the first (as the first is the trigger type)
			trigger = MapParsable.create(triggerClass, Commands.grabArgs(args, 1, args.length));
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
			Messenger.send(sender, "&cTrigger could not be set due to an uncommon error. Please check server console for error trace.");
			return true;
		}
		
		// Grab the previous trigger for later use
		Trigger previousTrigger = effect.getTrigger();
		
		// If this trigger and provided values are already on effect, do nothing and message appropriately
		if(trigger.equals(previousTrigger)) {
			Messenger.send(sender, StringsUtil.hoverText("&eThat Trigger and those values are already on &7"+item.getName()+"&e's instance of &7"+effect.getName()+"&e. Hover to view the details!"
														, successHoverText + trigger.toChatString(0, true)));
			return true;
		}
		
		// If there was a trigger previously, we are either UPDATING the trigger (same trigger, new values),
		// or CHANGING the trigger (new trigger/values). We must handle successmsg and hover differently for each...
		if(previousTrigger != null) {
			// If the trigger names equal, they are the same trigger
			if(trigger.className().equals(previousTrigger.className())) {
				// SuccessMsg is an 'update' message
				successMsg = "&aUpdated the Trigger for &7"+item.getName()+"&a's instance of &7"+effect.getName()+"&a. Hover to view the details!";
				// Hover text will use the 'compare' format showing exactly which values have been changed
				// compareFormatter doesnt easily show Trigger and its name, so we just add that before it for simplicity
				successHoverText += "&2Trigger: &7"+trigger.className()+"\n"
								  + StringsUtil.tableCompareFormatter(0,
									s -> "&2  "+s[0]+": &7"+s[1]
								  , s -> "&2  "+s[0]+": &8"+s[1]+" &7-> "+s[2]
								  , StringsUtil.getFieldNames(triggerClass) , StringsUtil.prepValueStrings(previousTrigger) , StringsUtil.prepValueStrings(trigger));
			}
			// If the trigger names arent equal, a new trigger has been added
			else {
				// SuccessMsg is a 'changed' message
				successMsg = "&aChanged the Trigger of &7"+item.getName()+"&a's instance of &7"+effect.getName()+"&a. Hover to view the details!";
				// Hover text will show the old trigger being removed (- format) and the new one being added (+ format)
				successHoverText += previousTrigger.toChatString(0, true, s -> "&c"+Chat.replace(s[0], "&c")+"&c: &8&m"+Chat.replace(s[1], "&8&m")+"&r")
								  + "\n"
								  + trigger.toChatString(0, true, s -> Chat.add("&2"+s[0]+"&2: &7"+s[1], ChatColor.BOLD));
			}
		}
		// If there was previously no trigger, then add it!
		else {
			// SuccessMsg is an 'added' message
			successMsg = "&aAdded a Trigger to &7"+item.getName()+"&a's instance of &7"+effect.getName()+"&a. Hover to view the details!";
			
			successHoverText += trigger.toChatString(0, true, s -> Chat.add("&2"+s[0]+"&2: &7"+s[1], ChatColor.BOLD));
		}
		// Save the new trigger to the effect
		effect.setTrigger(trigger);
		
		// Save the Item to the YML. This will also save the instance of the effect to the item
		BrItem.YML().save(item);

		// Allows the user to view the details on hover
		TextComponent successMsgComponent = StringsUtil.hoverText(successMsg, successHoverText);
		
		Messenger.send(sender, successMsgComponent);
		return true;
	}
	
	@Override
	public List<String> tabComplete(CommandSender sender, String[] args, Object... objects) {		
		// If its the first argument (args.length == 1), send all Triggers
		if(args.length < 2)
			return TriggerHandler.getKeys();
		
		// Once its more than 1 arg, we can attempt to grab the Trigger class
		Class<? extends Trigger> triggerClass = TriggerHandler.get(args[0]);
		
		// If trigger cannot be found
		if(triggerClass == null)
			return Arrays.asList();
		
		// Gets the suggestion.
		return MapParsable.getSuggestion(triggerClass, Commands.grabArgs(args, 1, args.length));
	}
}
