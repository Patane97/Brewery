package com.Patane.Brewery.commands.secondary;

import java.util.Arrays;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.Patane.Brewery.Brewery;
import com.Patane.Brewery.CustomItems.BrItem;
import com.Patane.Brewery.CustomItems.BrItem.CustomType;
import com.Patane.Brewery.commands.primary.createCommand;
import com.Patane.Commands.CommandHandler.CommandPackage;
import com.Patane.Commands.CommandInfo;
import com.Patane.util.general.Messenger;
import com.Patane.util.ingame.Commands;
@CommandInfo(
	name = "create item",
	description = "Creates a new item using the item held in hand.",
	usage = "/brewery create item <item name>",
	maxArgs = 1
)
public class createItem extends createCommand {

	@Override
	public boolean execute(CommandSender sender, String[] args, Object... objects) {
		if(!(sender instanceof Player)) {
			Messenger.send(sender, "&cYou must be a player to create a Brewery Item via commands.");
			return false;
		}
		Player player = (Player) sender;
		
		// Setting Itemstack
		ItemStack item = player.getInventory().getItemInMainHand();
		
		if(item == null || item.getType() == Material.AIR) {
			Messenger.send(player, "&cYou must be holding an item.");
			return false;
		}
		
		if(args.length == 0 || args[0] == null) {
			Messenger.send(player, "&cPlease specify a name for your item.");
			return false;
		}
		
		// Setting name
		String name = Commands.combineArgs(args);
		
		if(name.contains(".") || name.contains("/")) {
			Messenger.send(player, "&cItem names cannot contain the following characters: &7'.', '/'");
			return false;
		}
		
		if(Brewery.getItemCollection().hasItem(name)) {
			Messenger.send(player, "&7"+name+" &cis already the name of a brewery item!");
			return false;
		}
		try {
			BrItem brItem = new BrItem(name, CustomType.HITTABLE, item, null, null);
			
			// Attempt to save the item to YML. If this gives us exceptions then we dont add the item to the collection
			BrItem.YML().save(brItem);
			
			Brewery.getItemCollection().add(brItem);
			
			Messenger.send(player, "&aCreated item &7"+ brItem.getName() +"&a.");
			// Maybe print a more detailed 'create' message in console?
		} catch (Exception e) {
			Messenger.send(player, "&cFailed to create item due to the following error: \n &4&o" + e.getMessage());
			e.printStackTrace();
		}
		
		return true;
	}
	
	@Override
	public List<String> tabComplete(CommandSender sender, String[] args, CommandPackage thisPackage) {
		return Arrays.asList("<item name>");
	}
}
