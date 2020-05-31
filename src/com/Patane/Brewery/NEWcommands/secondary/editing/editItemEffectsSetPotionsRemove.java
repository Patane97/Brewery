package com.Patane.Brewery.NEWcommands.secondary.editing;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
	name = "edit item effects set potions remove",
	aliases = {"rem", "delete", "del"},
	description = "Removes a Potion from an Effect for a Brewery Item. These changes are seperate from the original Effect.",
	usage = "/brewery edit item <item name> effects set <effect name> potions remove [type] <amplifier>",
	maxArgs = 2
)
public class editItemEffectsSetPotionsRemove extends editItemEffectsSetPotions {

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
			Messenger.send(sender, "&7"+args[0]+" &cis not a valid potion effect type.");
			return true;
		}
		
		// Checking amplifier is given
		if(args.length < 1) {
			Messenger.send(sender, "&ePlease provide an existing amplifier.");
			return true;
		}

		Integer amplifier = null;
		try {
			amplifier = Integer.parseInt(args[1]);
		} catch (NumberFormatException e) {
			Messenger.send(sender, "&7"+args[1]+" &cis not a valid amplifier. It must be a rounded, positive number or 0.");
			return true;
		}
		
		// Checking its positive
		if(amplifier < 0) {
			Messenger.send(sender, "&cAmplifier must be a rounded, positive number or 0.");
			return true;
		}
		
		// Grabbing item
		BrItem item = (BrItem) objects[0];
		
		// Grabbing effect
		BrEffect effect = (BrEffect) objects[1];
		
		String successMsg = "&aAdded new Potion Effect to &7"+item.getName()+"&a's instance of &7"+effect.getName()+"&a. Hover to view the details!";
		
		String successHoverText = generateEditingTitle(item, effect);
		
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
			// Adding all potion effects
			successHoverText += StringsUtil.toChatString(0, true, s -> "&2"+s[0]+"&2: &7"+s[1], effect.getPotions().toArray(new PotionEffect[0]));
			
			Messenger.send(sender, StringsUtil.hoverText("&7"+item.getName()+"&e's instance of &7"+effect.getName()+"&e does not contain a potion effect with that type and amplifier. Hover to view all potion effects on this effect!"
					, (!effect.hasPotions() ? successHoverText+"&8No Potion Effects!" : successHoverText)));
			return true;
		}
		
		// Save potion effect to effect
		effect.removePotion(potionEffect);
		
		// Adding the removed potion effect with removed layout
		successHoverText += StringsUtil.toChatString(0, true, s -> "&2"+s[0]+"&2: &7"+s[1], effect.getPotions().toArray(new PotionEffect[0]))
					+ "\n"+StringsUtil.toChatString(0, true, s -> "&4&m"+Chat.replace(s[0], "&4&m")+"&4: &8&m"+Chat.replace(s[1], "&8&m")+"&r", potionEffect);
		
		// Save the Item to the YML. This will also save the instance of the effect to the item
		BrItem.YML().save(item);

		// Allows the user to view the details on hover
		TextComponent successMsgComponent = StringsUtil.hoverText(successMsg, successHoverText);
		
		Messenger.send(sender, successMsgComponent);
		return true;
		
	}
	
	@Override
	public List<String> tabComplete(CommandSender sender, String[] args, Object... objects) {
		// Grabbing effect
		BrEffect effect = (BrEffect) objects[1];
		if(effect == null) 
			return Arrays.asList();
		
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
