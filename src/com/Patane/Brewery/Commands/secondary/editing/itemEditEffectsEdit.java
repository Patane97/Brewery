package com.Patane.Brewery.Commands.secondary.editing;

import org.bukkit.command.CommandSender;

import com.Patane.Brewery.Brewery;
import com.Patane.Brewery.Commands.BrCommandHandler;
import com.Patane.Brewery.CustomEffects.BrEffect;
import com.Patane.Brewery.CustomItems.BrItem;
import com.Patane.Brewery.Editing.EditSession;
import com.Patane.Brewery.Editing.EditingInfo;
import com.Patane.Commands.CommandHandler;
import com.Patane.Commands.CommandInfo;
import com.Patane.Commands.PatCommand;
import com.Patane.util.general.Messenger;
import com.Patane.util.ingame.Commands;
@CommandInfo(
	name = "edit effects edit",
	description = "Edits an Effect that is attached to a Brewery Item.",
	usage = "/br edit effects edit <effect name> [set|delete] [something] ..."
)
@EditingInfo(type = BrItem.class)
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
		
		args = Commands.grabArgs(args, 1, args.length);
		
		PatCommand child = BrCommandHandler.grabInstance().getChildCommand(this, args[0]);
		if(child == null) {
			Messenger.send(sender, "&7"+args[0]+" &cis an invalid argument.");
			return false;
		}
		CommandHandler.grabInstance().handleCommand(sender, child, args, brEffect);
		return true;
	}
}
