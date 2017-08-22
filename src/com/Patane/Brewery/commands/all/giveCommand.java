package com.Patane.Brewery.commands.all;

import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import com.Patane.Brewery.Brewery;
import com.Patane.Brewery.Messenger;
import com.Patane.Brewery.CustomItems.BrItem;
import com.Patane.Brewery.commands.BrCommand;
import com.Patane.Brewery.commands.CommandHandler;
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
		String itemName = (args.length > 1 ? CommandHandler.argItemNameToString(args) : null);
		BrItem item = Brewery.getItemCollection().getItem(itemName);
		if(itemName == null || itemName.trim().isEmpty()){
			Messenger.send(sender, "&cPlease specify a potion name! &7/br give [name]");
			return false;
		}
		if(item == null){
			Messenger.send(sender, "&cThere is no item with the name &7"+itemName+"&c.");
			return false;
		}
		sender.getInventory().addItem(item.getItem());
		Messenger.send(sender, "&aGiving &7"+sender.getDisplayName()+"&a a &7"+item.getName()+"&a.");
			
		return true;
	}
}
