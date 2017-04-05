package com.Patane.Brewery;

import org.bukkit.plugin.java.JavaPlugin;

import com.Patane.Brewery.Listeners.GlobalListener;
import com.Patane.Brewery.commands.CommandHandler;

/*
 * @author Patane
 */

public class Brewery extends JavaPlugin{
	
	public void onEnable() {
		getServer().getPluginManager().registerEvents(new GlobalListener(), this);
        CommandHandler commandHandler = new CommandHandler(this);
		this.getCommand("br").setExecutor(commandHandler);
		// Loading message
		Messenger.info("Brewery v" + this.getDescription().getVersion() + "Loaded!");
	}
}
