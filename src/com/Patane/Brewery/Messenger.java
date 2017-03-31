package com.Patane.Brewery;

import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import com.Patane.Brewery.Chat;

public class Messenger {
	private static final Logger logger = Logger.getLogger("Minecraft");
	
	public static boolean send(CommandSender sender, String msg) {
        if (sender == null || msg.equals("")) {
            return false;
        }
        sender.sendMessage(Chat.PLUGIN_PREFIX + ChatColor.translateAlternateColorCodes('&', msg));
        return true;
    }
	public static void broadcast(String msg){
		Bukkit.broadcastMessage(Chat.PLUGIN_PREFIX + ChatColor.translateAlternateColorCodes('&', msg));
	}
	public static void info(String msg) {
		logger.info(Chat.PLUGIN_PREFIX + ChatColor.translateAlternateColorCodes('&', msg));
	}

	public static void warning(String msg) {
		logger.warning(Chat.PLUGIN_PREFIX + ChatColor.translateAlternateColorCodes('&', msg));
	}

	public static void severe(String msg) {
		logger.severe(Chat.PLUGIN_PREFIX + ChatColor.translateAlternateColorCodes('&', msg));
	}
}
