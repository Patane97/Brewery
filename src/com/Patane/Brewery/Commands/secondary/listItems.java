package com.Patane.Brewery.Commands.secondary;

import org.bukkit.command.CommandSender;

import com.Patane.Brewery.Brewery;
import com.Patane.Brewery.Commands.primary.listCommand;
import com.Patane.Brewery.CustomItems.BrItem;
import com.Patane.Commands.CommandInfo;
import com.Patane.util.general.Chat;
import com.Patane.util.general.Messenger;
import com.Patane.util.general.StringsUtil;

import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
@CommandInfo(
	name = "list items",
	aliases = {"item"},
	description = "Lists each registered Item.",
	usage = "/br list items"
)
public class listItems extends listCommand{

	@Override
	public boolean execute(CommandSender sender, String[] args, Object... objects) {
		Messenger.send(sender, StringsUtil.generateChatTitle("Registered Items"));
		for(BrItem item : Brewery.getItemCollection().getAllItems()) {			
			TextComponent commandText = new TextComponent(Chat.translate(" &a> &7"+item.getName()));

			commandText.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(item.hoverDetails()).create()));
			
			commandText.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/br info item "+item.getName()));
			
			Messenger.sendRaw(sender, commandText);
			
		}
		return true;
	}

}
