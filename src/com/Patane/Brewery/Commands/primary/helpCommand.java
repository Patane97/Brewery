package com.Patane.Brewery.Commands.primary;

import org.bukkit.command.CommandSender;

import com.Patane.Brewery.Brewery;
import com.Patane.Commands.CommandInfo;
import com.Patane.Commands.PatCommand;
import com.Patane.util.general.Chat;
import com.Patane.util.general.Messenger;
import com.Patane.util.general.StringsUtil;
import com.Patane.util.ingame.Commands;

import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;

@CommandInfo(
	name = "help",
	aliases = {"?"},
	description = "Lists each available command for Brewery.",
	usage = "/br help",
	permission = "brewery.help"
)
public class helpCommand implements PatCommand {

	@Override
	public boolean execute(CommandSender sender, String[] args) {
		Messenger.send(sender, StringsUtil.generateChatTitle("Brewery Commands"));
		for(PatCommand cmd : Brewery.getCommandHandler().allParentCommands()) {
			CommandInfo cmdInfo = PatCommand.grabInfo(cmd);
			
			TextComponent commandText = new TextComponent(Chat.translate(" &a> &7"+cmdInfo.usage()));
			
			commandText.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(Commands.hoverFormat(cmdInfo)).create()));
			
			commandText.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, cmdInfo.usage()));
			
			Messenger.sendRaw(sender, commandText);
			
		}
		return true;
	}

}
