package com.Patane.Brewery.commands;

import java.util.Arrays;
import java.util.HashMap;
import java.util.StringJoiner;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.Patane.Brewery.Brewery;
import com.Patane.Brewery.Messenger;
import com.Patane.Brewery.commands.all.*;

public class CommandHandler implements CommandExecutor{
	
	private Brewery plugin;
	private HashMap<String, BrCommand> commands;
	
	public CommandHandler(Brewery battlegrounds) {
		this.plugin = battlegrounds;
		registerAll();
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		String command = (args.length > 0 ? args[0] : "");
		// checking if sender is from console
		Player target = ((sender instanceof Player) ? (Player) sender : null);
		if (target == null){
			Messenger.send(target, "Command cannot be executed from console!");
			return true;
		}
		BrCommand newCommand = getCommand(command);
		if(newCommand == null){
			Messenger.send(target, "Command not found!");
			return true;
		}
		return newCommand.execute(plugin, target, args);
	}
	private BrCommand getCommand(String cmd){
		for(String commandName : commands.keySet()){
			if(cmd.contains(commandName))
				return commands.get(commandName);
		}
		return null;
	}
	private void registerAll() {
		commands = new HashMap<String, BrCommand>();
		register(potionCommand.class);
	}
	public void register(Class< ? extends BrCommand> command){
		CommandInfo cmdInfo = command.getAnnotation(CommandInfo.class);
		if(cmdInfo == null) {
			Messenger.warning("A command is missing its attached CommandInfo Annotation!");
			return;
		}
		try {
			commands.put(cmdInfo.name(), command.newInstance());
		} catch (InstantiationException | IllegalAccessException e) {
			e.printStackTrace();
		}

	}

	public static String argPotionNameToString(String[] args){
		String[] temp = Arrays.copyOfRange(args, 1, args.length);
		StringJoiner sj = new StringJoiner(" ");
		for(String arg : temp)
			sj.add(arg);
		return sj.toString();
	}

}
