package com.Patane.Brewery.NEWcommands.primary;

import java.util.Arrays;
import java.util.List;

import org.bukkit.command.CommandSender;

import com.Patane.Brewery.Editing.EditSession;
import com.Patane.Brewery.commands.BrCommandHandler;
import com.Patane.Brewery.commands.secondary.editSessionEnd;
import com.Patane.Commands.CommandHandler;
import com.Patane.Commands.CommandHandler.CommandPackage;
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
	usage = "/brewery editsession [type] <name>",
	permission = "brewery.editsession"
)
public class editSessionCommand extends PatCommand {
	
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
		
		CommandPackage child = BrCommandHandler.getChildPackage(this.getClass(), args[0]);
		if(child == null) {
			Messenger.send(sender, "&7"+args[0]+" &cis not a valid type for editing.");
			return false;
		}
		CommandHandler.grabInstance().handleCommand(sender, child.command(), args);
		return true;
	}
	
	public List<String> tabComplete(CommandSender sender, String[] args, CommandPackage thisPackage) {
		if(EditSession.active(sender.getName()))
			return Arrays.asList("end");
		List<String> children = thisPackage.trimmedChildren();
		children.remove("end");
		return children;
	}
}
