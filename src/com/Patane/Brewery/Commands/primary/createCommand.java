package com.Patane.Brewery.Commands.primary;

import java.util.List;

import org.bukkit.command.CommandSender;

import com.Patane.Commands.CommandInfo;
import com.Patane.Commands.PatCommand;

@CommandInfo(
	name = "create",
	aliases = {"cr", "new"},
	description = "Creates a product within Brewery.",
	usage = "/brewery create [type]",
	permission = "brewery.create"
)
public class createCommand extends PatCommand {
	
	@Override
	public boolean execute(CommandSender sender, String[] args, Object... objects) {
		return this.gotoChild(0, s -> "&7"+s+" &cis not a valid type for creation.", sender, args, objects);
	}
	
	@Override
	public List<String> tabComplete(CommandSender sender, String[] args, Object... objects) {
		return this.tabCompleteCore(sender, args, objects);
	}
}
