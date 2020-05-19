package com.Patane.Brewery.NEWcommands.secondary;

import java.util.Arrays;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.Patane.Brewery.Brewery;
import com.Patane.Brewery.Commands.primary.createCommand;
import com.Patane.Brewery.CustomItems.BrItem;
import com.Patane.Brewery.CustomItems.BrItem.CustomType;
import com.Patane.Commands.CommandHandler.CommandPackage;
import com.Patane.Commands.CommandInfo;
import com.Patane.util.general.Messenger;
import com.Patane.util.general.StringsUtil;
import com.Patane.util.ingame.Commands;

import net.md_5.bungee.api.chat.TextComponent;
@CommandInfo(
	name = "create item",
	description = "Creates a new item with default values using the item held in hand.",
	usage = "/brewery create item <item name>",
	maxArgs = 1
)
public class createItem extends createCommand {

	@Override
	public boolean execute(CommandSender sender, String[] args, Object... objects) {
		
		// This command required a player holding an item, thus cannot be done if youre not a player
		if(!(sender instanceof Player)) {
			Messenger.send(sender, "You must be a player to create a Brewery Item via commands.");
			return true;
		}
		
		// Saving the player
		Player player = (Player) sender;
		
		// Saving the itemstack
		ItemStack itemStack = player.getInventory().getItemInMainHand();
		
		// Making sure they are holding an item
		if(itemStack == null || itemStack.getType() == Material.AIR) {
			Messenger.send(player, "&ePlease hold an item you wish to create with.");
			return true;
		}
		
		// Checking item name is given
		if(args.length < 1) {
			Messenger.send(player, "&cPlease specify a name for the item.");
			return true;
		}
		
		// Setting name
		String itemName = Commands.combineArgs(args);

		// *** Not implemented yet
		// Storing these as its used multiple times and looks messy :3
//		LambdaStrings title = s -> "&f&l"+s[0];
//		LambdaStrings layout = s -> "&2"+s[0]+": &7"+s[1];
		
		// If an item with that name already exists, do nothing and message appropriately
		if(Brewery.getItemCollection().hasItem(itemName)) {
			// *** Need to add item 'toChatString' here when created
			Messenger.send(player, StringsUtil.hoverText("&eThere is already a Brewery Item named &7"+itemName+" &e. Hover to view its details!"
														,"&8Not implemented yet"));
			return true;
		}
		String successMsg = "&aCreated a new Brewery Item. Hover to view its details!";
		String successHoverText = null;
		
		try {
			BrItem item = new BrItem(itemName, CustomType.HITTABLE, itemStack, null, null);
			
			// Attempt to save the item to YML. If this gives us exceptions then we dont add the item to the collection
			BrItem.YML().save(item);
			
			// Add item to collection
			Brewery.getItemCollection().add(item);

			// Save new effect onto hover text
			// *** Not implemented yet
//			successHoverText = BrItem.manyToChatString(title, layout, false, item);
		} catch (Exception e) {
			// Save the error message onto successMsg (oh the irony)
			successMsg = "&cThere was an error with creating this item. Hover for error details!";
			// Save the exception message on hover. Dat shi ugly
			successHoverText = "&7"+e.getMessage();
			e.printStackTrace();
		}
		// Allows the user to view the details on hover
		TextComponent successMsgComponent = StringsUtil.hoverText(successMsg, successHoverText);
		
		// Send the hover message to sender
		Messenger.send(sender, successMsgComponent);
		return true;
	}
	
	@Override
	public List<String> tabComplete(CommandSender sender, String[] args, CommandPackage thisPackage) {
		return Arrays.asList("<item name>");
	}
}
