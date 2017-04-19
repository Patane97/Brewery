package com.Patane.Brewery;

import org.bukkit.Material;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionType;

import com.Patane.Brewery.CustomPotions.CustomPotion;
import com.Patane.Brewery.Listeners.GlobalListener;
import com.Patane.Brewery.Listeners.ParticlePacketAdapter;
import com.Patane.Brewery.collections.CustomPotions;
import com.Patane.Brewery.commands.CommandHandler;
import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketAdapter;

/*
 * @author Patane
 */

public class Brewery extends JavaPlugin{

	private ProtocolManager protocolManager;
	public void onEnable() {
		getServer().getPluginManager().registerEvents(new GlobalListener(), this);
        CommandHandler commandHandler = new CommandHandler(this);
		this.getCommand("br").setExecutor(commandHandler);

		protocolManager = ProtocolLibrary.getProtocolManager();
		protocolManager.addPacketListener(new ParticlePacketAdapter(PacketAdapter.params(this, new PacketType[] {PacketType.Play.Server.WORLD_EVENT})));
		CustomPotions.add(new CustomPotion("New Potion", Material.SPLASH_POTION, PotionType.LUCK));
		// Loading message
		Messenger.info("Brewery v" + this.getDescription().getVersion() + "Loaded!");
	}
}
