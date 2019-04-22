package com.Patane.Brewery.Commands.primary;

import org.bukkit.command.CommandSender;

import com.Patane.Brewery.Commands.BrCommandHandler;
import com.Patane.Brewery.Commands.secondary.editSessionEnd;
import com.Patane.Brewery.Editing.EditSession;
import com.Patane.Brewery.Editing.EditingInfo;
import com.Patane.Commands.CommandHandler;
import com.Patane.Commands.CommandInfo;
import com.Patane.Commands.PatCommand;
import com.Patane.util.general.Messenger;
import com.Patane.util.general.StringsUtil;

import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;

@CommandInfo(
	name = "edit",
	description = "Edit something in Brewery. Requires an editing session to be active.",
	usage = "/br edit [something]",
	permission = "brewery.edit"
)
public class editCommand implements PatCommand {
	
	@Override
	public boolean execute(CommandSender sender, String[] args, Object... objects) {
		String senderName = sender.getName();
		if(!EditSession.active(senderName)) {
			Messenger.send(sender, "&cYou must start an editing session before attempting to edit.");
			TextComponent text = StringsUtil.hoverText("&7Type "+PatCommand.grabInfo(editSessionCommand.class).usage()+" to start! \n&7Alternatively, click here to auto-complete the command.", "&7Click here to auto-complete the edit sessions command!");
			text.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, PatCommand.grabInfo(editSessionCommand.class).usage()));
			Messenger.sendRaw(sender, text);
			return true;
		}
		
		PatCommand child = BrCommandHandler.grabInstance().getChildCommand(this, args[0]);

		if(child == null) {
			Messenger.send(sender, "&7"+args[0]+" &cis not a valid type for editing.");
			return false;
		}
		// This checks if the command they are attempting to use/edit with is VALID for the type of session they are in (eg, attempting to use /br edit item name whilst in effect editsession)
		if(child.getClass().getAnnotation(EditingInfo.class).type() != EditSession.get(senderName).getClass()) {
			Messenger.send(sender, "&cYou are currently editing &7"+EditSession.get(senderName).getName()+"&c. The desired command cannot edit this. \nType &7"+PatCommand.grabInfo(editSessionEnd.class).usage()+" &cto end your current session.");
			return true;
		}
		
		CommandHandler.grabInstance().handleCommand(sender, child, args);
		return true;
	}
}
