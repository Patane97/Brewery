package com.Patane.Brewery.NEWcommands.secondary.editing;

import java.util.List;

import org.bukkit.command.CommandSender;

import com.Patane.Commands.CommandInfo;
@CommandInfo(
	name = "edit item effects modify set potions",
	description = "Edits the Potions of an Effect for a Brewery Item. These changes are seperate from the original Effect.",
	usage = "/brewery edit item <item name> effects modify <effect name> set potions [add|remove]"
)
public class editItemEffectsModifySetPotions extends editItemEffectsModifySet {

	@Override
	public boolean execute(CommandSender sender, String[] args, Object... objects) {
		return this.gotoChild(0, sender, args, objects);
	}
	
	@Override
	public List<String> tabComplete(CommandSender sender, String[] args, Object... objects) {
		return tabCompleteCore(sender, args, objects);
		
	}
}