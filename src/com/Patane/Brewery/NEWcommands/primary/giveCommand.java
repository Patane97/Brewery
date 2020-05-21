package com.Patane.Brewery.NEWcommands.primary;

import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.Patane.Brewery.Brewery;
import com.Patane.Brewery.CustomItems.BrItem;
import com.Patane.Commands.CommandInfo;
import com.Patane.Commands.PatCommand;
import com.Patane.util.general.GeneralUtil;
import com.Patane.util.general.Messenger;
import com.Patane.util.general.StringsUtil;
import com.Patane.util.ingame.Commands;

import net.md_5.bungee.api.chat.TextComponent;

@CommandInfo(
	name = "give",
	description = "Gives a player a Brewery Item. Player must currently be on the server to receive the item.",
	usage = "/brewery give <player> <item name>",
	permission = "brewery.give",
	maxArgs = 2
)
public class giveCommand extends PatCommand {
	
	@Override
	public boolean execute(CommandSender sender, String[] args, Object... objects) {
		
		// Checks if player name is given
		if(args.length < 1) { 
			Messenger.send(sender, "&cPlease specify a players display name.");
			return true;
		}
		
		Player player = null;
		
		// Finding the target player.
		// If it is a player as sender, we only allow all VISIBLE online players to be selected.
		// When a player is invisible to the sender but still online, they will be treated as if they were offline
		// This is ignored if it is not a Player sending the command (ie. the Console can see all players)
		for(Player onlinePlayer : (sender instanceof Player ? GeneralUtil.getVisibleOnlinePlayers((Player) sender): GeneralUtil.getOnlinePlayers())) {
			if(args[0].equalsIgnoreCase(onlinePlayer.getPlayerListName())) {
				player = onlinePlayer;
				break;
			}
			Messenger.send(sender, "&7"+args[0]+" &e is currently not online or does not exist.");
			return true;
		}
		// Checks if item name is given
		if(args.length < 2) { 
			Messenger.send(sender, "&cPlease specify an item to give.");
			return true;
		}
		
		// Grab the item name from remaining arguments
		String itemName = Commands.combineArgs(args, 1, args.length);
		
		// If no effect with that name exists, do nothing and message appropriately
		if(!Brewery.getItemCollection().hasItem(itemName)) {
			Messenger.send(sender, StringsUtil.hoverText("&eThere is no Brewery Item named &7"+itemName+"&e. Hover to view all Items!"
														, "&8Not implemented yet"));
			return true;
		}
		
		// Grabbing the item
		BrItem item = Brewery.getItemCollection().getItem(itemName);

		String successMsg = "&aGiving &7"+player.getDisplayName()+"&a a &7"+item.getName()+"&a. Hover to view its details!";
		// *** Not implemented yet
//		String successHoverText = item.toChatString();
		String successHoverText = "&8Not implemented yet";
		
		// Give the player the itemStack
		player.getInventory().addItem(item.generateItem());
		
		// Allows the user to view the details on hover
		TextComponent successMsgComponent = StringsUtil.hoverText(successMsg, successHoverText);
		
		// Send the hover message to sender
		Messenger.send(sender, successMsgComponent);
		return true;
	}

	@Override
	public List<String> tabComplete(CommandSender sender, String[] args, Object... objects) {
		switch(args.length) {
			case 1: return (sender instanceof Player 
					? StringsUtil.getVisibleOnlinePlayerNames((Player) sender)
					: StringsUtil.getOnlinePlayerNames()) ;
			default: return StringsUtil.encase(Brewery.getItemCollection().getAllIDs(), "'", "'");
		}
	}
}
