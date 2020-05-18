package com.Patane.Brewery.NEWcommands.primary;

import java.util.Map;

import org.bukkit.command.CommandSender;

import com.Patane.Brewery.Commands.BrCommandHandler;
import com.Patane.Commands.CommandHandler;
import com.Patane.Commands.CommandHandler.CommandPackage;
import com.Patane.Commands.CommandInfo;
import com.Patane.Commands.PatCommand;
import com.Patane.util.YAML.MapParsable;
import com.Patane.util.general.Chat;
import com.Patane.util.general.Messenger;
import com.Patane.util.general.StringsUtil;

import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
@CommandInfo(
	name = "info",
	aliases = {"information","detail"},
	description = "Gives detailed information about a specific Brewery product.",
	usage = "/brewery info [type] <type name>",
	permission = "brewery.info"
)
public class infoCommand extends PatCommand{

	@Override
	public boolean execute(CommandSender sender, String[] args, Object... objects) {
		// Reason we dont check for length < 1 is because CommandHandler already checks for that and handles it appropriately.
		if(args.length < 2) { 
			Messenger.send(sender, "&cPlease specify a type name.");
			return false;
		}
		CommandPackage child = BrCommandHandler.getChildPackage(this.getClass(), args[0]);
		if(child == null) {
			Messenger.send(sender, "&7"+args[0]+" &cis not a valid info type.");
			return false;
		}
		CommandHandler.grabInstance().handleCommand(sender, child.command(), args);
		return true;
	}
	
	/**
	 * Creates a chat message which displays the underlying type and name in the following format: "&2Type: &7Name"
	 * On hover, it displays the specific MapParsables fields and values in a similar format.
	 * @param sender
	 * @param name
	 * @param mapParsable
	 */
	protected void mapParsableInfo(CommandSender sender, String type, MapParsable mapParsable) {
		// Constructs the initial "&2Type: " format of the displayed string.
		type = "&2"+type+": ";
		
		TextComponent infoText;
		// If the MapParsable is null then it is underfined and has no information.
		if(mapParsable == null) {
			infoText = new TextComponent(Chat.translate(type+"&cUndefined"));
			infoText.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(Chat.translate("&7No Information")).create()));
		} else {
			// Add the name to the string and construct a TextComponent from it (now looks like "&2Type: &7Name")
			infoText = new TextComponent(Chat.translate(type+"&7"+mapParsable.name()));
			
			// Hovertext constructed to display the name of the MapParsable before anything else.
			String hoverText = "&2Name: &7"+mapParsable.name();
			
			// Grabbing the fields and values of the MapParsable and saving as a Map<String, Object>.
			Map<String, Object> fieldMap = mapParsable.mapFields();

			// If there were no fields to loop through, then "&7No fields" is added under the MapParsable's name.
			if(fieldMap.isEmpty())
				hoverText += "\n&7&oNo fields";
			
			// Looping through each field and adding it to the string in the following format: "&2Field: &aValue".
			else for(String fieldName : fieldMap.keySet())
				hoverText += "\n&2"+StringsUtil.formaliseString(fieldName)+": &a"+fieldMap.get(fieldName).toString();

			
			// Saving the HoverEvent to our displayed text.
			infoText.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(Chat.translate(hoverText)).create()));
		}
		// Sends either the undefined or defined MapParsable object to sender.
		Messenger.sendRaw(sender, infoText);
	}
}
