package com.Patane.Brewery.commands.all;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import com.Patane.Brewery.Brewery;
import com.Patane.Brewery.commands.BrCommand;
import com.Patane.Brewery.commands.CommandInfo;

@CommandInfo(
	name = "give",
	description = "Gives player test potion",
	usage = "/br give",
	permission = ""
)
public class giveCommand implements BrCommand{

	@Override
	public boolean execute(Plugin plugin, Player sender, String[] args) {
		ItemStack item = Brewery.getCustomPotions().getAllItems().get(0).getItem();
		sender.getInventory().addItem(item);
		return true;
	}
}
