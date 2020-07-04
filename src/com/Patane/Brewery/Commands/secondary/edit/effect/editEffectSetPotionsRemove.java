package com.Patane.Brewery.Commands.secondary.edit.effect;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.bukkit.command.CommandSender;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.Patane.Brewery.CustomEffects.BrEffect;
import com.Patane.Commands.CommandInfo;
import com.Patane.util.general.Chat;
import com.Patane.util.general.Messenger;
import com.Patane.util.general.StringsUtil;

import net.md_5.bungee.api.chat.TextComponent;
@CommandInfo(
	name = "edit effect set potions remove",
	aliases = {"rem", "delete", "del"},
	description = "Removes a Potion from an original Effect.",
	usage = "/brewery edit effect <effect name> set potions remove [type] <amplifier>",
	maxArgs = 2
)
public class editEffectSetPotionsRemove extends editEffectSetPotions {

	@Override
	public boolean execute(CommandSender sender, String[] args, Object... objects) {
		
		// Checking potion effect type is given
		if(args.length < 1) {
			Messenger.send(sender, "&ePlease specify an existing potion effect type.");
			return true;
		}
		
		PotionEffectType potionEffectType = null;
		
		// Getting the potion effect type (can be null)
		potionEffectType = PotionEffectType.getByName(StringsUtil.normalize(args[0]));
		
		// null means an invalid effect type was given
		if(potionEffectType == null) {
			Messenger.send(sender, String.format("&7%s &cis not a valid potion effect type.", args[0]));
			return true;
		}
		
		// Checking amplifier is given
		if(args.length < 2) {
			Messenger.send(sender, "&ePlease provide an existing amplifier.");
			return true;
		}

		Integer amplifier = null;
		try {
			amplifier = Integer.parseInt(args[1]);
		} catch (NumberFormatException e) {
			Messenger.send(sender, String.format("&7%s &cis not a valid amplifier. It must be a rounded, positive number or 0.", args[1]));
			return true;
		}
		
		// Checking its positive
		if(amplifier < 0) {
			Messenger.send(sender, "&cAmplifier must be a rounded, positive number or 0.");
			return true;
		}
		
		// Grabbing effect
		BrEffect effect = (BrEffect) objects[0];
		
		String successMsg = String.format("&aRemoved a Potion Effect from &7%s&a. Hover to view the details!", effect.getName());
		
		String successHoverText = generateEditingTitle(effect);
		
		PotionEffect potionEffect = null;
		
		// Looping through each potion effect and checking if the TYPE and AMPLIFIER are equal
		for(PotionEffect currentPotionEffect : effect.getPotions()) {
			if(currentPotionEffect.getType().equals(potionEffectType) && currentPotionEffect.getAmplifier() == amplifier) {
				potionEffect = currentPotionEffect;
				break;
			}
		}
		
		// If the above loop found no matching potions, it means there are none with that type+amplifier. Do nothing and message appropriately
		if(potionEffect == null) {
			Messenger.send(sender, StringsUtil.hoverText(String.format("&7%s&e does not contain a potion effect with that type and amplifier. Hover to view all potion effects on this effect!", effect.getName())
					, successHoverText + (effect.hasPotions() ? StringsUtil.toChatString(0, true, s -> "&2"+s[0]+"&2: &7"+s[1], effect.getPotions().toArray(new PotionEffect[0])) : "&8No Potion Effects!")));
			return true;
		}
		
		// Save potion effect to effect
		effect.removePotion(potionEffect);
		
		// Adding the removed potion effect with removed layout
		successHoverText += (effect.hasPotions() ? StringsUtil.toChatString(0, true, s -> "&2"+s[0]+"&2: &7"+s[1], effect.getPotions().toArray(new PotionEffect[0])) : "")
						  + String.format("\n%s", StringsUtil.toChatString(0, true, s -> "&c"+Chat.replace(s[0], "&7", "&8&m")+"&c: &8&m"+Chat.replace(s[1], "&8&m")+"&r", potionEffect));

		// Save the Effect to YML
		BrEffect.YML().save(effect);
		
		// Updates all items that contain references to this effect. Doing this updates any relevant changes to the items effect.
		effect.updateReferences();

		// Allows the user to view the details on hover
		TextComponent successMsgComponent = StringsUtil.hoverText(successMsg, successHoverText);
		
		Messenger.send(sender, successMsgComponent);
		return true;
		
	}
	
	@Override
	public List<String> tabComplete(CommandSender sender, String[] args, Object... objects) {
		// Grabbing effect
		BrEffect effect = (BrEffect) objects[0];
		
		switch(args.length) {
			case 1:
				// Sets do not allow duplicates
				Set<String> potionEffectTypes = new HashSet<String>();
				effect.getPotions().forEach(pe -> potionEffectTypes.add(pe.getType().getName()));
				return new ArrayList<String>(potionEffectTypes);
				
			case 2: 
				PotionEffectType potionEffectType = PotionEffectType.getByName(StringsUtil.normalize(args[0]));
				
				if(potionEffectType == null)
					return Arrays.asList();
				
				List<String> amplifierStrings = new ArrayList<String>();
				effect.getPotions().forEach(pe -> {
					if(pe.getType().equals(potionEffectType))
						amplifierStrings.add(Integer.toString(pe.getAmplifier()));
				});
				return amplifierStrings;
		}
		return Arrays.asList();
	}
}
