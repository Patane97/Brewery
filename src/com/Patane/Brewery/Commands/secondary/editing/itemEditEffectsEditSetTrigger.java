package com.Patane.Brewery.Commands.secondary.editing;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.command.CommandSender;

import com.Patane.Brewery.CustomEffects.Trigger;
import com.Patane.Brewery.CustomItems.BrItem;
import com.Patane.Brewery.Editing.EditingInfo;
import com.Patane.Brewery.Handlers.TriggerHandler;
import com.Patane.Commands.CommandHandler.CommandPackage;
import com.Patane.Commands.CommandInfo;
import com.Patane.util.general.StringsUtil;
@CommandInfo(
	name = "edit effects edit <effectname> set trigger",
	description = "Sets the Trigger for an Effect that is attached to a Brewery Item.",
	usage = "/brewery edit effects edit <effect name> set trigger [type] <values> ..."
)
@EditingInfo(type = BrItem.class)
public class itemEditEffectsEditSetTrigger extends itemEditEffectsEditSet {

	@Override
	public boolean execute(CommandSender sender, String[] args, Object... objects) {
//		
//		String triggerName = args[0];
//		
//		BrEffect brEffect = (BrEffect) objects[0];
//		
//		Class<? extends Trigger> trigClass = TriggerHandler.get(triggerName);
//		Trigger trigger = null;
//		try {
//			trigger = GeneralUtil.createMapParsable(trigClass, Commands.grabArgs(args, 1, args.length));
//		} catch (InvocationTargetException e) {
//			Messenger.send(sender, "&cFailed to set &7"+triggerName+" &cTrigger for this items &7"+brEffect.getName()+" &ceffect: &7"+e.getMessage());
//			return true;
//		} catch (IllegalArgumentException e) {
//			Messenger.send(sender, e.getMessage());
//			return true;
//		}
//		
//		
//		BrItem brItem = (BrItem) EditSession.get(sender.getName());
//
//		BrEffect effect2 = Brewery.getItemCollection().getItem(brItem.getName()).getEffect(brEffect.getName());
//		Messenger.debug("BEFORE EFFECT TRIGGER: "+(effect2.getTrigger() == null ? "NULL": effect2.getTrigger().className()));
//		brEffect.setTrigger(trigger);
////		brItem.updateEffect(brEffect);
//		
//		BrItem.YML().save(brItem);
//		
//		effect2 = Brewery.getItemCollection().getItem(brItem.getName()).getEffect(brEffect.getName());
//		Messenger.debug("AFTER EFFECT TRIGGER: "+effect2.getTrigger().className());
//		Messenger.send(sender, "&aSet Trigger for this items &7"+brEffect.getName()+"&a effect to &7"+trigger.className()+"&a.");
		return true;
	}
	@Override
	public List<String> tabComplete(CommandSender sender, String[] args, CommandPackage thisPackage) {
		switch(args.length) {
			case 7: return TriggerHandler.getKeys();
			default: 
				Class<? extends Trigger> trigClass = TriggerHandler.get(args[6]);
				if(trigClass == null)
					return Arrays.asList();
				int index = args.length-8;
				if(index >= trigClass.getFields().length)
					return Arrays.asList();
				Field field = trigClass.getFields()[index];
				List<String> values = new ArrayList<String>();
				if(field.getType().isEnum())
					values.addAll(Arrays.asList(StringsUtil.enumValueStrings(field.getType().asSubclass(Enum.class))));
				values.add("<"+field.getName()+">");
				return values;
		}
	}
}
