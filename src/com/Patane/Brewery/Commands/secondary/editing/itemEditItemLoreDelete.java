package com.Patane.Brewery.Commands.secondary.editing;

import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.inventory.ItemStack;

import com.Patane.Brewery.CustomItems.BrItem;
import com.Patane.Brewery.Editing.EditSession;
import com.Patane.Commands.CommandInfo;
import com.Patane.util.general.Messenger;
import com.Patane.util.ingame.ItemsUtil;
@CommandInfo(
	name = "edit item lore delete",
	aliases = {"del", "remove", "rem"},
	description = "Deletes a specific line from an items lore.",
	usage = "/br edit item lore delete <line>",
	hideCommand = true
)
public class itemEditItemLoreDelete extends itemEditItemLore {

	@Override
	public boolean execute(CommandSender sender, String[] args, Object... objects) {
		if(args.length < 1) {
			Messenger.send(sender, "&cPlease specify a line number.");
			return false;
		}
		
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
		
		BrItem brItem = (BrItem) EditSession.get(sender.getName());
		ItemStack currentItem = brItem.getItemStack();
		
		List<String> lore = ItemsUtil.getLore(currentItem);
		
		if(lore == null || line > lore.size()) {
			Messenger.send(sender, "&cThere is no line &7"+line+"&c.");
			return true;
		}
		lore.remove(line-1);
		if(lore.isEmpty())
			brItem.setItemStack(ItemsUtil.removeLore(currentItem));
		else
			brItem.setItemStack(ItemsUtil.setLore(currentItem, lore));
		
		BrItem.YML().save(brItem);
		
		Messenger.send(sender, "&aLine &7"+line+" &aremoved from the item lore.");
		return true;
	}
}
