package com.Patane.Brewery.Commands.secondary;

import org.bukkit.command.CommandSender;

import com.Patane.Brewery.Brewery;
import com.Patane.Brewery.Commands.primary.listCommand;
import com.Patane.Brewery.CustomEffects.BrEffect;
import com.Patane.Commands.CommandInfo;
import com.Patane.Commands.PatCommand;
import com.Patane.util.general.Chat;
import com.Patane.util.general.Messenger;
import com.Patane.util.general.StringsUtil;

import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
@CommandInfo(
	name = "list effects",
	description = "Lists each registered Effect.",
	usage = "/br list effects",
	parent = listCommand.class
)
public class listEffects implements PatCommand{

	@Override
	public boolean execute(CommandSender sender, String[] args) {
		Messenger.send(sender, StringsUtil.generateChatTitle("Registered Effects"));
		for(BrEffect effect : Brewery.getEffectCollection().getAllItems()) {
			TextComponent commandText = new TextComponent(Chat.translate(" &a> &7"+effect.getName()));

			commandText.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(effectDetails(effect)).create()));
			
			commandText.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/br info effect "+effect.getName()));
			
			Messenger.sendRaw(sender, commandText);
		}
		return true;
	}
	public static String effectDetails(BrEffect effect) {
		return Chat.translate("&7"+effect.getName()
		+"\n&2Modifier: &a"+(effect.getModifier() != null ? effect.getModifier().name() : "&cUndefined")
		+"\n&2Trigger: &a"+(effect.getTrigger() != null ? effect.getTrigger().name() : "&cUndefined")
		+(effect.hasRadius() ? "\n&2Radius: &a"+effect.getRadius() : "")
		+(effect.hasTag() ? "\n&2Tag: &a"+effect.getTag().name : "")
		+(effect.hasParticle() ? "\n&2Particles: &aApplied" : "")
		+(effect.hasSound() ? "\n&2Sounds: &aApplied" : "")
		+(effect.hasPotions() ? "\n&2Potion Effects: &a"+effect.getPotions().size() : ""));
	}
}
