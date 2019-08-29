package com.Patane.Brewery.Commands.secondary.editing;

import java.util.Arrays;
import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.Patane.Brewery.CustomEffects.BrEffect;
import com.Patane.Brewery.CustomItems.BrItem;
import com.Patane.Brewery.Editing.EditSession;
import com.Patane.Commands.CommandHandler.CommandPackage;
import com.Patane.Commands.CommandInfo;
import com.Patane.util.general.Messenger;
import com.Patane.util.general.StringsUtil;
@CommandInfo(
	name = "edit effects edit <effectname> set potions add",
	description = "Adds a Potion Effect for an Effect that is attached to a Brewery Item.",
	usage = "/brewery edit effects edit <effect name> set potions add [type] <duration> <amplifier> (ambient) (particles) (icon)",
	maxArgs = 6
)
public class itemEditEffectsEditSetPotionsAdd extends itemEditEffectsEditSetPotions {

	@Override
	public boolean execute(CommandSender sender, String[] args, Object... objects) {
		
		PotionEffectType type = null;
		type = PotionEffectType.getByName(StringsUtil.normalize(args[0]));
		if(type == null) {
			Messenger.send(sender, "&7"+args[0]+" &c is an invalid Potion Effect Type.");
			return true;
		}
		
		if(args.length < 2) {
			Messenger.send(sender, "&cPlease provide a duration.");
			return false;
		}
		
		Integer duration = null;
		try {
			duration = Integer.parseInt(args[1]);
		} catch (NumberFormatException e) {
			Messenger.send(sender, "&7"+args[1]+" &c is an invalid duration.");
			return true;
		}
		
		if(args.length < 3) {
			Messenger.send(sender, "&cPlease provide an amplifier.");
			return false;
		}
		
		Integer amplifier = null;
		try {
			amplifier = Integer.parseInt(args[2]);
		} catch (NumberFormatException e) {
			Messenger.send(sender, "&7"+args[2]+" &c is an invalid amplifier.");
			return true;
		}

		Boolean ambient = null;
		if(args.length < 4) {
			ambient = true;
		} else {
			try {
				ambient = StringsUtil.parseBoolean(args[3]);
			} catch (IllegalArgumentException e) {
				Messenger.send(sender, "&7"+args[3]+" &c is an invalid ambient value.");
				return true;
			}
		}

		Boolean particles = null;
		if(args.length < 5) {
			particles = true;
		} else {
			try {
				particles = StringsUtil.parseBoolean(args[4]);
			} catch (IllegalArgumentException e) {
				Messenger.send(sender, "&7"+args[4]+" &c is an invalid particles value.");
				return true;
			}
		}

		Boolean icon = null;
		if(args.length < 6) {
			icon = true;
		} else {
			try {
				icon = StringsUtil.parseBoolean(args[5]);
			} catch (IllegalArgumentException e) {
				Messenger.send(sender, "&7"+args[5]+" &c is an invalid icon value.");
				return true;
			}
		}
		
		PotionEffect potion_effect = new PotionEffect(type, duration, amplifier, ambient, particles, icon);
		
		BrEffect brEffect = (BrEffect) objects[0];
		
		brEffect.addPotion(potion_effect);
		
		BrItem brItem = (BrItem) EditSession.get(sender.getName());
		
		BrItem.YML().save(brItem);
		
		Messenger.send(sender, "&aAdded &7"+potion_effect.getType().getName()+" &aPotion Effect to Effect.");
		return true;
	}
	
	@Override
	public List<String> tabComplete(CommandSender sender, String[] args, CommandPackage thisPackage) {
		switch(args.length) {
			case 8: return Arrays.asList(StringsUtil.getPotionTypeStrings());
			case 9: return Arrays.asList("<duration>");
			case 10: return Arrays.asList("<amplifier>");
			case 11: return Arrays.asList("(ambient)");
			case 12: return Arrays.asList("(particles)");
			default: return Arrays.asList("(icon)");
			
		}
	}
}
