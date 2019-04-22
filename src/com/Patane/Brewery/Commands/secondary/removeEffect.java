package com.Patane.Brewery.Commands.secondary;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.Patane.Brewery.Brewery;
import com.Patane.Brewery.Commands.primary.removeCommand;
import com.Patane.Brewery.CustomEffects.BrEffect;
import com.Patane.Commands.CommandInfo;
import com.Patane.util.general.Messenger;
import com.Patane.util.ingame.Commands;
@CommandInfo(
	name = "remove effect",
	description = "Removes an effect from Brewery.",
	usage = "/br remove effect <effect name>"
)
public class removeEffect extends removeCommand {

	@Override
	public boolean execute(CommandSender sender, String[] args, Object... objects) {
		
		if(args.length == 0 || args[0] == null) {
			Messenger.send(sender, "&cPlease specify an effect name.");
			return false;
		}
		
		// Setting name
		String name = Commands.combineArgs(args);

		if(!Brewery.getEffectCollection().hasItem(name)) {
			Messenger.send(sender, "&cThere is no effect named &7"+name+"&c!");
			return false;
		}
		
		try {
			BrEffect brEffect = Brewery.getEffectCollection().getItem(name);
			
			// Attempt to clear the effect from YML
			BrEffect.YML().clearSection(brEffect.getName());
			
			Brewery.getEffectCollection().remove(brEffect.getID());
			
			Messenger.send(sender, "&aRemoved effect &7"+ brEffect.getName() +"&a.");
			// Maybe print a more detailed 'create' message in console?
		} catch (Exception e) {
			if(sender instanceof Player)
				Messenger.send(sender, "&cFailed to remove effect due to the following error: \n &4&o" + e.getMessage());
			else
				Messenger.warning("Failed to remove effect");
			e.printStackTrace();
		}
		
		return true;
	}
}
