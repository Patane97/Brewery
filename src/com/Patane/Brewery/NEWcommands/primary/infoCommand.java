package com.Patane.Brewery.NEWcommands.primary;

import java.util.List;

import org.bukkit.command.CommandSender;

import com.Patane.Commands.CommandInfo;
import com.Patane.Commands.PatCommand;
@CommandInfo(
	name = "info",
	aliases = {"information","detail"},
	description = "Provides detailed information for a Brewery product.",
	usage = "/brewery info [type]",
	permission = "brewery.info"
)
public class infoCommand extends PatCommand{

	@Override
	public boolean execute(CommandSender sender, String[] args, Object... objects) {
		return this.gotoChild(0, s -> "&7"+s+" &cis not a valid type.", sender, args, objects);
	}
	
	@Override
	public List<String> tabComplete(CommandSender sender, String[] args, Object... objects) {
		return this.tabCompleteCore(sender, args, objects);
	}
}
