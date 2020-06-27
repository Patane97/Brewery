package com.Patane.Brewery.Commands.secondary;

import java.util.Arrays;
import java.util.List;

import org.bukkit.command.CommandSender;

import com.Patane.Brewery.Brewery;
import com.Patane.Brewery.Commands.primary.listCommand;
import com.Patane.Brewery.CustomItems.BrItem;
import com.Patane.Commands.CommandHandler;
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
	description = "Lists each registered Brewery Item.",
	usage = "/brewery list items"
)
public class listItems extends listCommand {

	@Override
	public boolean execute(CommandSender sender, String[] args, Object... objects) {
		int itemCount = Brewery.getItemCollection().getAllItems().size();
		TextComponent[] textComponents = new TextComponent[itemCount+1];
		
		textComponents[0] = StringsUtil.createTextComponent(StringsUtil.generateChatTitle("Listing Items")
				+ "\n&f This is a list of all registered Brewery Items. Hover for an overview of the Item, or click on it for more details!"
				+ "\n\n&2Registered Items:");
		int i=1;
		for(BrItem item : Brewery.getItemCollection().getAllItems()) {
			TextComponent text = StringsUtil.createTextComponent("\n"+Chat.indent(1)+"&2> &7"+item.getName());
			
			text.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(Chat.translate(item.toChatString(0, true))).create()));
			
			text.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, CommandHandler.getPackage(infoItem.class).buildString(item.getName())));
			
			textComponents[i] = text;
			i++;
		}
		
		Messenger.send(sender, textComponents);
		return true;
	}
	
	@Override
	public List<String> tabComplete(CommandSender sender, String[] args, Object... objects) {
		return Arrays.asList();
	}
}
