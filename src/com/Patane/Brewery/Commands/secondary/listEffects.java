package com.Patane.Brewery.Commands.secondary;

import java.util.Arrays;
import java.util.List;

import org.bukkit.command.CommandSender;

import com.Patane.Brewery.Brewery;
import com.Patane.Brewery.Commands.primary.listCommand;
import com.Patane.Brewery.CustomEffects.BrEffect;
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
	name = "list effects",
	aliases = {"effect"},
	description = "Lists each registered Effect.",
	usage = "/brewery list effects"
)
public class listEffects extends listCommand {

	@Override
	public boolean execute(CommandSender sender, String[] args, Object... objects) {
		int itemCount = Brewery.getEffectCollection().getAllItems().size();
		TextComponent[] textComponents = new TextComponent[itemCount+1];
		
		textComponents[0] = StringsUtil.createTextComponent(StringsUtil.generateChatTitle("Listing Effects")
				+ "\n&f This is a list of all registered Brewery Effects. Hover for an overview of the Effect, or click on it for more details!"
				+ "\n&2Registered Effects:");
		int i=1;
		for(BrEffect effect : Brewery.getEffectCollection().getAllItems()) {
			TextComponent text = StringsUtil.createTextComponent("\n"+Chat.indent(1)+"&2> &7"+effect.getName());
			
			text.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(Chat.translate(effect.toChatString(0, false))).create()));
			
			text.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, CommandHandler.getPackage(infoEffect.class).buildString(effect.getName())));
			
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
