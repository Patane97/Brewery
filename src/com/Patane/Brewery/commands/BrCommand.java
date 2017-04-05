package com.Patane.Brewery.commands;

import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public interface BrCommand {
	/**
	 * 
	 * @param sender who typed the command
	 * @param args of the command
	 * @return true if the command did not fail to execute
	 */
	public boolean execute(Plugin plugin, Player sender, String[] args);
}
