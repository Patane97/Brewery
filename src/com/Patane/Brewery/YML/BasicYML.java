package com.Patane.Brewery.YML;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.Plugin;

import com.Patane.Brewery.util.StringUtilities;

public abstract class BasicYML {
	protected Plugin plugin;
	protected Config config;
	protected String root;
	protected ConfigurationSection header;
	
	public BasicYML(Plugin plugin, String config, String root){
		this.plugin = plugin;
		this.config = new Config(plugin, config);
		this.root = root;
		if(!isRootSection())
			createRootSection();
		this.config.save();
		this.header = getRootSection();
	}
	
	public abstract void save();
	public abstract void load();
	
	// DEFINE EACH OF THESE. You know what it does but will others? :)
	
	protected ConfigurationSection createRootSection() {
		return config.createSection(root);
	}
	public ConfigurationSection createSection(String...strings) {
		String path = (strings.length > 1 ? StringUtilities.stringJoiner(strings, ".") : strings[0]);
		if(isSection(path))
			return getSection(path);
		return config.createSection(root + "." + path);
	}
	public ConfigurationSection clearCreateSection(String...strings) {
		String path = (strings.length > 1 ? StringUtilities.stringJoiner(strings, ".") : strings[0]);
		clearSection(path);
		return config.createSection(root + "." + path);
	}
	public void setHeader(String...strings) {
		header = createSection(strings);
	}
	public void setHeader(ConfigurationSection section) {
		header = section;
	}
	public ConfigurationSection getHeader(){
		return header;
	}
	public boolean isRootSection() {
		return config.isConfigurationSection(root);
	}
	public boolean isSection(String...strings){
		String path = (strings.length > 1 ? StringUtilities.stringJoiner(strings, ".") : strings[0]);
		boolean section = config.isConfigurationSection(root + "." + path);
		// If it isnt a configuration section, return whether it has a value set.
		return (!section ? config.isSet(root + "." + path) : section);
	}
	public ConfigurationSection getRootSection() {
		return config.getConfigurationSection(root);
	}
	public ConfigurationSection getSection(String...strings) {
		String path = (strings.length > 1 ? StringUtilities.stringJoiner(strings, ".") : strings[0]);
		return config.getConfigurationSection(root + "." + path);
	}
	public boolean isEmpty(String...strings) {
		String path = (strings.length > 1 ? StringUtilities.stringJoiner(strings, ".") : strings[0]);
		boolean empty;
		try{
			empty = getSection(path).getKeys(false).isEmpty();
		} catch (NullPointerException e){
			empty = true;
		}
		// If it is empty (or holds a value instead of keys), return whether it has a value in its path.
		return (empty ? !config.isSet(root + "." + path) : empty);
	}
	public void clearSection(String...strings) {
		String path = (strings.length > 1 ? StringUtilities.stringJoiner(strings, ".") : strings[0]);
		getRootSection().set(path, null);
		config.save();
	}
	public void checkEmptyClear(String...strings) {
		if(isEmpty(strings))
			clearSection(strings);
	}
	public void clearRoot(){
		for(String paths : getRootSection().getKeys(true)){
			getRootSection().set(paths, null);
		}
	}
}
