package com.Patane.Brewery.Commands.all;

import org.bukkit.command.CommandSender;

import com.Patane.Brewery.Brewery;
import com.Patane.Brewery.CustomItems.BrItem;
import com.Patane.Commands.CommandInfo;
import com.Patane.Commands.PatCommand;
import com.Patane.util.general.Messenger;
import com.Patane.util.general.StringsUtil;

@CommandInfo(
		name = "list",
		aliases = {"listall","all","items"},
		description = "Lists all available Brewery items.",
		usage = "/br list|listall|all|items",
		permission = "brewery.list"
	)
public class listCommand implements PatCommand {

	@Override
	public boolean execute(CommandSender sender, String[] args) {
		String listString = StringsUtil.generateChatTitle("Brewery Items");
		for(BrItem item : Brewery.getItemCollection().getAllItems()) {
			listString = listString + "\n&a  "+item.getItem().getItemMeta().getDisplayName();
			if(item.getItem().getItemMeta().hasLore())
				listString = listString +"\n&7    "+StringsUtil.stringJoiner(item.getItem().getItemMeta().getLore(), "\n&7    ");
		}
		Messenger.send(sender, listString);
		return true;
	}

}
