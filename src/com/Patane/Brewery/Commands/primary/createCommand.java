package com.Patane.Brewery.Commands.primary;

import org.bukkit.command.CommandSender;

import com.Patane.Brewery.Commands.BrCommandHandler;
import com.Patane.Commands.CommandHandler;
import com.Patane.Commands.CommandInfo;
import com.Patane.Commands.PatCommand;
import com.Patane.util.general.Messenger;

@CommandInfo(
	name = "create",
	aliases = {"cr", "new"},
	description = "Creates something new in Brewery.",
	usage = "/br create [type] <name>",
	permission = "brewery.create"
)
public class createCommand implements PatCommand {
	
	@Override
	public boolean execute(CommandSender sender, String[] args, Object... objects) {
		PatCommand child = BrCommandHandler.grabInstance().getChildCommand(this, args[0]);
		if(child == null) {
			Messenger.send(sender, "&7"+args[0]+" &cis not a valid creation type.");
			return false;
		}
		CommandHandler.grabInstance().handleCommand(sender, child, args);
		return true;
	}
}
