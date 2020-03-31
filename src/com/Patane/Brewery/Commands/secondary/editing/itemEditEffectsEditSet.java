package com.Patane.Brewery.commands.secondary.editing;

import org.bukkit.command.CommandSender;

import com.Patane.Brewery.commands.BrCommandHandler;
import com.Patane.Commands.CommandHandler;
import com.Patane.Commands.CommandHandler.CommandPackage;
import com.Patane.Commands.CommandInfo;
import com.Patane.util.general.Messenger;
@CommandInfo(
	name = "edit effects edit <effectname> set",
	description = "Sets something in an Effect that is attached to a Brewery Item.",
	usage = "/brewery edit effects edit <effect name> set [something] ..."
)
public class itemEditEffectsEditSet extends itemEditEffectsEdit {

	@Override
	public boolean execute(CommandSender sender, String[] args, Object... objects) {
		CommandPackage child = BrCommandHandler.getChildPackage(this.getClass(), args[0]);
		if(child == null) {
			Messenger.send(sender, "&7"+args[0]+" &cis an invalid argument.");
			return false;
		}
		CommandHandler.grabInstance().handleCommand(sender, child.command(), args, objects);
		return true;
	}
}
