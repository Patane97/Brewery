package com.Patane.Brewery.commands.primary;

import org.bukkit.command.CommandSender;

import com.Patane.Brewery.Brewery;
import com.Patane.Commands.CommandInfo;
import com.Patane.Commands.PatCommand;
import com.Patane.util.general.Messenger;
@CommandInfo(
	name = "reload",
	description = "Reloads the plugin or a specific part of the plugin.",
	usage = "/brewery reload",
	permission = "brewery.reload"
)
public class reloadCommand extends PatCommand{

	@Override
	public boolean execute(CommandSender sender, String[] args, Object... objects) {
		if(args == null || args.length == 0) {
			// Reload all plugin ymls
			Brewery.getInstance().unLoadPlugin();
			Brewery.getInstance().loadPlugin();
			Messenger.send(sender, "&aSuccessfully reloaded &7Brewery &o"+Brewery.getInstance().getDescription().getVersion()+"&r&a.");
		}
		return true;
		// Maybe add individual YML reloads here.
//		// Reload individual YML's here
//		CommandPackage child = BrCommandHandler.getChildPackage(this.getClass(), args[0]);
//		if(child == null) {
//			Messenger.send(sender, "&7"+args[0]+" &cis not a valid info type.");
//			return false;
//		}
//		CommandHandler.grabInstance().handleCommand(sender, child.command(), args);
	}
}
