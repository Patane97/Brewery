package com.Patane.Brewery;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.Patane.Brewery.Collections.BrEffectCollection;
import com.Patane.Brewery.Collections.BrItemCollection;
import com.Patane.Brewery.CustomEffects.BrEffect;
import com.Patane.Brewery.CustomEffects.BrEffectYML;
import com.Patane.Brewery.CustomEffects.Modifier;
import com.Patane.Brewery.CustomEffects.types.Instant;
import com.Patane.Brewery.CustomEffects.types.Lingering;
import com.Patane.Brewery.CustomItems.BrItem;
import com.Patane.Brewery.CustomItems.BrItem.CustomType;
import com.Patane.Brewery.CustomItems.BrItemYML;
import com.Patane.Brewery.Listeners.GlobalListener;
import com.Patane.Brewery.Listeners.ParticlePacketAdapter;
import com.Patane.Brewery.commands.CommandHandler;
import com.Patane.Brewery.util.ItemUtilities;
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
	private static BrEffectCollection effectCollection;
	
	private ProtocolManager protocolManager;
	
	public void onEnable() {
		brewery = this;
		itemCollection = new BrItemCollection();
		effectCollection = new BrEffectCollection();
		getServer().getPluginManager().registerEvents(new GlobalListener(), this);
        CommandHandler commandHandler = new CommandHandler(this);
		this.getCommand("br").setExecutor(commandHandler);

		protocolManager = ProtocolLibrary.getProtocolManager();
		protocolManager.addPacketListener(new ParticlePacketAdapter(PacketAdapter.params(this, new PacketType[] {PacketType.Play.Server.WORLD_EVENT})));
		loadFiles();
		/////////////////////////
		Map<BrEffect, EntityType[]> effectPerEntities = new HashMap<BrEffect, EntityType[]>();
		EntityType[] undead = {EntityType.SKELETON, EntityType.ZOMBIE};
		EntityType[] players = {EntityType.PLAYER};
		effectPerEntities.put(new BrEffect("Life Drainer", new Instant(), new Modifier.Damage(DamageCause.FIRE, 5), 3), undead);
		effectPerEntities.put(new BrEffect("Mark of Light", new Lingering(5f, 0.5f), new Modifier.Heal(2), 3,
				new PotionEffect(PotionEffectType.GLOWING, 20, 1)), players);
		new BrItem("Vampiric Scepter", CustomType.THROWABLE, ItemUtilities.createItem(Material.SPLASH_POTION, 1, (short) 0, "&6Vampiric Scepter", "&7Damages Undead", "&7heals players"), effectPerEntities);
		/////////////////////////

		BrEffect.YML().save();
		// Loading message
		Messenger.info("Brewery v" + this.getDescription().getVersion() + "Loaded!");
	}
	public void onDisable(){
		saveFiles();
	}
	private void saveFiles(){
		BrItem.YML().save();
		BrEffect.YML().save();
	}
	private void loadFiles(){
		BrItem.setYML(new BrItemYML(this));
		BrItem.YML().load();
		BrEffect.setYML(new BrEffectYML(this));
		BrEffect.YML().load();
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
	public static BrEffectCollection getEffectCollection(){
		return effectCollection;
	}
}
