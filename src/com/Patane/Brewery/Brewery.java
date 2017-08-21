package com.Patane.Brewery;

import org.bukkit.Material;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.Patane.Brewery.CustomEffects.CustomEffect.DamageContainer;
import com.Patane.Brewery.CustomEffects.InstantEffect;
import com.Patane.Brewery.CustomEffects.LingeringEffect;
import com.Patane.Brewery.CustomPotions.CustomPotion;
import com.Patane.Brewery.CustomPotions.CustomPotions;

import com.Patane.Brewery.Listeners.GlobalListener;
import com.Patane.Brewery.Listeners.ParticlePacketAdapter;
import com.Patane.Brewery.commands.CommandHandler;
import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketAdapter;

/*
 * @author Patane
 */

public class Brewery extends JavaPlugin{
	private static boolean debugMode = true;
	
	private static Brewery brewery;
	private static CustomPotions customPotions;
	
	private ProtocolManager protocolManager;
	public void onEnable() {
		brewery = this;
		customPotions = new CustomPotions();
		getServer().getPluginManager().registerEvents(new GlobalListener(), this);
        CommandHandler commandHandler = new CommandHandler(this);
		this.getCommand("br").setExecutor(commandHandler);

		protocolManager = ProtocolLibrary.getProtocolManager();
		protocolManager.addPacketListener(new ParticlePacketAdapter(PacketAdapter.params(this, new PacketType[] {PacketType.Play.Server.WORLD_EVENT})));
		// Loading message
		Messenger.info("Brewery v" + this.getDescription().getVersion() + "Loaded!");
		new CustomPotion("Vampiric Scepter", Material.SPLASH_POTION, 
				new InstantEffect("Life Drainer", new DamageContainer(DamageCause.FIRE, 5), 3),
				new LingeringEffect("Mark of Light", new DamageContainer(DamageCause.FIRE, 1), 3, 5f, 0.5f,
						new PotionEffect(PotionEffectType.GLOWING, 20, 1)));
	}
	public static boolean debugMode() {
		return debugMode;
	}
	public static Brewery getInstance(){
		return brewery;
	}
	public static CustomPotions getCustomPotions(){
		return customPotions;
	}
}
