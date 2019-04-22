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
import com.Patane.Brewery.Editing.EditSession;
import com.Patane.Brewery.Handlers.FormationHandler;
import com.Patane.Brewery.Handlers.ModifierHandler;
import com.Patane.Brewery.Handlers.TriggerHandler;
import com.Patane.Brewery.Listeners.GlobalListener;
import com.Patane.util.YAML.types.YAMLData;
import com.Patane.util.general.Messenger;
import com.Patane.util.main.PataneUtil;

/*
 * @author Patane
 */

public class Brewery extends JavaPlugin{
	
	private static Brewery brewery;
	private static BrItemCollection itemCollection;
	private static BrEffectCollection effectCollection;
	private static GlobalListener globalListener;
	
	public void onEnable() {
		brewery = this;
		PataneUtil.setup(brewery, true, "&2[&aBrewery&2]&r ", "&2[&aBR&2]&r ");
		
		this.getCommand("br").setExecutor(new BrCommandHandler());

		
		globalListener = new GlobalListener();
		
		TriggerHandler.registerAll();
		ModifierHandler.registerAll();
		FormationHandler.registerAll();
		
		loadPlugin();
	}
	public void unLoadPlugin() {
		EditSession.reset();
	}
	
	public void loadPlugin() {
		
		itemCollection = new BrItemCollection();
		effectCollection = new BrEffectCollection();
		
		loadFiles();
		
		CooldownHandler.onLoadChecks();
		
		Messenger.info("Brewery version " + this.getDescription().getVersion() + " successfully loaded!");
	}
	public void onDisable() {
		unLoadPlugin();
//		saveFiles();
	}
	@SuppressWarnings("unused")
	private void saveFiles() {
		try {
			BrItem.YML().save();
		} catch (IllegalStateException e) {}
		try {
			BrEffect.YML().save();
		} catch (IllegalStateException e) {}
	}
	private void loadFiles() {
		try {
			BrEffect.setYML(new BrEffectYML());
			BrEffect.YML().load();
		} catch(Exception e) {
			e.printStackTrace();
		}
		try {
			BrItem.setYML(new BrItemYML());
			BrItem.YML().load();
		} catch(Exception e) {
			e.printStackTrace();
		}
		try {
			CooldownHandler.setYML(new YAMLData("cooldowns", "data"));
		} catch(Exception e) {
			e.printStackTrace();
		}
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
	public static GlobalListener getGlobalListener() {
		return globalListener;
	}
}
