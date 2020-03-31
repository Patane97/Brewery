package com.Patane.Brewery.commands.secondary;

import org.bukkit.command.CommandSender;

import com.Patane.Brewery.Brewery;
import com.Patane.Brewery.CustomEffects.BrEffect;
import com.Patane.Brewery.commands.primary.listCommand;
import com.Patane.Commands.CommandInfo;
import com.Patane.util.general.Chat;
import com.Patane.util.general.Messenger;
import com.Patane.util.general.StringsUtil;

import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
@CommandInfo(
	name = "list effects",
	aliases = {"effect"},
	description = "Lists each registered Effect.",
	usage = "/brewery list effects"
)
public class listEffects extends listCommand{

	@Override
	public boolean execute(CommandSender sender, String[] args, Object... objects) {
		Messenger.send(sender, StringsUtil.generateChatTitle("Registered Effects"));
		for(BrEffect effect : Brewery.getEffectCollection().getAllItems()) {
			TextComponent commandText = new TextComponent(Chat.translate(" &a> &7"+effect.getName()));

			commandText.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(effect.hoverDetails()).create()));
			
			commandText.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/br info effect "+effect.getName()));
			
			Messenger.sendRaw(sender, commandText);
		}
		return true;
	}
}
