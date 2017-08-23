package com.Patane.Brewery;

import org.bukkit.plugin.java.JavaPlugin;

import com.Patane.Brewery.Collections.BrItemCollection;
import com.Patane.Brewery.Collections.CustomEffectCollection;
import com.Patane.Brewery.CustomItems.BrItem;
import com.Patane.Brewery.CustomItems.BrItemYML;
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
	private static BrItemCollection itemCollection;
	private static CustomEffectCollection effectCollection;
	
	private ProtocolManager protocolManager;
	
	public void onEnable() {
		brewery = this;
		itemCollection = new BrItemCollection();
		effectCollection = new CustomEffectCollection();
		getServer().getPluginManager().registerEvents(new GlobalListener(), this);
        CommandHandler commandHandler = new CommandHandler(this);
		this.getCommand("br").setExecutor(commandHandler);

		protocolManager = ProtocolLibrary.getProtocolManager();
		protocolManager.addPacketListener(new ParticlePacketAdapter(PacketAdapter.params(this, new PacketType[] {PacketType.Play.Server.WORLD_EVENT})));
		loadFiles();
//		/////////////////////////
//		Map<CustomEffect, EntityType[]> effectPerEntities = new HashMap<CustomEffect, EntityType[]>();
//		EntityType[] undead = {EntityType.SKELETON, EntityType.ZOMBIE};
//		EntityType[] players = {EntityType.PLAYER};
//		effectPerEntities.put(new LingeringEffect("Life Drainer", new Damage(DamageCause.FIRE, 5), 3, 5f, 0.5f), undead);
//		effectPerEntities.put(new LingeringEffect("Mark of Light", new Heal(2), 3, 5f, 0.5f,
//				new PotionEffect(PotionEffectType.GLOWING, 20, 1)), players);
//		new BrItem("Vampiric Scepter", CustomType.THROWABLE, ItemUtilities.createItem(Material.SPLASH_POTION, 1, (short) 0, "&6Vampiric Scepter", "&7Damages Undead", "&7heals players"), effectPerEntities);
//		/////////////////////////

		// Loading message
		Messenger.info("Brewery v" + this.getDescription().getVersion() + "Loaded!");
	}
	public void onDisable(){
		saveFiles();
	}
	private void saveFiles(){
		BrItem.YML().save();
	}
	private void loadFiles(){
		BrItem.setYML(new BrItemYML(this));
		BrItem.YML().load();
	}
	public static boolean debugMode() {
		return debugMode;
	}
	public static Brewery getInstance(){
		return brewery;
	}
	public static BrItemCollection getItemCollection(){
		return itemCollection;
	}
	public static CustomEffectCollection getEffectCollection(){
		return effectCollection;
	}
}
