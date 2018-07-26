package com.Patane.Brewery.Commands.all;

import org.bukkit.command.CommandSender;

import com.Patane.Brewery.Brewery;
import com.Patane.Commands.CommandInfo;
import com.Patane.Commands.PatCommand;
import com.Patane.util.general.Messenger;
import com.Patane.util.general.StringsUtil;

@CommandInfo(
	name = "help",
	aliases = {"?"},
	description = "Lists each available command for Brewery.",
	usage = "/br help|?",
	permission = "brewery.help"
)
public class helpCommand implements PatCommand {

	@Override
	public boolean execute(CommandSender sender, String[] args) {
		String listString = StringsUtil.generateChatTitle("Brewery Help");
		for(PatCommand cmd : Brewery.getCommandHandler().allCommands()) {
			CommandInfo cmdInfo = cmd.getClass().getAnnotation(CommandInfo.class);
			listString = listString + "\n&a  "+cmdInfo.usage()+"\n&7   > "+cmdInfo.description();
		}
		Messenger.send(sender, listString);
		return true;
	}

}
