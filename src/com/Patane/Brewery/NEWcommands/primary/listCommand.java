package com.Patane.Brewery.NEWcommands.primary;

import org.bukkit.command.CommandSender;

import com.Patane.Brewery.Commands.BrCommandHandler;
import com.Patane.Commands.CommandHandler;
import com.Patane.Commands.CommandHandler.CommandPackage;
import com.Patane.Commands.CommandInfo;
import com.Patane.Commands.PatCommand;
import com.Patane.util.general.Messenger;

@CommandInfo(
	name = "list",
	aliases = {"listall","all"},
	description = "Lists a type of Brewery product.",
	usage = "/brewery list [type]",
	permission = "brewery.list"
)
public class listCommand extends PatCommand {

	@Override
	public boolean execute(CommandSender sender, String[] args, Object... objects) {
		CommandPackage child = BrCommandHandler.getChildPackage(this.getClass(), args[0]);
		if(child == null) {
			Messenger.send(sender, "&7"+args[0]+" &cis not a valid list type.");
			return false;
		}
		CommandHandler.grabInstance().handleCommand(sender, child.command(), args);
		return true;
	}
}
