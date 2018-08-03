package com.Patane.Brewery.Commands.primary;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.Patane.Commands.CommandInfo;
import com.Patane.Commands.PatCommand;
import com.Patane.util.general.Chat;
import com.Patane.util.general.Messenger;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
@CommandInfo(
	name = "cd",
	description = "",
	usage = "/br cd",
	permission = ""
)
public class cooldownCommand implements PatCommand{

	@Override
	public boolean execute(CommandSender sender, String[] args) {
		Player player = (Player) sender;
		TextComponent actionText = new TextComponent(Chat.translate(args[0]));
		Messenger.sendRaw(player, ChatMessageType.ACTION_BAR, actionText);
		return true;
	}
}
