package com.Patane.Brewery.Commands.secondary.editing;

import java.util.Arrays;
import java.util.List;

import org.bukkit.Sound;
import org.bukkit.command.CommandSender;

import com.Patane.Brewery.CustomEffects.BrEffect;
import com.Patane.Brewery.CustomEffects.BrEffect.BrSoundEffect;
import com.Patane.Brewery.CustomItems.BrItem;
import com.Patane.Brewery.Editing.EditSession;
import com.Patane.Commands.CommandHandler.CommandPackage;
import com.Patane.Commands.CommandInfo;
import com.Patane.util.general.Messenger;
import com.Patane.util.general.StringsUtil;
@CommandInfo(
	name = "edit effects edit <effectname> set sounds",
	description = "Sets the Sound Effects for an Effect that is attached to a Brewery Item.",
	usage = "/brewery edit effects edit <effect name> set sounds [type] [volume] <pitch>",
	maxArgs = 4
)
public class itemEditEffectsEditSetSounds extends itemEditEffectsEditSet {

	@Override
	public boolean execute(CommandSender sender, String[] args, Object... objects) {
		
		if(args.length < 1) {
			Messenger.send(sender, "&cPlease provide a Sound.");
			return false;
		}
		
		Sound sound = null;
		
		try {
			sound = StringsUtil.constructEnum(args[0], Sound.class);
		} catch (IllegalArgumentException e) {
			Messenger.send(sender, "&7"+args[0]+" &cis not a valid Sound.");
			return true;
		}

		if(args.length < 2) {
			Messenger.send(sender, "&cPlease provide a volume.");
			return false;
		}

		Float volume = null;
		try {
			volume = Float.parseFloat(args[1]);
		} catch (NumberFormatException e) {
			Messenger.send(sender, "&7"+args[1]+" &cis an invalid volume.");
			return true;
		}

		if(args.length < 3) {
			Messenger.send(sender, "&cPlease provide a pitch.");
			return false;
		}


		Float pitch = null;
		try {
			pitch = Float.parseFloat(args[2]);
		} catch (NumberFormatException e) {
			Messenger.send(sender, "&7"+args[2]+" &cis an invalid pitch.");
			return true;
		}
		
		BrSoundEffect soundEffect = new BrSoundEffect(sound, volume, pitch);
		
		BrEffect brEffect = (BrEffect) objects[0];
		
		String successMsg = "&aAdded &7"+sound.name()+" &aSound Effect to this effect.";
		if(brEffect.hasParticle())
			successMsg = "&aChanged to &7"+sound.name()+" &aSound Effect.";
		
		brEffect.setSound(soundEffect);
		
		BrItem brItem = (BrItem) EditSession.get(sender.getName());
		
		BrItem.YML().save(brItem);
		
		Messenger.send(sender, successMsg);
		return true;
	}
	
	@Override
	public List<String> tabComplete(CommandSender sender, String[] args, CommandPackage thisPackage) {
		switch(args.length) {
			case 7: return Arrays.asList(StringsUtil.enumValueStrings(Sound.class));
			case 8: return Arrays.asList("<volume>");
			default: return Arrays.asList("<pitch>");
			
		}
	}
}
