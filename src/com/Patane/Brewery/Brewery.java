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
	
	private static BrCommandHandler commandHandler;
	
	private ProtocolManager protocolManager;
	
	public void onEnable() {
		brewery = this;
		PataneUtil.setup(brewery, true);
		itemCollection = new BrItemCollection();
		effectCollection = new BrEffectCollection();
		getServer().getPluginManager().registerEvents(new GlobalListener(), this);
        commandHandler = new BrCommandHandler();
		this.getCommand("br").setExecutor(commandHandler);
		
		TriggerHandler.registerAll();
		ModifierHandler.registerAll();
		FormationHandler.registerAll();
		
		protocolManager = ProtocolLibrary.getProtocolManager();
		protocolManager.addPacketListener(new GenericPacketAdapter(ListenerPriority.NORMAL, PacketType.Play.Client.SET_CREATIVE_SLOT));
		loadFiles();
		/////////////////////////
//		List<EffectContainer> effects = new ArrayList<EffectContainer>();
//		effects.add(new EffectContainer(new BrEffect("Life Drainer", new Modifier.Damage(DamageCause.FIRE, 5), 3), 
//				new Instant(), 
//				EntityType.SKELETON, EntityType.ZOMBIE));
//		effects.add(new EffectContainer(new BrEffect("Mark of Light", new Modifier.Heal(2), 3, 
//						new PotionEffect(PotionTrigger.GLOWING, 20, 1)), 
//				new Lingering(5f, 0.5f), 
//				EntityType.PLAYER));
//		new BrItem("Vampiric Scepter", CustomType.THROWABLE, ItemsUtil.createItem(Material.SPLASH_POTION, 1, (short) 0, "&6Vampiric Scepter", "&7Damages Undead", "&7heals players"), effects);
		/////////////////////////

//		BrItem.YML().save();
//		BrEffect.YML().save();
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
	public static BrCommandHandler getCommandHandler() {
		return commandHandler;
	}
}
