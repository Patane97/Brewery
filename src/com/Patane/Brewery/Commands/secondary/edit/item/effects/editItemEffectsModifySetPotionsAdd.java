package com.Patane.Brewery.Commands.secondary.edit.item.effects;

import java.util.Arrays;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.Patane.Brewery.CustomEffects.BrEffect;
import com.Patane.Brewery.CustomItems.BrItem;
import com.Patane.Commands.CommandInfo;
import com.Patane.util.general.Chat;
import com.Patane.util.general.Messenger;
import com.Patane.util.general.StringsUtil;

import net.md_5.bungee.api.chat.TextComponent;
@CommandInfo(
	name = "edit item effects modify set potions add",
	description = "Adds a Potion to an Effect for a Brewery Item. These changes are seperate from the original Effect.",
	usage = "/brewery edit item <item name> effects modify <effect name> set potions add [type] <duration> <amplifier> (ambient) (particles) (icon)",
	maxArgs = 6
)
public class editItemEffectsModifySetPotionsAdd extends editItemEffectsModifySetPotions {

	@Override
	public boolean execute(CommandSender sender, String[] args, Object... objects) {
		
		// Checking potion effect type is given
		if(args.length < 1) {
			Messenger.send(sender, "&ePlease specify a potion effect type.");
			return true;
		}
		
		PotionEffectType potionEffectType = null;
		
		// Getting the potion effect type (can be null)
		potionEffectType = PotionEffectType.getByName(StringsUtil.normalize(args[0]));
		
		// null means an invalid effect type was given
		if(potionEffectType == null) {
			Messenger.send(sender, "&7"+args[0]+" &cis not a valid potion effect type.");
			return true;
		}
		
		// Checking duration is given
		if(args.length < 2) {
			Messenger.send(sender, "&ePlease provide a duration. This must be a rounded, positive number.");
			return true;
		}

		Integer duration = null;
		try {
			duration = Integer.parseInt(args[1]);
		} catch (NumberFormatException e) {
			Messenger.send(sender, "&7"+args[1]+" &cis not a valid duration. It must be a rounded, positive number.");
			return true;
		}
		
		// Checking its positive
		if(duration <= 0) {
			Messenger.send(sender, "&cDuration must be a rounded, positive number.");
			return true;
		}
		
		// Checking amplifier is given
		if(args.length < 2) {
			Messenger.send(sender, "&ePlease provide an amplifier. This must be a rounded, positive number or 0.");
			return true;
		}

		Integer amplifier = null;
		try {
			amplifier = Integer.parseInt(args[2]);
		} catch (NumberFormatException e) {
			Messenger.send(sender, "&7"+args[2]+" &cis not a valid amplifier. It must be a rounded, positive number or 0.");
			return true;
		}
		
		// Checking its positive
		if(amplifier < 0) {
			Messenger.send(sender, "&cAmplifier must be a rounded, positive number or 0.");
			return true;
		}
		
		Boolean ambient = null;
		// If ambient isnt given, defaults to true
		if(args.length < 4) {
			ambient = true;
		} else {
			try {
				ambient = StringsUtil.parseBoolean(args[3]);
			} catch (IllegalArgumentException e) {
				Messenger.send(sender, "&7"+args[3]+" &cis not a valid ambient value. It must be either true or false.");
				return true;
			}
		}

		Boolean particles = null;
		// If particles isnt given, defaults to true
		if(args.length < 5) {
			particles = true;
		} else {
			try {
				particles = StringsUtil.parseBoolean(args[4]);
			} catch (IllegalArgumentException e) {
				Messenger.send(sender, "&7"+args[4]+" &cis not a valid particles value. It must be either true or false.");
				return true;
			}
		}

		Boolean icon = null;
		// If icon isnt given, defaults to true
		if(args.length < 6) {
			icon = true;
		} else {
			try {
				icon = StringsUtil.parseBoolean(args[5]);
			} catch (IllegalArgumentException e) {
				Messenger.send(sender, "&7"+args[5]+" &cis not a valid icon value. It must be either true or false.");
				return true;
			}
		}
		
		// Creating the potion effect
		PotionEffect potionEffect = new PotionEffect(potionEffectType, duration, amplifier, ambient, particles, icon);
		
		// Grabbing item
		BrItem item = (BrItem) objects[0];
		
		// Grabbing effect
		BrEffect effect = (BrEffect) objects[1];
		
		String successMsg = "&aAdded new Potion Effect to &7"+item.getName()+"&a's instance of &7"+effect.getName()+"&a. Hover to view the details!";
		
		String successHoverText = generateEditingTitle(item, effect)
								+ (effect.hasPotions() ? StringsUtil.toChatString(0, true, s -> "&2"+s[0]+"&2: &7"+s[1], effect.getPotions().toArray(new PotionEffect[0])) : "");
		
		// If the type AND amplifier are equal, we do not want to duplicate with the same amp. Therefore, do nothing and message appropriately
		for(PotionEffect currentPotionEffect : effect.getPotions()) {
			if(currentPotionEffect.getType().equals(potionEffect.getType())) {
				if(currentPotionEffect.getAmplifier() == potionEffect.getAmplifier()) {
					Messenger.send(sender, StringsUtil.hoverText("&7"+item.getName()+"&e's instance of &7"+effect.getName()+"&e already contains a potion effect with that type and amplifier. Hover to view all potion effects on this effect!"
							, successHoverText));
					return true;
				}
			}
		}
		// Add the new potion to end of potion effect list
		successHoverText += String.format("\n%s", Chat.add(StringsUtil.toChatString(0, true, s -> "&2"+s[0]+"&2: &7"+s[1], potionEffect), ChatColor.BOLD));
		
		// Save potion effect to effect
		effect.addPotion(potionEffect);
		
		// Save the Item to the YML. This will also save the instance of the effect to the item
		BrItem.YML().save(item);

		// Allows the user to view the details on hover
		TextComponent successMsgComponent = StringsUtil.hoverText(successMsg, successHoverText);
		
		Messenger.send(sender, successMsgComponent);
		return true;
		
	}
	
	@Override
	public List<String> tabComplete(CommandSender sender, String[] args, Object... objects) {
		switch(args.length) {
			case 1: return Arrays.asList(StringsUtil.getPotionTypeStrings());
			case 2: return Arrays.asList("<duration>");
			case 3: return Arrays.asList("<amplifier>");
			case 4: return Arrays.asList("(ambient)");
			case 5: return Arrays.asList("(particles)");
			case 6: return Arrays.asList("(icon)");
		}
		return Arrays.asList();
	}
}
