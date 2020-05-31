package com.Patane.Brewery.NEWcommands.secondary.editing;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.inventory.ItemStack;

import com.Patane.Brewery.CustomItems.BrItem;
import com.Patane.Commands.CommandInfo;
import com.Patane.util.general.Chat;
import com.Patane.util.general.Messenger;
import com.Patane.util.general.StringsUtil;
import com.Patane.util.ingame.Commands;
import com.Patane.util.ingame.ItemsUtil;

import net.md_5.bungee.api.chat.TextComponent;

@CommandInfo(
	name = "edit item item lore set",
	aliases = {"add"},
	description = "Sets a line for the Lore of a physical Minecraft Item for a Brewery Item.",
	usage = "/brewery edit item <item name> item lore set <line> <text>",
	maxArgs = 2,
	hideCommand = true
)
public class editItemItemstackLoreSet extends editItemItemstackLore {

	@Override
	public boolean execute(CommandSender sender, String[] args, Object... objects) {
		
		// Checking if line number is grabbable
		if(args.length < 1) {
			Messenger.send(sender, "&ePlease provide a line number. This must be a rounded, positive number.");
			return true;
		}
		Integer line = null;
		if(args[0].equalsIgnoreCase("above"))
			line = -2;
		else if(args[0].equalsIgnoreCase("below"))
			line = -1;
		else {
			try {
				line = Integer.parseInt(args[0]);
			} catch (Exception e) {
				Messenger.send(sender, "&7"+args[0]+" &cis not a valid line number. It must be a rounded, positive number.");
				return true;
			}
			if(line <= 0) {
				Messenger.send(sender, "&cLine number must be above 0.");
				return true;
			}
		}
		String text = Commands.combineArgs(Commands.grabArgs(args, 1, args.length));
		
		// Grabbing the brItem from objects
		BrItem brItem = (BrItem) objects[0];
		
		// Grabbing the itemstack from brItem
		ItemStack currentItem = brItem.getItemStack();
		
		// Grabbing the lore list
		List<String> lore = ItemsUtil.getLore(currentItem);
		
		// If there is no lore, create empty array to start it
		if(lore == null)
			lore = new ArrayList<String>();
		
		// If they want to add text at first element (above)
		if(line == -2)
			lore.add(0, Chat.translate(text));
		// If they want to add text at last element (below or line >= lore size)
		else if (line == -1 || line >= lore.size())
			lore.add(Chat.translate(text));
		// Otherwise, set specific line to text
		else
			lore.set(line-1, Chat.translate(text));
		
		// Save successmsg
		String successMsg = "&aUpdated lore for &7"+brItem.getName()+"&a. Hover to view the item!";
		
		// Save updated itemstack to item
		brItem.setItemStack(ItemsUtil.setLore(currentItem, lore));
		
		// Allows the user to see the entire updated item on hover
		TextComponent successMsgComponent = StringsUtil.hoverItem(successMsg, brItem.getItemStack());
		
		// Save YML
		BrItem.YML().save(brItem);
		
		// Send successmsg
		Messenger.send(sender, successMsgComponent);
		return true;
	}
	
	@Override
	public List<String> tabComplete(CommandSender sender, String[] args, Object... objects) {
		BrItem brItem = (BrItem) objects[0];
		if(brItem == null)
			return Arrays.asList();
		
		switch(args.length) {
			case 1:
				// Saves current lore for later use
				List<String> lore = ItemsUtil.getLore(brItem.getItemStack());
				// If its empty, just suggest line 1 to start
				if(lore == null || lore.isEmpty())
					return Arrays.asList("1");
				else {
					// Create a list showing all available line numbers along with 'Above' and 'Below'
					List<String> lines = new ArrayList<String>();
					for(int i=1 ; i<lore.size() ; i++)
						lines.add(Integer.toString(i));
					lines.add("Above");
					lines.add("Below");
					return lines;
				}
			case 2: return Arrays.asList("<text>");
		}
		return Arrays.asList();
	}
}
