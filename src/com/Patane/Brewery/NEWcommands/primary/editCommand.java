package com.Patane.Brewery.NEWcommands.primary;

import java.util.List;

import org.bukkit.command.CommandSender;

import com.Patane.Brewery.commands.BrCommandHandler;
import com.Patane.Commands.CommandHandler;
import com.Patane.Commands.CommandHandler.CommandPackage;
import com.Patane.Commands.CommandInfo;
import com.Patane.Commands.PatCommand;
import com.Patane.util.general.Messenger;

@CommandInfo(
	name = "edit",
	description = "Edits a product within Brewery.",
	usage = "/brewery edit [type] ...",
	permission = "brewery.edit"
)
public class editCommand extends PatCommand {
	
	@Override
	public boolean execute(CommandSender sender, String[] args, Object... objects) {

		// Find child command
		CommandPackage child = BrCommandHandler.getChildPackage(this.getClass(), args[0]);
		if(child == null) {
			Messenger.send(sender, "&7"+args[0]+" &cis not a valid type to edit.");
			return false;
		}
		
		// Handle child command with specific arguments & objects
		CommandHandler.grabInstance().handleCommand(sender, child.command(), args);
		return true;
	}
	
	@Override
	public List<String> tabComplete(CommandSender sender, String[] args, Object... objects) {
		return tabCompleteCore(this, sender, args);
	}
}
