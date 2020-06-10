package com.Patane.Brewery.Commands.secondary.edit.effect;

import java.util.List;

import org.bukkit.command.CommandSender;

import com.Patane.Brewery.Commands.secondary.edit.editEffect;
import com.Patane.Commands.CommandInfo;
import com.Patane.util.general.Messenger;
@CommandInfo(
	name = "edit effect set",
	aliases = {"change", "edit", "modify"},
	description = "Sets or changes the property of an original Effect.",
	usage = "/brewery edit effect <effect name> set [property]"
)
public class editEffectSet extends editEffect {

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
