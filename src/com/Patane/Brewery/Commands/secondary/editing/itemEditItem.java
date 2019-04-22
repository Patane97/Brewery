package com.Patane.Brewery.Commands.secondary.editing;

import org.bukkit.command.CommandSender;

import com.Patane.Brewery.Commands.BrCommandHandler;
import com.Patane.Brewery.Commands.primary.editCommand;
import com.Patane.Brewery.CustomItems.BrItem;
import com.Patane.Brewery.Editing.EditingInfo;
import com.Patane.Commands.CommandHandler;
import com.Patane.Commands.CommandInfo;
import com.Patane.Commands.PatCommand;
import com.Patane.util.general.Messenger;
@CommandInfo(
	name = "edit item",
	description = "Edits the Itemstack for a Brewery Item.",
	usage = "/br edit item [something]"
)
@EditingInfo(type = BrItem.class)
public class itemEditItem extends editCommand {

	@Override
	public boolean execute(CommandSender sender, String[] args, Object... objects) {
		
		PatCommand child = BrCommandHandler.grabInstance().getChildCommand(this, args[0]);
		if(child == null) {
			Messenger.send(sender, "&7"+args[0]+" &cis not a valid edit item command.");
			return false;
		}
		CommandHandler.grabInstance().handleCommand(sender, child, args);
		return true;
	}
}
