package com.Patane.Brewery.Commands.secondary.edit.effect;

import java.util.Arrays;
import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.potion.PotionEffect;

import com.Patane.Brewery.CustomEffects.BrEffect;
import com.Patane.Commands.CommandInfo;
import com.Patane.util.general.Chat;
import com.Patane.util.general.Messenger;
import com.Patane.util.general.StringsUtil;
import com.Patane.util.general.StringsUtil.LambdaStrings;

import net.md_5.bungee.api.chat.TextComponent;
@CommandInfo(
	name = "edit effect remove potions",
	description = "Removes the Potion Effects of an original Effect.",
	usage = "/brewery edit effect <effect name> remove potions"
)
public class editEffectRemovePotions extends editEffectRemove {

	@Override
	public boolean execute(CommandSender sender, String[] args, Object... objects) {
		
		// Find Effect
		BrEffect effect = (BrEffect) objects[0];
		
		// Default message assumes there is no previous potions, thus 'set' message is given
		String successMsg = String.format("&aRemoved all potion effects for &7%s&a. Hover to view the details!", effect.getName());
		
		String successHoverText = generateEditingTitle(effect);

		List<PotionEffect> currentPotions = effect.getPotions();
		
		if(currentPotions.isEmpty()) {
			Messenger.send(sender, StringsUtil.hoverText(String.format("&7%s&e already has no potion effects. Hover to view the effect!", effect.getName())
					, effect.toChatString(0, false)));
			return true;
		}
		// Uses original successMsg
		successHoverText += toChatStringCustom(s -> "&c"+Chat.replace(s[0], "&7", "&8&m")+"&c: &8&m"+Chat.replace(s[1], "&8&m")+"&r", currentPotions);
		
		// Removing the potions from effect
		effect.setPotions(null);

		// Save the Effect to YML
		BrEffect.YML().save(effect);
		
		// Updates all items that contain references to this effect. Doing this updates any relevant changes to the items effect.
		effect.updateReferences();
		
		// Allows the user to view the details on hover
		TextComponent successMsgComponent = StringsUtil.hoverText(successMsg, successHoverText);
		
		Messenger.send(sender, successMsgComponent);
		return true;
	}
	
	private String toChatStringCustom(LambdaStrings layout, List<PotionEffect> potionEffects) {
		return layout.build("Potion Effects", "") + "\n" + StringsUtil.toChatString(2, true, layout, potionEffects.toArray(new PotionEffect[0]));
	}
	
	@Override
	public List<String> tabComplete(CommandSender sender, String[] args, Object... objects) {
		return Arrays.asList();
	}
}
