package com.Patane.Brewery.Commands.primary;

import org.bukkit.command.CommandSender;

import com.Patane.Brewery.Brewery;
import com.Patane.Commands.CommandInfo;
import com.Patane.Commands.PatCommand;
import com.Patane.util.general.Messenger;
@CommandInfo(
	name = "info",
	aliases = {"information","detail"},
	description = "Gives detailed information about a specific Brewery product.",
	usage = "/br info [type] <type name>",
	permission = "brewery.info"
)
public class infoCommand implements PatCommand{

	@Override
	public boolean execute(CommandSender sender, String[] args) {
		try {
			if(args.length < 2) { 
				Messenger.send(sender, "&cPlease specify an effect name.");
				return false;
			}
			return Brewery.getCommandHandler().runChildCommand(this, sender, args);
		} catch(IllegalArgumentException e) {
			Messenger.send(sender, "&7"+args[0]+" &cis not a valid info type.");
			return false;
		}
	}

}
