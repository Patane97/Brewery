package com.Patane.Brewery.Commands.secondary;

import java.util.Arrays;
import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.Patane.Brewery.Brewery;
import com.Patane.Brewery.Commands.primary.createCommand;
import com.Patane.Brewery.CustomEffects.BrEffect;
import com.Patane.Brewery.CustomEffects.modifiers.None;
import com.Patane.Brewery.CustomEffects.triggers.Instant;
import com.Patane.Commands.CommandHandler.CommandPackage;
import com.Patane.Commands.CommandInfo;
import com.Patane.util.general.Messenger;
import com.Patane.util.ingame.Commands;
@CommandInfo(
	name = "create effect",
	description = "Creates a new effect using default values.",
	usage = "/brewery create effect <effect name>",
	maxArgs = 1
)
public class createEffect extends createCommand {

	@Override
	public boolean execute(CommandSender sender, String[] args, Object... objects) {
		
		if(args.length == 0 || args[0] == null) {
			Messenger.send(sender, "&cPlease specify a name for your effect.");
			return false;
		}
		
		// Setting name
		String name = Commands.combineArgs(args);
		
		if(name.contains(".") || name.contains("/")) {
			Messenger.send(sender, "&cItem names cannot contain the following characters: &7'.', '/'");
			return false;
		}
		
		if(Brewery.getEffectCollection().hasItem(name)) {
			Messenger.send(sender, "&7"+name+" &cis already the name of a brewery effect!");
			return false;
		}
		try {
			BrEffect brEffect = new BrEffect(name, new None(), new Instant(), null, null, null, null, null, null, null);

			// Attempt to save the effect to YML. If this gives us exceptions then we dont add the effect to the collection
			BrEffect.YML().save(brEffect);
			
			Brewery.getEffectCollection().add(brEffect);
			
			Messenger.send(sender, "&aCreated effect &7"+ brEffect.getName() +"&a.");
			// Maybe print a more detailed 'create' message in console?
		} catch (Exception e) {
			if(sender instanceof Player)
				Messenger.send(sender, "&cFailed to create effect due to the following error: \n &4&o" + e.getMessage());
			else
				Messenger.warning("Failed to create effect");
			e.printStackTrace();
		}
		
		return true;
	}
	
	@Override
	public List<String> tabComplete(CommandSender sender, String[] args, CommandPackage thisPackage) {
		return Arrays.asList("<effect name>");
	}
}
