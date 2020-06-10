package com.Patane.Brewery.Commands.secondary.edit.item.effects;

import java.util.Arrays;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.command.CommandSender;

import com.Patane.Brewery.CustomEffects.BrEffect;
import com.Patane.Brewery.CustomEffects.BrEffect.BrSoundEffect;
import com.Patane.Brewery.CustomItems.BrItem;
import com.Patane.Commands.CommandInfo;
import com.Patane.util.general.Chat;
import com.Patane.util.general.Messenger;
import com.Patane.util.general.StringsUtil;

import net.md_5.bungee.api.chat.TextComponent;
@CommandInfo(
	name = "edit item effects modify set sounds",
	description = "Sets or changes the Sound Effects of an Effect for a Brewery Item. These changes are seperate from the original Effect.",
	usage = "/brewery edit item <item name> effects modify <effect name> set sounds [type] <volume> <pitch>",
	maxArgs = 3
)
public class editItemEffectsModifySetSounds extends editItemEffectsModifySet {

	@Override
	public boolean execute(CommandSender sender, String[] args, Object... objects) {
		
		// Checking sound type is given
		if(args.length < 1) {
			Messenger.send(sender, "&ePlease specify a sound type.");
			return true;
		}

		// Checking/Saving sound type
		Sound sound = null;
		try {
			sound = StringsUtil.constructEnum(args[0], Sound.class);
		} catch (IllegalArgumentException e) {
			Messenger.send(sender, "&7"+args[0]+" &cis not a valid Sound Type.");
			return true;
		}
		
		// Checking volume is given
		if(args.length < 2) {
			Messenger.send(sender, "&ePlease provide a volume. This must be a positive number.");
			return true;
		}
		
		// Checking/Saving volume
		Float volume = null;
		try {
			volume = Float.parseFloat(args[1]);
		} catch (NumberFormatException e) {
			Messenger.send(sender, "&7"+args[1]+" &cis not a valid volume. It must be a positive number.");
			return true;
		}
		
		// Checking its positive
		if(volume <= 0) {
			Messenger.send(sender, "&cVolume must be a positive number.");
			return true;
		}
		
		// Checking pitch is given
		if(args.length < 3) {
			Messenger.send(sender, "&cPlease provide a pitch. This must be a positive number or 0.");
			return true;
		}
		
		// Checking/Saving pitch
		Float pitch = null;
		try {
			pitch = Float.parseFloat(args[2]);
		} catch (NumberFormatException e) {
			Messenger.send(sender, "&7"+args[2]+" &cis not a valid pitch. It must be a positive number or 0.");
			return true;
		}
		
		// Checking its positive
		if(pitch < 0) {
			Messenger.send(sender, "&cPitch must be a positive number or 0.");
			return true;
		}
		
		// Creating the sound effect
		BrSoundEffect soundEffect = new BrSoundEffect(sound, volume, pitch);

		// Grabbing item
		BrItem item = (BrItem) objects[0];
		
		// Grabbing effect
		BrEffect effect = (BrEffect) objects[1];

		String successMsg = "&aAdded new Sound Effect to &7"+item.getName()+"&a's instance of &7"+effect.getName()+"&a. Hover to view the details!";
		
		String successHoverText = generateEditingTitle(item, effect);
		
		BrSoundEffect previousSoundEffect = effect.getSoundEffect();
		
		// If the sound effect values are the same, do nothing and message appropriately
		if(soundEffect.equals(previousSoundEffect)) {
			Messenger.send(sender, StringsUtil.hoverText("&7"+item.getName()+"&e's instance of &7"+effect.getName()+"&e already has a Sound Effect with those values. Hover to view it!"
														, successHoverText + effect.getSoundEffect().toChatString(0, true)));
			return true;
		}
		
		if(previousSoundEffect != null) {
			// If its different, then it is changing
			successMsg = "&aChanged the Sound Effect for &7"+item.getName()+"&a's instance of &7"+effect.getName()+"&a. Hover to view the details!";
			successHoverText += "&2"+soundEffect.className()+":\n"
							  + StringsUtil.tableCompareFormatter(0,
								s -> "&2  "+s[0]+": &7"+s[1]
							  , s -> "&2  "+s[0]+": &8"+s[1]+" &7-> "+s[2]
							  , StringsUtil.getFieldNames(BrSoundEffect.class) , StringsUtil.prepValueStrings(previousSoundEffect) , StringsUtil.prepValueStrings(soundEffect));
		}
		// There was previously none, so add it!
		else
			successHoverText += Chat.add(soundEffect.toChatString(0, true), ChatColor.BOLD);
		
		// Sets the sound effect to effect
		effect.setSoundEffect(soundEffect);

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
			case 1: return Arrays.asList(StringsUtil.enumValueStrings(Sound.class));
			case 2: return Arrays.asList("<volume>");
			case 3: return Arrays.asList("<pitch>");
		}
		return Arrays.asList();
	}
}
