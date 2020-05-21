package com.Patane.Brewery.NEWcommands.primary;

import java.util.List;

import org.bukkit.command.CommandSender;

import com.Patane.Commands.CommandInfo;
import com.Patane.Commands.PatCommand;

@CommandInfo(
	name = "remove",
	aliases = {"rem", "delete", "del"},
	description = "Removes a product from Brewery.",
	usage = "/brewery remove [type]",
	permission = "brewery.remove"
)
public class removeCommand extends PatCommand {

	@Override
	public boolean execute(CommandSender sender, String[] args, Object... objects) {
		return this.gotoChild(0, s -> "&7"+s+" &cis not a valid type for removal.", sender, args, objects);
	}
	
	@Override
	public List<String> tabComplete(CommandSender sender, String[] args, Object... objects) {
		return this.tabCompleteCore(sender, args, objects);
	}
}
