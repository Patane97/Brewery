package com.Patane.Brewery.Commands.primary;

import org.bukkit.command.CommandSender;

import com.Patane.Brewery.Commands.BrCommandHandler;
import com.Patane.Brewery.Commands.secondary.editSessionEnd;
import com.Patane.Brewery.Editing.EditSession;
import com.Patane.Commands.CommandHandler;
import com.Patane.Commands.CommandInfo;
import com.Patane.Commands.PatCommand;
import com.Patane.util.general.Messenger;
import com.Patane.util.general.StringsUtil;

import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;

@CommandInfo(
	name = "editsession",
	aliases = {"editmode"},
	description = "Starts an editing session for something in Brewery.",
	usage = "/br editsession [type] <name>",
	permission = "brewery.editsession"
)
public class editSessionCommand implements PatCommand {
	
	@Override
	public boolean execute(CommandSender sender, String[] args, Object... objects) {
		
		String senderName = sender.getName();
		if(!args[0].equals("end") && EditSession.active(senderName)) {
			Messenger.send(sender, "&cYou are already editing &7"+EditSession.get(senderName)+"&c.");
			TextComponent text = StringsUtil.hoverText("&7Type "+PatCommand.grabInfo(editSessionEnd.class).usage()+" to end. \n&7Alternatively, click here to automatically end.", "&7Click here to end your current editing session!");
			text.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, PatCommand.grabInfo(editSessionEnd.class).usage()));
			Messenger.sendRaw(sender, text);
			
			return true;
		}
		
		PatCommand child = BrCommandHandler.grabInstance().getChildCommand(this, args[0]);
		if(child == null) {
			Messenger.send(sender, "&7"+args[0]+" &cis not a valid type for editing.");
			return false;
		}
		CommandHandler.grabInstance().handleCommand(sender, child, args);
		return true;
	}
}
