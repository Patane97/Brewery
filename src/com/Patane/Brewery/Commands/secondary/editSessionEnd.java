package com.Patane.Brewery.commands.secondary;

import org.bukkit.command.CommandSender;

import com.Patane.Brewery.Editing.EditSession;
import com.Patane.Brewery.commands.primary.editSessionCommand;
import com.Patane.Commands.CommandInfo;
import com.Patane.util.general.Messenger;

@CommandInfo(
	name = "editsession end",
	aliases = {"finish", "complete"},
	description = "Ends the currently active editing session.",
	usage = "/brewery editsession end"
)
public class editSessionEnd extends editSessionCommand {
	
	@Override
	public boolean execute(CommandSender sender, String[] args, Object... objects) {
		String senderName = sender.getName();
		// This is different because /br edit item can be used in two scenarios: When activating an editing session OR when editing the itemstack of an item.
		if(!EditSession.active(senderName)) {
			Messenger.send(sender, "&cYou are currently not in an editing session.");
			return true;
		}
		EditSession.end(senderName);
		return true;
	}
}
