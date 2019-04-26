package com.Patane.Brewery.Commands.secondary.editing;

import org.bukkit.attribute.Attribute;
import org.bukkit.command.CommandSender;

import com.Patane.Brewery.Commands.BrCommandHandler;
import com.Patane.Commands.CommandHandler;
import com.Patane.Commands.CommandInfo;
import com.Patane.Commands.PatCommand;
import com.Patane.util.general.Messenger;
import com.Patane.util.general.StringsUtil;
import com.Patane.util.ingame.Commands;
@CommandInfo(
	name = "edit item attributes [attribute]",
	aliases = {"attribute", "attrib"},
	description = "Edits an items attribute modifiers.",
	usage = "/br edit item attributes [attribute] [add|remove] <modifier name> ...",
	hideCommand = true
)
public class itemEditItemAttributes extends itemEditItem {

	@Override
	public boolean execute(CommandSender sender, String[] args, Object... objects) {
		Attribute attribute = null;

		try {
			// Attempting to find the Attribute enum from given string name.
			attribute = StringsUtil.constructEnum(args[0], Attribute.class);
		} catch (IllegalArgumentException e) {
			Messenger.send(sender,  "&7"+args[0]+" &cis not a valid Attribute.");
			return true;
		}
		if(args.length < 2)
			return false;
		
		args = Commands.grabArgs(args, 1, args.length);
		
		PatCommand child = BrCommandHandler.grabInstance().getChildCommand(this, args[0]);
		if(child == null) {
			Messenger.send(sender, "&7"+args[0]+" &cis an invalid argument.");
			return false;
		}
		CommandHandler.grabInstance().handleCommand(sender, child, args, attribute);
		return true;
	}
}
