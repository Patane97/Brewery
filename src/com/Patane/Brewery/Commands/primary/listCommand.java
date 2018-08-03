package com.Patane.Brewery.Commands.primary;

import org.bukkit.command.CommandSender;

import com.Patane.Brewery.Brewery;
import com.Patane.Commands.CommandInfo;
import com.Patane.Commands.PatCommand;
import com.Patane.util.general.Messenger;

@CommandInfo(
	name = "list",
	aliases = {"listall","all"},
	description = "Lists a type of Brewery product.",
	usage = "/br list [type]",
	permission = "brewery.list"
)
public class listCommand implements PatCommand {

	@Override
	public boolean execute(CommandSender sender, String[] args) {
		try {
			return Brewery.getCommandHandler().runChildCommand(this, sender, args);
		} catch(IllegalArgumentException e) {
			Messenger.send(sender, "&7"+args[0]+" &cis not a valid list type.");
			return false;
		}
	}

}
