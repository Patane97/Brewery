package com.Patane.Brewery.Commands.secondary;

import java.util.Arrays;
import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.libs.org.apache.commons.lang3.ArrayUtils;

import com.Patane.Brewery.Brewery;
import com.Patane.Brewery.Commands.primary.infoCommand;
import com.Patane.Brewery.CustomEffects.BrEffect;
import com.Patane.Commands.CommandInfo;
import com.Patane.util.general.Messenger;
import com.Patane.util.general.StringsUtil;
import com.Patane.util.ingame.Commands;

import net.md_5.bungee.api.chat.TextComponent;
@CommandInfo(
	name = "info effect",
	description = "Provides detailed information for a Brewery Effect.",
	usage = "/brewery info effect <effect name>",
	maxArgs = 1
)
public class infoEffect extends infoCommand{

	@Override
	public boolean execute(CommandSender sender, String[] args, Object... objects) {

		// Checking if effect name is given
		if(args.length < 1) {
			Messenger.send(sender, "&cPlease specify an effect name.");
			return true;
		}
		
		String effectName = Commands.combineArgs(args);
		
		// Find Effect
		BrEffect effect = Brewery.getEffectCollection().getItem(effectName);
		
		// Check if Effect exists
		if(effect == null) {
			Messenger.send(sender, "&cThere is no effect with the name &7"+effectName+"&c.");
			return true;
		}
		TextComponent[] titleComponent = new TextComponent[] {StringsUtil.createTextComponent(StringsUtil.generateChatTitle("Effect Information")
				+ "\n&f Hover to view more information about each element.\n\n")};
		
		
		TextComponent[] textComponents = effect.toChatHover(0, true);
		
		
		textComponents = ArrayUtils.addAll(titleComponent, textComponents);
		
		Messenger.send(sender, textComponents);
		return true;
	}
	
	@Override
	public List<String> tabComplete(CommandSender sender, String[] args, Object... objects) {
		switch(args.length) {
			case 1: return StringsUtil.encase(Brewery.getEffectCollection().getAllIDs(), "'", "'");
		}
		return Arrays.asList();
	}
}
