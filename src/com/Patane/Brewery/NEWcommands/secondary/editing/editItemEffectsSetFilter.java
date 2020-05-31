package com.Patane.Brewery.NEWcommands.secondary.editing;

import java.util.List;

import org.bukkit.command.CommandSender;

import com.Patane.Commands.CommandInfo;
@CommandInfo(
	name = "edit item effects set filter",
	description = "Edits the Filter of an Effect for a Brewery Item. These changes are seperate from the original Effect.",
	usage = "/brewery edit item <item name> effects set <effect name> filter [add|remove]"
)
public class editItemEffectsSetFilter extends editItemEffectsSet {

	@Override
	public boolean execute(CommandSender sender, String[] args, Object... objects) {
		
		return this.gotoChild(0, sender, args, objects);
	}
	
	@Override
	public List<String> tabComplete(CommandSender sender, String[] args, Object... objects) {
		return tabCompleteCore(sender, args, objects);
		
	}
}
