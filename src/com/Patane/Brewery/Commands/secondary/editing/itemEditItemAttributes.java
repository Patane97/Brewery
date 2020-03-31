package com.Patane.Brewery.commands.secondary.editing;

import org.bukkit.command.CommandSender;

import com.Patane.Brewery.commands.BrCommandHandler;
import com.Patane.Commands.CommandHandler;
import com.Patane.Commands.CommandHandler.CommandPackage;
import com.Patane.Commands.CommandInfo;
import com.Patane.util.general.Messenger;
@CommandInfo(
	name = "edit item attributes",
	aliases = {"attribute", "attrib"},
	description = "Edits an items attribute modifiers.",
	usage = "/brewery edit item attributes [add|remove]",
	hideCommand = true
)
public class itemEditItemAttributes extends itemEditItem {

	@Override
	public boolean execute(CommandSender sender, String[] args, Object... objects) {		
		CommandPackage child = BrCommandHandler.getChildPackage(this.getClass(), args[0]);
		if(child == null) {
			Messenger.send(sender, "&7"+args[0]+" &cis an invalid argument.");
			return false;
		}
		CommandHandler.grabInstance().handleCommand(sender, child.command(), args);
		return true;
	}
}
