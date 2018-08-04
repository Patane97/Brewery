package com.Patane.Brewery;

import org.bukkit.plugin.java.JavaPlugin;

import com.Patane.Brewery.Collections.BrEffectCollection;
import com.Patane.Brewery.Collections.BrItemCollection;
import com.Patane.Brewery.Commands.BrCommandHandler;
import com.Patane.Brewery.Cooldowns.CooldownHandler;
import com.Patane.Brewery.CustomEffects.BrEffect;
import com.Patane.Brewery.CustomEffects.BrEffectYML;
import com.Patane.Brewery.CustomItems.BrItem;
import com.Patane.Brewery.CustomItems.BrItemYML;
import com.Patane.Brewery.Handlers.FormationHandler;
import com.Patane.Brewery.Handlers.ModifierHandler;
import com.Patane.Brewery.Handlers.TriggerHandler;
import com.Patane.Brewery.Listeners.GenericPacketAdapter;
import com.Patane.Brewery.Listeners.GlobalListener;
import com.Patane.Brewery.Sequencer.Sequencer;
import com.Patane.Brewery.Sequencer.SequencesYML;
import com.Patane.util.YAML.types.YAMLData;
import com.Patane.util.general.Messenger;
import com.Patane.util.main.PataneUtil;
import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.ListenerPriority;

/*
 * @author Patane
 */

public class Brewery extends JavaPlugin{
	
	private static Brewery brewery;
	private static BrItemCollection itemCollection;
	private static BrEffectCollection effectCollection;
	
	private ProtocolManager protocolManager;
	
	public void onEnable() {
		brewery = this;
		PataneUtil.setup(brewery, true);
		itemCollection = new BrItemCollection();
		effectCollection = new BrEffectCollection();
		getServer().getPluginManager().registerEvents(new GlobalListener(), this);
		// Call 'BrCommandHandler.getInstance()' to get CommandHandler instance.
		this.getCommand("br").setExecutor(new BrCommandHandler());
		
		TriggerHandler.registerAll();
		ModifierHandler.registerAll();
		FormationHandler.registerAll();
		
		protocolManager = ProtocolLibrary.getProtocolManager();
		protocolManager.addPacketListener(new GenericPacketAdapter(ListenerPriority.NORMAL, PacketType.Play.Client.SET_CREATIVE_SLOT));
		
		loadFiles();
		
		CooldownHandler.onLoadChecks();
		
		// Loading message
		Messenger.info("Brewery version " + this.getDescription().getVersion() + " successfully loaded!");
	}
	public void onDisable() {
		saveFiles();
	}
	private void saveFiles() {
//		BrItem.YML().save();
//		BrEffect.YML().save();
	}
	private void loadFiles() {
		BrEffect.setYML(new BrEffectYML());
		BrEffect.YML().load();
		BrItem.setYML(new BrItemYML());
		BrItem.YML().load();
		Sequencer.setYML(new SequencesYML());
		Sequencer.YML().load();
		// Data YAML's
		CooldownHandler.setYML(new YAMLData("data", "cooldowns"));
	}
	public static Brewery getInstance() {
		return brewery;
	}
	public static BrItemCollection getItemCollection() {
		return itemCollection;
	}
	public static BrEffectCollection getEffectCollection() {
		return effectCollection;
	}
}
