package com.Patane.Brewery.Commands.secondary.editing;

import java.util.Arrays;
import java.util.List;

import org.bukkit.command.CommandSender;

import com.Patane.Brewery.Brewery;
import com.Patane.Brewery.Commands.BrCommandHandler;
import com.Patane.Brewery.CustomEffects.BrEffect;
import com.Patane.Brewery.CustomItems.BrItem;
import com.Patane.Brewery.Editing.EditSession;
import com.Patane.Commands.CommandHandler;
import com.Patane.Commands.CommandHandler.CommandPackage;
import com.Patane.Commands.CommandInfo;
import com.Patane.util.collections.PatCollectable;
import com.Patane.util.general.Messenger;
import com.Patane.util.general.StringsUtil;
import com.Patane.util.ingame.Commands;
@CommandInfo(
	name = "edit effects edit <effectname>",
	description = "Edits an Effect that is attached to a Brewery Item.",
	usage = "/brewery edit effects edit <effect name> [set|delete] [something] ...",
	maxArgs = 1
)
public class itemEditEffectsEdit extends itemEditEffects {

	@Override
	public boolean execute(CommandSender sender, String[] args, Object... objects) {
		BrEffect brEffect = null;
		
		
		
		if(!Brewery.getEffectCollection().hasItem(args[0])) {
			Messenger.send(sender, "&cThere is no effect named &7"+args[0]+"&c.");
			return true;
		}
		BrItem brItem = (BrItem) EditSession.get(sender.getName());
		
		if(!brItem.hasEffect(args[0])) {
			Messenger.send(sender, "&cItem does not have &7"+Brewery.getEffectCollection().getItem(args[0]).getName()+" &ceffect.");
			return true;
		}
		
		brEffect = brItem.getEffect(args[0]);

		if(args.length < 2)
			return false;
		
		args = Commands.grabArgs(args, 1, args.length);
		
		CommandPackage child = BrCommandHandler.getChildPackage(this.getClass(), args[0]);
		if(child == null) {
			Messenger.send(sender, "&7"+args[0]+" &cis an invalid argument.");
			return false;
		}
		CommandHandler.grabInstance().handleCommand(sender, child.command(), args, brEffect);
		return true;
	}

	public List<String> tabComplete(CommandSender sender, String[] args, CommandPackage thisPackage) {
		switch(args.length) {
		case 4:
			PatCollectable brItem = EditSession.get(sender.getName());
			if(!(brItem instanceof BrItem))
				return Arrays.asList();
			return StringsUtil.getCollectableNames(((BrItem) brItem).getEffects());
		default:
			return thisPackage.trimmedChildren();
		}
	}
}
