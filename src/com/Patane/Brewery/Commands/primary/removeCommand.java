package com.Patane.Brewery.Commands.primary;

import org.bukkit.command.CommandSender;

import com.Patane.Brewery.Commands.BrCommandHandler;
import com.Patane.Commands.CommandHandler;
import com.Patane.Commands.CommandInfo;
import com.Patane.Commands.PatCommand;
import com.Patane.util.general.Messenger;

@CommandInfo(
	name = "remove",
	aliases = {"rem", "delete", "del"},
	description = "Removes something from Brewery.",
	usage = "/br remove [type] <name>",
	permission = "brewery.remove"
)
public class removeCommand implements PatCommand {
	
	@Override
	public boolean execute(CommandSender sender, String[] args, Object... objects) {
		PatCommand child = BrCommandHandler.grabInstance().getChildCommand(this, args[0]);
		if(child == null) {
			Messenger.send(sender, "&7"+args[0]+" &cis not a valid removal type.");
			return false;
		}
		CommandHandler.grabInstance().handleCommand(sender, child, args);
		return true;
	}
}
