package com.Patane.Brewery.Commands.secondary.editing;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.command.CommandSender;

import com.Patane.Brewery.CustomEffects.BrEffect;
import com.Patane.Brewery.CustomEffects.Modifier;
import com.Patane.Brewery.CustomItems.BrItem;
import com.Patane.Brewery.Editing.EditSession;
import com.Patane.Brewery.Editing.EditingInfo;
import com.Patane.Brewery.Handlers.ModifierHandler;
import com.Patane.Commands.CommandInfo;
import com.Patane.util.general.Messenger;
import com.Patane.util.general.StringsUtil;
@CommandInfo(
	name = "edit effects edit <effect name> set modifier",
	description = "Sets the Modifier for an Effect that is attached to a Brewery Item.",
	usage = "/br edit effects edit <effect name> set modifier [type] <values> ..."
)
@EditingInfo(type = BrItem.class)
public class itemEditEffectsEditSetModifier extends itemEditEffectsEditSet {

	// *** THis isnt working???
	@Override
	public boolean execute(CommandSender sender, String[] args, Object... objects) {
		
		String modifierName = args[0];
		Class<? extends Modifier> modClass = ModifierHandler.get(modifierName);
		
		Map<String, Object> fieldValues = new HashMap<String, Object>();
		String[] fieldNames = new String[modClass.getFields().length];
		for(int i=0; i<modClass.getFields().length; i++) {
			fieldNames[i] = modClass.getFields()[i].getName();
			try {
				fieldValues.put(modClass.getFields()[i].getName(), args[i+1]);
			} catch (ArrayIndexOutOfBoundsException e) {
				Messenger.send(sender, "&cMissing required information: &7"+StringsUtil.stringJoiner(fieldNames, "&c, &7"));
			}
		}
		Modifier modifier = null;
		try {
			modifier = modClass.getConstructor(Map.class).newInstance(fieldValues);
		} catch (Exception e) {
			Messenger.send(sender, "&cFailed to set Modifier.");
			e.printStackTrace();
			return true;
		}
		BrEffect brEffect = (BrEffect) objects[0];
		brEffect.setModifier(modifier);
		
		BrItem brItem = (BrItem) EditSession.get(sender.getName());
		
		BrItem.YML().save(brItem);
		
		Messenger.send(sender, "&aSet Modifier to &7"+modifier.name()+"&a.");
		return true;
	}
}
