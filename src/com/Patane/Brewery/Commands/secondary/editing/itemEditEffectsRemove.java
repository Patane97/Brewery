package com.Patane.Brewery.Commands.secondary.editing;

import org.bukkit.command.CommandSender;

import com.Patane.Brewery.Brewery;
import com.Patane.Brewery.CustomEffects.BrEffect;
import com.Patane.Brewery.CustomItems.BrItem;
import com.Patane.Brewery.Editing.EditSession;
import com.Patane.Brewery.Editing.EditingInfo;
import com.Patane.Commands.CommandInfo;
import com.Patane.util.general.Messenger;
import com.Patane.util.ingame.Commands;
@CommandInfo(
	name = "edit effects remove",
	description = "Removes an Effect from a Brewery Item.",
	usage = "/br edit effects remove <effect name>"
)
@EditingInfo(type = BrItem.class)
public class itemEditEffectsRemove extends itemEditEffects {

	@Override
	public boolean execute(CommandSender sender, String[] args, Object... objects) {

		String name = Commands.combineArgs(args);

		BrEffect brEffect = null;
		
		if(!Brewery.getEffectCollection().hasItem(name)) {
			Messenger.send(sender, "&cThere is no effect named &7"+name+"&c.");
			return true;
		}
		brEffect = Brewery.getEffectCollection().getItem(name);
		BrItem brItem = (BrItem) EditSession.get(sender.getName());
		
		if(!brItem.hasEffect(name)) {
			Messenger.send(sender, "&cItem does not have &7"+brEffect.getName()+" &ceffect.");
			return true;
		}
		
		brItem.removeEffect(brEffect.getName());

		BrItem.YML().save(brItem);
		Messenger.send(sender, "&aRemoved &7"+brEffect.getName()+" &aeffect from Item.");
		return true;
	}
}