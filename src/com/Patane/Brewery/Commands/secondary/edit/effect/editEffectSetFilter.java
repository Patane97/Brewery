package com.Patane.Brewery.Commands.secondary.edit.effect;

import java.util.List;

import org.bukkit.command.CommandSender;

import com.Patane.Commands.CommandInfo;
@CommandInfo(
	name = "edit effect set filter",
	description = "Edits the Filter of an original Effect.",
	usage = "/brewery edit effect <effect name> set filter [add|remove]"
)
public class editEffectSetFilter extends editEffectSet {

	@Override
	public boolean execute(CommandSender sender, String[] args, Object... objects) {
		
		return this.gotoChild(0, sender, args, objects);
	}
	
	@Override
	public List<String> tabComplete(CommandSender sender, String[] args, Object... objects) {
		return tabCompleteCore(sender, args, objects);
		
	}
}
