package com.Patane.Brewery.Commands.secondary.edit.effect;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import com.Patane.Brewery.Brewery;
import com.Patane.Brewery.CustomEffects.BrEffect;
import com.Patane.Brewery.CustomEffects.Trigger;
import com.Patane.Brewery.Handlers.TriggerHandler;
import com.Patane.Commands.CommandInfo;
import com.Patane.util.YAML.MapParsable;
import com.Patane.util.general.Chat;
import com.Patane.util.general.GeneralUtil;
import com.Patane.util.general.Messenger;
import com.Patane.util.general.StringsUtil;
import com.Patane.util.ingame.Commands;

import net.md_5.bungee.api.chat.TextComponent;
@CommandInfo(
	name = "edit effect set trigger",
	aliases = {"mod"},
	description = "Sets or changes the Trigger of an original Effect.",
	usage = "/brewery edit effect <effect name> set trigger [type] <values...> "
)
public class editEffectSetTrigger extends editEffectSet {

	@Override
	public boolean execute(CommandSender sender, String[] args, Object... objects) {
		
		// Checking trigger name is given
		if(args.length < 1) {
			Messenger.send(sender, "&ePlease specify a trigger.");
			return true;
		}
		// Find Effect
		BrEffect effect = (BrEffect) objects[0];
		
		// Find trigger class through trigger name
		Class<? extends Trigger> triggerClass = TriggerHandler.get(args[0]);
		
		
		// Checking trigger is found
		if(triggerClass == null) {
			Messenger.send(sender, String.format("&7%s &cis not a valid Trigger.", args[0]));
			return true;
		}
		
		Trigger trigger = null;
		
		// Default message assumes there is no previous trigger, thus 'set' message is given
		String successMsg = String.format("&aSet the Trigger of &7%s&a. Hover to view the details!", effect.getName());
		
		String successHoverText = generateEditingTitle(effect);
		
		try {
			// Attempting to create the trigger using the found Class
			// and all the arguments after the first (as the first is the trigger type)
			trigger = GeneralUtil.createMapParsable(triggerClass, Commands.grabArgs(args, 1, args.length));
		}
		// This will catch if either the value is missing OR an incorrect value was given for an argument
		catch(IllegalArgumentException|NullPointerException e) {
			// These exceptions are formatted and coloured for players to see properly
			Messenger.send(sender, e.getMessage());
			return true;
		}
		// This will catch if theres any other error with generating this mapParsable
		catch(InvocationTargetException e) {
			Messenger.send(sender, "&cTrigger could not be set due to an uncommon error. Please check server console for error trace.");
			return true;
		}
		
		// Grab the previous trigger for later use
		Trigger previousTrigger = effect.getTrigger();
		
		// If this trigger and provided values are already on effect, do nothing and message appropriately
		if(trigger.equals(previousTrigger)) {
			Messenger.send(sender, StringsUtil.hoverText(String.format("&eThat Trigger and those values are already present on &7%s&e. Hover to view the details!", effect.getName())
														, successHoverText + trigger.toChatString(0,  true)));
			return true;
		}
		
		// If there was a trigger previously, we are either UPDATING the trigger (same trigger, new values),
		// or CHANGING the trigger (new trigger/values). We must handle successmsg and hover differently for each...
		if(previousTrigger != null) {
			// If the trigger names equal, they are the same trigger (UPDATING)
			if(trigger.className().equals(previousTrigger.className())) {
				// SuccessMsg is an 'update' message
				successMsg = String.format("&aUpdated the Trigger for &7%s&a. Hover to view the details!", effect.getName());
				
				// Hover text will use the 'compare' format showing exactly which values have been changed
				// compareFormatter doesnt easily show Trigger and its name, so we just add that before it for simplicity
				successHoverText += String.format("&2Trigger: &7%s\n", trigger.className())
								  + StringsUtil.tableCompareFormatter(0,
									s -> "&2  "+s[0]+": &7"+s[1]
								  , s -> "&2  "+s[0]+": &8"+s[1]+" &7-> "+s[2]
								  , StringsUtil.getFieldNames(triggerClass) , StringsUtil.prepValueStrings(previousTrigger) , StringsUtil.prepValueStrings(trigger));
			}
			// If the trigger names arent equal, a new trigger has been added (CHANGING)
			else {
				// SuccessMsg is a 'changed' message
				successMsg = String.format("&aChanged the Trigger for &7%s&a. Hover to view the details!", effect.getName());
				// Hover text will show the old trigger being removed (- format) and the new one being added (+ format)
				successHoverText += previousTrigger.toChatString(0, true, s -> "&c"+Chat.replace(s[0], "&c")+"&c: &8&m"+Chat.replace(s[1], "&8&m")+"&r")
								  + "\n"
								  + trigger.toChatString(0, true, s -> Chat.add("&2"+s[0]+"&2: &7"+s[1], ChatColor.BOLD));
			}
		}
		// If there was previously no trigger, then add it!
		else {
			// SuccessMsg is an 'added' message
			successMsg = String.format("&aAdded a Trigger to &7%s&a. Hover to view the details!", effect.getName());
			
			successHoverText += trigger.toChatString(0, true, s -> Chat.add("&2"+s[0]+"&2: &7"+s[1], ChatColor.BOLD));
		}
			
		// Save the new trigger to the effect
		effect.setTrigger(trigger);

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
		BrEffect effect = (BrEffect) objects[0];
		
		if(effect == null)
			return Arrays.asList();
		
		// If its the first argument (args.length == 1), send all Triggers
		if(args.length < 2)
			return TriggerHandler.getKeys();
		
		// Once its more than 1 arg, we can attempt to grab the Trigger class
		Class<? extends Trigger> triggerClass = TriggerHandler.get(args[0]);
		
		// If trigger cannot be found
		if(triggerClass == null)
			return Arrays.asList();
		
		// Save all fields for found trigger class
		Field[] fields = triggerClass.getFields();
		
		// index is this as we want the last argument (args.length-1) PAST the trigger arg (-2 instead of -1)
		int index = args.length - 2;
		
		// If index is past fields length
		if(index >= fields.length)
			return Arrays.asList();
		
		// Very specific scenario for Effect trigger.
		// *** Low priority: find a spot for this within effect trigger. 
		//     Is difficult as trigger ISNT an actual object by this point, only a class.
		//     Only thought is to have a "specialSuggestion" methos that runs onLoad.
		if(args[0].equalsIgnoreCase("effect") && fields[index].getType().isAssignableFrom(BrEffect.class))
			return StringsUtil.encase(Brewery.getEffectCollection().getAllIDs(), "'", "'");
		
		// Gets the suggestion. If its an enum, shows the enums available
		return MapParsable.getSuggestion(fields[index]);
	}
	
	protected String generateEditingTitle(BrEffect effect) {
		return "&f&l" + effect.getNameLimited(15)+"\n";
	}
}
