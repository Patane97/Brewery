package com.Patane.Brewery.NEWcommands.secondary.editing;

import java.util.List;

import org.bukkit.command.CommandSender;

import com.Patane.Commands.CommandInfo;

@CommandInfo(
	name = "edit item effects",
	description = "Edits the Effects of a Brewery Item. Editing these Effects does not change the original Effect if there is any.",
	usage = "/brewery edit item <item name> effects [add|remove|set]"
)
public class editItemEffects extends editItem {
	
	@Override
	public boolean execute(CommandSender sender, String[] args, Object... objects) {
		return this.gotoChild(0, sender, args, objects);
	}
	
	@Override
	public List<String> tabComplete(CommandSender sender, String[] args, Object... objects) {
		return this.tabCompleteCore(sender, args, objects);
	}
}
