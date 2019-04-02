package com.Patane.Brewery.Commands.primary;

import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.Patane.Commands.CommandInfo;
import com.Patane.Commands.PatCommand;
import com.Patane.util.general.Messenger;
import com.Patane.util.ingame.ItemEncoder;

@CommandInfo(
	name = "tags",
	description = "Adds or Checks a tag on the currently held item.",
	usage = "/br tags [name] [value]",
	permission = "brewery.tags"
)
public class tagCommand implements PatCommand {
	
	@Override
	public boolean execute(CommandSender sender, String[] args) {
		if(!(sender instanceof Player))
			return false;
		Player player = (Player) sender;
		ItemStack item = player.getInventory().getItemInMainHand();
		if(item == null || item.getType() == Material.AIR) {
			Messenger.send(player, "&cYou must be holding an item to add or check its tags.");
			return false;
		}
		if(args.length == 1) {
			Object value = ItemEncoder.getTag(item, args[0]);
			if(value == null) {
				Messenger.send(player, "&cThis item does not contain the tag &7"+args[0]);
				return true;
			}
			Messenger.send(player, "&aValue for tag &7"+args[0]+" &ais &7"+value.toString()+"&a.");
			
		}
		else if(args.length >= 2) {
			item = ItemEncoder.addTag(item, args[0], args[1]);
			player.getInventory().setItemInMainHand(item);
			Messenger.send(player, "&aAdding tag &7"+args[0]+" &awith value &7"+args[1]+" &ato item with name &7"+item.getItemMeta().getDisplayName()+"&a.");
		}
		return true;
	}
}
