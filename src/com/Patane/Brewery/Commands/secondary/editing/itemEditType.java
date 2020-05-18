package com.Patane.Brewery.Commands.secondary.editing;

import java.util.Arrays;
import java.util.List;

import org.bukkit.command.CommandSender;

import com.Patane.Brewery.Commands.primary.editCommand;
import com.Patane.Brewery.CustomItems.BrItem;
import com.Patane.Brewery.CustomItems.BrItem.CustomType;
import com.Patane.Brewery.Editing.EditSession;
import com.Patane.Brewery.Editing.EditingInfo;
import com.Patane.Commands.CommandHandler.CommandPackage;
import com.Patane.Commands.CommandInfo;
import com.Patane.util.general.Messenger;
import com.Patane.util.general.StringsUtil;
@CommandInfo(
	name = "edit type",
	description = "Edits the Item Type for a Brewery Item.",
	usage = "/brewery edit type [type]",
	maxArgs = 1,
	hideCommand = true
)
@EditingInfo(type = BrItem.class)
public class itemEditType extends editCommand {

	@Override
	public boolean execute(CommandSender sender, String[] args, Object... objects) {
		
		CustomType type = null;
		try {
			type = StringsUtil.constructEnum(args[0], CustomType.class);
		} catch (IllegalArgumentException e) {
			Messenger.send(sender, "&7"+args[0]+" &cis an invalid Type. Options are "+StringsUtil.stringJoiner(StringsUtil.enumValueStrings(CustomType.class), "&c, &7", "&7", "&c."));
			return true;
		}
		
		BrItem brItem = (BrItem) EditSession.get(sender.getName());
		
		brItem.setType(type);
		
		BrItem.YML().save(brItem);
		
		Messenger.send(sender, "&aSet Item Type to &7"+type+"&a.");
		return true;
	}
	@Override
	public List<String> tabComplete(CommandSender sender, String[] args, CommandPackage thisPackage) {
		return Arrays.asList(StringsUtil.enumValueStrings(CustomType.class));
	}
}
