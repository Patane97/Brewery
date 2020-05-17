package com.Patane.Brewery.NEWcommands.primary;

import java.util.List;

import org.bukkit.command.CommandSender;

import com.Patane.Commands.CommandInfo;
import com.Patane.Commands.PatCommand;

@CommandInfo(
	name = "edit",
	description = "Edits a product within Brewery.",
	usage = "/brewery edit [type]",
	permission = "brewery.edit"
)
public class editCommand extends PatCommand {
	
	@Override
	public boolean execute(CommandSender sender, String[] args, Object... objects) {
		return this.gotoChild(0, s -> "&7"+s+" &cis not a valid type to edit.", sender, args, objects);
	}
	
	@Override
	public List<String> tabComplete(CommandSender sender, String[] args, Object... objects) {
		return this.tabCompleteCore(sender, args, objects);
	}
}
