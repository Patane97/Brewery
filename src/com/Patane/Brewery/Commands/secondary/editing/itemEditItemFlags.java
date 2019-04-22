package com.Patane.Brewery.Commands.secondary.editing;

import org.bukkit.command.CommandSender;

import com.Patane.Brewery.Commands.BrCommandHandler;
import com.Patane.Commands.CommandHandler;
import com.Patane.Commands.CommandInfo;
import com.Patane.Commands.PatCommand;
import com.Patane.util.general.Messenger;
@CommandInfo(
	name = "edit item flags",
	aliases = {"flag"},
	description = "Edits an items flags.",
	usage = "/br edit item flags [add|remove] [item flag]",
	hideCommand = true
)
public class itemEditItemFlags extends itemEditItem {

	@Override
	public boolean execute(CommandSender sender, String[] args, Object... objects) {
		PatCommand child = BrCommandHandler.grabInstance().getChildCommand(this, args[0]);
		if(child == null) {
			Messenger.send(sender, "&7"+args[0]+" &cis an invalid argument.");
			return false;
		}
		CommandHandler.grabInstance().handleCommand(sender, child, args);
		return true;
	}
}
