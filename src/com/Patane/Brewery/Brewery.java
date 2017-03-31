package com.Patane.Brewery;

import org.bukkit.plugin.java.JavaPlugin;

import com.Patane.Brewery.Listeners.GlobalListener;

/*
 * @author Patane
 */

public class Brewery extends JavaPlugin{
	
	public void onEnable() {
		// Loading message
		Messenger.info("Brewery v" + this.getDescription().getVersion() + "Loaded!");
		getServer().getPluginManager().registerEvents(new GlobalListener(), this);
	}
}
