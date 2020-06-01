package com.Patane.Brewery.NEWcommands.secondary.editing;

import java.util.List;

import org.bukkit.command.CommandSender;

import com.Patane.Commands.CommandInfo;
import com.Patane.util.general.Messenger;
@CommandInfo(
	name = "edit item effects modify set",
	aliases = {"change", "edit", "modify"},
	description = "Sets or changes the property of an Effect for a Brewery Item. These changes are seperate from the original Effect.",
	usage = "/brewery edit item <item name> effects modify <effect name> set [property]"
)
public class editItemEffectsModifySet extends editItemEffectsModify {

	@Override
	public boolean execute(CommandSender sender, String[] args, Object... objects) {
		// Check if next argument/child command is provided
		if(args.length < 1) {
			Messenger.send(sender, "&ePlease specify a property to edit.");
			return true;
		}
		
		return this.gotoChild(0, s -> "&7"+s+" &cis not a valid property to edit.", sender, args, objects);
	}
	
	@Override
	public List<String> tabComplete(CommandSender sender, String[] args, Object... objects) {
		
		return this.tabCompleteCore(sender, args, objects);
	}
}
