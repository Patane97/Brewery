package com.Patane.Brewery.Commands.secondary.edit.item.itemstack;

import java.util.List;

import org.bukkit.command.CommandSender;

import com.Patane.Brewery.Commands.secondary.edit.item.editItemItemstack;
import com.Patane.Commands.CommandInfo;

@CommandInfo(
	name = "edit item item enchantments",
	aliases = {"enchant", "ench"},
	description = "Edits the Enchantments of a physical Minecraft Item for a Brewery Item.",
	usage = "/brewery edit item <item name> item enchantments [add|remove]"
)
public class editItemItemstackEnchantments extends editItemItemstack {
	
	@Override
	public boolean execute(CommandSender sender, String[] args, Object... objects) {
		
		return this.gotoChild(0, sender, args, objects);
	}
	
	@Override
	public List<String> tabComplete(CommandSender sender, String[] args, Object... objects) {
		return this.tabCompleteCore(sender, args, objects);
	}
}
