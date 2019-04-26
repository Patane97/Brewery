package com.Patane.Brewery.Commands.secondary.editing;

import org.bukkit.command.CommandSender;

import com.Patane.Brewery.Brewery;
import com.Patane.Brewery.CustomEffects.BrEffect;
import com.Patane.Brewery.CustomEffects.modifiers.None;
import com.Patane.Brewery.CustomEffects.triggers.Instant;
import com.Patane.Brewery.CustomItems.BrItem;
import com.Patane.Brewery.Editing.EditSession;
import com.Patane.Brewery.Editing.EditingInfo;
import com.Patane.Commands.CommandInfo;
import com.Patane.util.general.Messenger;
import com.Patane.util.general.StringsUtil;
import com.Patane.util.ingame.Commands;
@CommandInfo(
	name = "edit effects add",
	description = "Adds an Effect to a Brewery Item.",
	usage = "/br edit effects add <effect name>"
)
@EditingInfo(type = BrItem.class)
public class itemEditEffectsAdd extends itemEditEffects {

	@Override
	public boolean execute(CommandSender sender, String[] args, Object... objects) {
		String name = Commands.combineArgs(args);
		
		BrEffect brEffect = null;
		String successMsg = null;
		
		// If effect doesnt already exist, create a new one and add it to this item!
		if(!Brewery.getEffectCollection().hasItem(name)) {
			brEffect = new BrEffect(name, new None(), new Instant(), null, null, null, null, null, null, null);
			Brewery.getEffectCollection().add(brEffect);
			
			successMsg = "&aCreated and added &7"+brEffect.getName()+" &aeffect Item.";
		}
		else {
			brEffect = Brewery.getEffectCollection().getItem(name);
			if(brEffect.isComplete())
				successMsg = "&aAdded &7"+brEffect.getName()+" &aeffect to Item.";
			else
				successMsg = "&aAdded incomplete &7"+brEffect.getName()+" &aeffect to Item. Please fill in the missing values for the following: &7"+StringsUtil.stringJoiner(brEffect.getIncomplete(), "&a, &7")+"&r&7.";
		}
		
		BrItem brItem = (BrItem) EditSession.get(sender.getName());
		
		if(brItem.hasEffect(brEffect.getName())) {
			Messenger.send(sender, "&cItem already has &7"+brEffect.getName()+" &ceffect. To edit this effect, use the INSERT COMMAND command.");
			return true;
		}
		
		brItem.addEffect(brEffect);

		BrItem.YML().save(brItem);
		Messenger.send(sender, successMsg);
		return true;
	}
}
