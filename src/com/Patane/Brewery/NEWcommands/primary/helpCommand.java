package com.Patane.Brewery.NEWcommands.primary;

import org.bukkit.command.CommandSender;

import com.Patane.Brewery.Commands.BrCommandHandler;
import com.Patane.Commands.CommandHandler.CommandPackage;
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
	usage = "/brewery help",
	permission = "brewery.help"
)
public class helpCommand extends PatCommand {

	@Override
	public boolean execute(CommandSender sender, String[] args, Object... objects) {
		Messenger.send(sender, StringsUtil.generateChatTitle("Brewery Commands"));
		CommandPackage parentPackage;
		for(String parentName : BrCommandHandler.grabInstance().getParentCommandNames()) {
			parentPackage = BrCommandHandler.getPackage(parentName);	
			if(parentPackage.info().hideCommand())
				continue;
			
			TextComponent commandText = new TextComponent(Chat.translate(" &a> &7"+parentPackage.info().usage()));
			
			commandText.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(Commands.hoverFormat(parentPackage.info())).create()));
			
			commandText.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, parentPackage.info().usage()));
			
			Messenger.sendRaw(sender, commandText);
			
		}
		return true;
	}
}
