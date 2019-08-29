package com.Patane.Brewery.Commands.secondary.editing;

import java.util.Arrays;
import java.util.List;

import org.bukkit.command.CommandSender;

import com.Patane.Brewery.Commands.BrCommandHandler;
import com.Patane.Commands.CommandHandler;
import com.Patane.Commands.CommandHandler.CommandPackage;
import com.Patane.Commands.CommandInfo;
import com.Patane.util.general.Messenger;
import com.Patane.util.ingame.Commands;
@CommandInfo(
	name = "edit effects edit <effectname> set filter [target|ignore]",
	description = "Edits a Filter Group for an Effect that is attached to a Brewery Item.",
	usage = "/brewery edit effects edit <effect name> set filter [target|ignore] [add|remove|clear]",
	maxArgs = 1
)
public class itemEditEffectsEditSetFilter extends itemEditEffectsEditSet {

	@Override
	public boolean execute(CommandSender sender, String[] args, Object... objects) {
		
		if(args.length < 1) {
			Messenger.send(sender, "&cPlease provide a filter group.");
			return false;
		}		
		String filterGroup = null;
		
		switch(args[0].toLowerCase()) {
		case "target":
			filterGroup = "Target";
			break;
		case "ignore":
			filterGroup = "Ignore";
			break;
		default:
			Messenger.send(sender, "&7"+args[0]+" &cis an invalid filter group.");
			return true;
		}
		args = Commands.grabArgs(args, 1, args.length);
		CommandPackage child = BrCommandHandler.getChildPackage(this.getClass(), args[0]);
		if(child == null) {
			Messenger.send(sender, "&7"+args[0]+" &cis not a valid set filter command.");
			return false;
		}
		CommandHandler.grabInstance().handleCommand(sender, child.command(), args, objects[0], filterGroup);
		return true;
	}
	
	@Override
	public List<String> tabComplete(CommandSender sender, String[] args, CommandPackage thisPackage) {
		switch(args.length) {
			case 7:
				return Arrays.asList("target", "ignore");
			default:
				return thisPackage.trimmedChildren();
			}
	}
}
