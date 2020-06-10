package com.Patane.Brewery.Commands.primary;

import java.util.List;

import org.bukkit.command.CommandSender;

import com.Patane.Commands.CommandInfo;
import com.Patane.Commands.PatCommand;

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
		return this.gotoChild(0, sender, args, objects);
	}
	
	@Override
	public List<String> tabComplete(CommandSender sender, String[] args, Object... objects) {
		return this.tabCompleteCore(sender, args, objects);
	}
}
