package com.Patane.Brewery.Commands.primary;

import java.util.Arrays;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.Patane.Brewery.Brewery;
import com.Patane.Brewery.CustomItems.BrItem;
import com.Patane.Commands.CommandInfo;
import com.Patane.Commands.PatCommand;
import com.Patane.util.general.Messenger;
import com.Patane.util.general.StringsUtil;

@CommandInfo(
	name = "give",
	description = "Gives a specified player a specified Brewery item.",
	usage = "/br give <player> <item name>",
	permission = "brewery.give"
)
public class giveCommand implements PatCommand {
	
	@Override
	public boolean execute(CommandSender sender, String[] args, Object... objects) {
		// Checks if there is more than 1 argument. If not, player name is missing.
		if(args.length < 1) { 
			Messenger.send(sender, "&cPlease specify a player name.");
			return false;
		}
		
		Player target = null;
		// Loops through each online player and checks if the first arg is their name.
		for(Player player : Brewery.getInstance().getServer().getOnlinePlayers()) {
			if(args[0].equalsIgnoreCase(player.getPlayerListName())) {
				target = player;
				break;
			}
		}
		// Checks if player was found currently on server.
		if(target == null) {
			Messenger.send(sender, "&7"+args[0]+" &cis not online or does not exist.");
			return false;
		}
		
		// Sets the item name to the rest of the args given.
		String itemName = StringsUtil.stringJoiner(Arrays.copyOfRange(args, 1, args.length), " ");
		
		// Checks if itemname cannot be constructed into a string for any reason.
		if(itemName == null || itemName.trim().isEmpty()) {
			Messenger.send(sender, "&cPlease specify an item name.");
			return false;
		}
		
		// Grabs the BrItem using the itemName.
		BrItem item = Brewery.getItemCollection().getItem(itemName);
		
		if(item == null) {
			Messenger.send(sender, "&cThere is no item with the name &7"+itemName+"&c.");
			return false;
		}
		
		target.getInventory().addItem(item.generateItem());
		Messenger.send(sender, "&aGiving &7"+target.getDisplayName()+"&a a &7"+item.getName()+"&a.");
			
		return true;
	}
}
