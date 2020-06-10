package com.Patane.Brewery.Commands.secondary.edit.effect;

import java.util.List;

import org.bukkit.command.CommandSender;

import com.Patane.Brewery.Commands.secondary.edit.editEffect;
import com.Patane.Commands.CommandInfo;
import com.Patane.util.general.Messenger;
@CommandInfo(
	name = "edit effect remove",
	aliases = {"rem", "delete", "del", "clear"},
	description = "Removes a property of an original Effect.",
	usage = "/brewery edit effect <effect name> remove [property]"
)
public class editEffectRemove extends editEffect {

	@Override
	public boolean execute(CommandSender sender, String[] args, Object... objects) {
		// Check if next argument/child command is provided
		if(args.length < 1) {
			Messenger.send(sender, "&ePlease specify a property to remove.");
			return true;
		}
		
		return this.gotoChild(0, s -> "&7"+s+" &cis not a valid property to remove.", sender, args, objects);
	}
	
	@Override
	public List<String> tabComplete(CommandSender sender, String[] args, Object... objects) {
		
		return this.tabCompleteCore(sender, args, objects);
	}
}
