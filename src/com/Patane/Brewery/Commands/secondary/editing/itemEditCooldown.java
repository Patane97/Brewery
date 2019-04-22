package com.Patane.Brewery.Commands.secondary.editing;

import org.bukkit.command.CommandSender;

import com.Patane.Brewery.Commands.primary.editCommand;
import com.Patane.Brewery.CustomItems.BrItem;
import com.Patane.Brewery.Editing.EditSession;
import com.Patane.Brewery.Editing.EditingInfo;
import com.Patane.Commands.CommandInfo;
import com.Patane.util.general.Messenger;
@CommandInfo(
	name = "edit cooldown",
	description = "Sets the cooldown for a Brewery Item in seconds. Setting to 0 removes the cooldown.",
	usage = "/br edit cooldown <amount>"
)
@EditingInfo(type = BrItem.class)
public class itemEditCooldown extends editCommand {

	@Override
	public boolean execute(CommandSender sender, String[] args, Object... objects) {
		
		Float cooldown = null;
		try {
			cooldown = Float.parseFloat(args[0]);
		} catch (NumberFormatException e) {
			Messenger.send(sender, "&7"+args[0]+" &cis an invalid amount.");
			return true;
		}
		
		if(cooldown < 0) {
			Messenger.send(sender, "&cCooldown must be a positive number.");
			return true;
		}

		
		BrItem brItem = (BrItem) EditSession.get(sender.getName());

		String successMsg = "&aSet Item Cooldown to &7"+cooldown+"&a second"+(cooldown==1?"":"s")+".";

		if(cooldown == 0) {
			cooldown = null;
			successMsg = "&aRemoved Item Cooldown!";
		}
		brItem.setCooldown(cooldown);
		
		BrItem.YML().save(brItem);
		
		Messenger.send(sender, successMsg);
		return true;
	}
}
