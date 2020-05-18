package com.Patane.Brewery.NEWcommands.secondary.editing;

import java.util.List;

import org.bukkit.command.CommandSender;

import com.Patane.Commands.CommandInfo;

@CommandInfo(
	name = "edit item item flags",
	aliases = {"flag"},
	description = "Edits the Flags of a physical Minecraft Item for a Brewery Item.",
	usage = "/brewery edit item <item name> item flags [add|remove]"
)
public class editItemItemstackFlags extends editItemItemstack {
	
	@Override
	public boolean execute(CommandSender sender, String[] args, Object... objects) {
		return this.gotoChild(0, sender, args, objects);
	}
	
	@Override
	public List<String> tabComplete(CommandSender sender, String[] args, Object... objects) {
		return this.tabCompleteCore(sender, args, objects);
	}
}
