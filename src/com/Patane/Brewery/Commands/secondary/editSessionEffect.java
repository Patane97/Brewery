package com.Patane.Brewery.Commands.secondary;

import org.bukkit.command.CommandSender;

import com.Patane.Brewery.Brewery;
import com.Patane.Brewery.Commands.primary.editSessionCommand;
import com.Patane.Brewery.CustomEffects.BrEffect;
import com.Patane.Brewery.Editing.EditSession;
import com.Patane.Commands.CommandInfo;
import com.Patane.util.general.Messenger;
import com.Patane.util.ingame.Commands;

@CommandInfo(
	name = "editsession effect",
	description = "Starts an editing session for a Brewery Effect.",
	usage = "/br editsession effect <effect name>"
)
public class editSessionEffect extends editSessionCommand {
	
	@Override
	public boolean execute(CommandSender sender, String[] args, Object... objects) {
		// Setting name
		String name = Commands.combineArgs(args);

		if(!Brewery.getEffectCollection().hasItem(name)) {
			Messenger.send(sender, "&cThere is no effect named &7"+name+"&c!");
			return true;
		}
		
		BrEffect brEffect = Brewery.getEffectCollection().getItem(name);
		EditSession.start(sender.getName(), brEffect);
		return true;
	}
}
