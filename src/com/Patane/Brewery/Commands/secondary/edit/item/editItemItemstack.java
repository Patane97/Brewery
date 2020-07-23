package com.Patane.Brewery.Commands.secondary.edit.item;

import java.util.List;

import org.bukkit.command.CommandSender;

import com.Patane.Brewery.Commands.secondary.edit.editItem;
import com.Patane.Commands.CommandInfo;

@CommandInfo(
	name = "edit item item",
	description = "Edits the physical Minecraft Item for a Brewery Item.",
	usage = "/brewery edit item <item name> item [property]"
)
public class editItemItemstack extends editItem {
	
	@Override
	public boolean execute(CommandSender sender, String[] args, Object... objects) {

		return this.gotoChild(0, s -> "&7"+s+" &cis not a valid item property to edit.", sender, args, objects);
	}
	
	@Override
	public List<String> tabComplete(CommandSender sender, String[] args, Object... objects) {
		return this.tabCompleteCore(sender, args, objects);
	}	
}
