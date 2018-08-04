package com.Patane.Brewery.Commands.primary;

import org.bukkit.command.CommandSender;

import com.Patane.Brewery.Commands.BrCommandHandler;
import com.Patane.Commands.CommandInfo;
import com.Patane.Commands.PatCommand;
import com.Patane.util.general.Messenger;
import com.Patane.util.ingame.Commands;

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
		PatCommand child = BrCommandHandler.grabInstance().getChildCommand(this, args[0]);
		if(child == null) {
			Messenger.send(sender, "&7"+args[0]+" &cis not a valid list type.");
			return false;
		}
		return child.execute(sender, Commands.grabArgs(args, 1, args.length));
	}

}
