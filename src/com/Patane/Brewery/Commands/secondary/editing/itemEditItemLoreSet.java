package com.Patane.Brewery.Commands.secondary.editing;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.inventory.ItemStack;

import com.Patane.Brewery.CustomItems.BrItem;
import com.Patane.Brewery.Editing.EditSession;
import com.Patane.Commands.CommandInfo;
import com.Patane.util.general.Chat;
import com.Patane.util.general.Messenger;
import com.Patane.util.ingame.Commands;
import com.Patane.util.ingame.ItemsUtil;
@CommandInfo(
	name = "edit item lore set",
	aliases = {"add"},
	description = "Sets a specific line of an items lore.",
	usage = "/br edit item lore set <line> <text>",
	hideCommand = true
)
public class itemEditItemLoreSet extends itemEditItemLore {

	@Override
	public boolean execute(CommandSender sender, String[] args, Object... objects) {
		
		Integer line = null;
		try {
			line = Integer.parseInt(args[0]);
		} catch (Exception e) {
			Messenger.send(sender, "&7"+args[0]+" &cis an invalid line number.");
			return true;
		}
		if(line <= 0) {
			Messenger.send(sender, "&cLine number must be above 0.");
			return true;
		}
		String text = Commands.combineArgs(Commands.grabArgs(args, 1, args.length));
		
		BrItem brItem = (BrItem) EditSession.get(sender.getName());
		ItemStack currentItem = brItem.getItemStack();
		
		List<String> lore = ItemsUtil.getLore(currentItem);
		if(lore == null)
			lore = new ArrayList<String>();
		if(line > lore.size()) {
			line = lore.size()+1;
			lore.add(Chat.translate(text));
		}
		else
			lore.set(line-1, Chat.translate(text));
		
		brItem.setItemStack(ItemsUtil.setLore(currentItem, lore));
		
		BrItem.YML().save(brItem);
		
		if(text.trim().length() == 0)
			Messenger.send(sender, "&aEmpty line of lore text set for line &7"+line+"&a.");
		else
			Messenger.send(sender, "&aLore text for line &7"+line+"&a set to the following: &7"+text);
		return true;
	}
}
