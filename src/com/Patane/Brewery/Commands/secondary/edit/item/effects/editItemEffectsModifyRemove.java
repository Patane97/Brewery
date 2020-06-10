package com.Patane.Brewery.Commands.secondary.edit.item.effects;

import java.util.List;

import org.bukkit.command.CommandSender;

import com.Patane.Commands.CommandInfo;
import com.Patane.util.general.Messenger;
@CommandInfo(
	name = "edit item effects modify remove",
	aliases = {"rem", "delete", "del", "clear"},
	description = "Removes a property of an Effect for a Brewery Item. These changes are seperate from the original.",
	usage = "/brewery edit item <item name> effects modify <effect name> remove [property]"
)
public class editItemEffectsModifyRemove extends editItemEffectsModify {

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
