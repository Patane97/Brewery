package com.Patane.Brewery.NEWcommands.secondary.editing;

import java.util.List;

import org.bukkit.command.CommandSender;

import com.Patane.Brewery.commands.BrCommandHandler;
import com.Patane.Commands.CommandHandler;
import com.Patane.Commands.CommandHandler.CommandPackage;
import com.Patane.Commands.CommandInfo;
import com.Patane.util.general.Messenger;

@CommandInfo(
	name = "edit item item attributes",
	aliases = {"attribute", "attrib"},
	description = "Edits the Attributes of a physical Minecraft Item for a Brewery Item.",
	usage = "/brewery edit item <item name> item attributes [add|remove]"
)
public class editItemItemstackAttributes extends editItemItemstack {
	
	@Override
	public boolean execute(CommandSender sender, String[] args, Object... objects) {

		// Find child command
		CommandPackage child = BrCommandHandler.getChildPackage(this.getClass(), args[0]);
		if(child == null) {
			Messenger.send(sender, "&7"+args[0]+" &cis an invalid argument.");
			return false;
		}
		
		// Handle child command with specific arguments & objects (object[0] = BrItem)
		CommandHandler.grabInstance().handleCommand(sender, child.command(), args, objects);
		return true;
	}
	
	@Override
	public List<String> tabComplete(CommandSender sender, String[] args, Object... objects) {
		return tabCompleteCore(this, sender, args, objects);
	}
}
