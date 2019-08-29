package com.Patane.Brewery.Commands.secondary.editing;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.command.CommandSender;

import com.Patane.Brewery.Brewery;
import com.Patane.Brewery.CustomEffects.BrEffect;
import com.Patane.Brewery.CustomEffects.Modifier;
import com.Patane.Brewery.CustomItems.BrItem;
import com.Patane.Brewery.Editing.EditSession;
import com.Patane.Brewery.Handlers.ModifierHandler;
import com.Patane.Commands.CommandHandler.CommandPackage;
import com.Patane.Commands.CommandInfo;
import com.Patane.util.general.GeneralUtil;
import com.Patane.util.general.Messenger;
import com.Patane.util.general.StringsUtil;
import com.Patane.util.ingame.Commands;
@CommandInfo(
	name = "edit effects edit <effectname> set modifier",
	description = "Sets the Modifier for an Effect that is attached to a Brewery Item.",
	usage = "/brewery edit effects edit <effect name> set modifier [type] <values> ..."
)
public class itemEditEffectsEditSetModifier extends itemEditEffectsEditSet {

	@Override
	public boolean execute(CommandSender sender, String[] args, Object... objects) {
		
		String modifierName = args[0];

		BrEffect brEffect = (BrEffect) objects[0];
		
		Class<? extends Modifier> modClass = ModifierHandler.get(modifierName);

		Modifier modifier = null;
		try {
			modifier = GeneralUtil.createMapParsable(modClass, Commands.grabArgs(args, 1, args.length));
		} catch (InvocationTargetException e) {
			Messenger.send(sender, "&cFailed to set &7"+modifierName+" &cModifier for this items &7"+brEffect.getName()+" &ceffect: &7"+e.getMessage());
			return true;
		} catch (IllegalArgumentException e) {
			Messenger.send(sender, e.getMessage());
			return true;
		}
		
		brEffect.setModifier(modifier);
		
		BrItem brItem = (BrItem) EditSession.get(sender.getName());
		
		BrItem.YML().save(brItem);
		
		Messenger.send(sender, "&aSet Modifier for this items &7"+brEffect.getName()+"&a effect to &7"+modifier.name()+"&a.");
		return true;
	}
	
	@Override
	public List<String> tabComplete(CommandSender sender, String[] args, CommandPackage thisPackage) {
		switch(args.length) {
			case 7: return ModifierHandler.getKeys();
			default: 
				Class<? extends Modifier> modClass = ModifierHandler.get(args[6]);
				if(modClass == null)
					return Arrays.asList();
				int index = args.length-8;
				if(index >= modClass.getFields().length)
					return Arrays.asList();
				Field field = modClass.getFields()[index];
				List<String> values = new ArrayList<String>();
				if(field.getType().isEnum())
					values.addAll(Arrays.asList(StringsUtil.enumValueStrings(field.getType().asSubclass(Enum.class))));
				else if(field.getType() == BrEffect.class)
					values.addAll(Brewery.getEffectCollection().getAllIDs());
				values.add("<"+field.getName()+">");
				return values;
		}
	}
}
