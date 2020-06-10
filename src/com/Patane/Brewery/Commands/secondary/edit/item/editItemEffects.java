package com.Patane.Brewery.Commands.secondary.edit.item;

import java.util.List;

import org.bukkit.command.CommandSender;

import com.Patane.Brewery.Commands.secondary.edit.editItem;
import com.Patane.Commands.CommandInfo;
import com.Patane.util.collections.ChatCollectable;

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
	
	protected String generateEditingTitle(ChatCollectable item, ChatCollectable effect) {
		// This is a little more complicated than it needs to be, however it ensures the hover text doesnt get too long horizontally
		// It starts with item name limited at 15 characters (will add '...' if it gets too long)
		// It then either adds an arrow to a new line if both item and effect combined exceed 25 characters, or same line if under
		// Finally it prints the effect name limited at 15 characters
		return "&f&l"
				+ item.getNameLimited(15)
				+ ((item.getNameLimited(15)+effect.getNameLimited(15)).length() > 25 ? "\n" : "") + " &7&l\u2192 &f&l"
				+ effect.getNameLimited(15)+"\n";
	}
}
