package com.Patane.Brewery.Commands.secondary;

import java.util.Arrays;
import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.libs.org.apache.commons.lang3.ArrayUtils;

import com.Patane.Brewery.Brewery;
import com.Patane.Brewery.Commands.primary.giveCommand;
import com.Patane.Brewery.Commands.primary.infoCommand;
import com.Patane.Brewery.CustomItems.BrItem;
import com.Patane.Commands.CommandHandler;
import com.Patane.Commands.CommandInfo;
import com.Patane.util.general.Chat;
import com.Patane.util.general.Messenger;
import com.Patane.util.general.StringsUtil;
import com.Patane.util.ingame.Commands;

import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
@CommandInfo(
	name = "info item",
	description = "Provides detailed information for a Brewery Item.",
	usage = "/brewery info item <item name>",
	maxArgs = 1
)
public class infoItem extends infoCommand{

	@Override
	public boolean execute(CommandSender sender, String[] args, Object... objects) {

		// Checking if item name is given
		if(args.length < 1) {
			Messenger.send(sender, "&cPlease specify an item name.");
			return true;
		}
		
		String itemName = Commands.combineArgs(args);
		
		// Find Item
		BrItem item = Brewery.getItemCollection().getItem(itemName);
		
		// Check if Item exists
		if(item == null) {
			Messenger.send(sender, "&cThere is no item with the name &7"+itemName+"&c.");
			return true;
		}
		TextComponent[] titleComponent = new TextComponent[] {StringsUtil.createTextComponent(StringsUtil.generateChatTitle("Item Information")
				+ "\n&f Hover to view more information about each element and click the item name to get it!\n\n")};
		
		
		TextComponent[] textComponents = item.toChatHover(0, true);
		
		
		textComponents = ArrayUtils.addAll(titleComponent, textComponents);
		
		// textComponents[1] should be the Item Title!
		
		textComponents[1].setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(Chat.translate("&7Click here to give yourself this item!")).create()));
		textComponents[1].setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, CommandHandler.getPackage(giveCommand.class).buildString(sender.getName(), item.getName())));
		
		Messenger.send(sender, textComponents);
		return true;
	}
	
	@Override
	public List<String> tabComplete(CommandSender sender, String[] args, Object... objects) {
		switch(args.length) {
			case 1: return StringsUtil.encase(Brewery.getItemCollection().getAllIDs(), "'", "'");
		}
		return Arrays.asList();
	}
}
