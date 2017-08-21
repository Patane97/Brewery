package com.Patane.Brewery;

import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class Messenger {
	private static final Logger logger = Brewery.getInstance().getLogger();
	
	public static boolean send(CommandSender sender, String msg) {
        // If the input sender is null or the string is empty, return.
        if (sender == null || msg.equals("")) {
            return false;
        }

        // Otherwise, send the message with the plugin prefix.
        sender.sendMessage(Chat.PLUGIN_PREFIX_SMALL + ChatColor.translateAlternateColorCodes('&', msg));
        return true;
    }
	public static void broadcast(String msg){
		Bukkit.broadcastMessage(Chat.PLUGIN_PREFIX_SMALL + ChatColor.translateAlternateColorCodes('&', msg));
	}
	public static void info(String msg) {
		logger.info(Chat.STRIPPED_PLUGIN_PREFIX + ChatColor.stripColor(ChatColor.translateAlternateColorCodes('&', msg)));
	}

	public static void warning(String msg) {
		logger.warning(Chat.STRIPPED_PLUGIN_PREFIX + ChatColor.stripColor(ChatColor.translateAlternateColorCodes('&', msg)));
	}

	public static void severe(String msg) {
		logger.severe(Chat.STRIPPED_PLUGIN_PREFIX + ChatColor.stripColor(ChatColor.translateAlternateColorCodes('&', msg)));
	}
	public static void debug(ChatType type, String msg) {
		if(!Brewery.debugMode())
			return;
		msg = ">> " + msg;
		switch(type){
		case BROADCAST:
			broadcast(msg);
			break;
		case WARNING:
			warning(msg);
			break;
		case SEVERE:
			severe(msg);
			break;
		case INFO:
			info(msg);
			break;
		}
	}
	public static void debug(CommandSender sender, String msg) {
		if(Brewery.debugMode()){
			msg = ">> &c" + msg;
			send(sender, msg);
		}
	}
	public static enum ChatType {
		BROADCAST(), WARNING(), SEVERE(), INFO();
	}
}
