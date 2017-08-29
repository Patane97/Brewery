package com.Patane.Brewery.YML;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Particle;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.Plugin;

import com.Patane.Brewery.Messenger;
import com.Patane.Brewery.Messenger.Msg;
import com.Patane.Brewery.YMLParsable;
import com.Patane.Brewery.CustomEffects.BrEffect.BrParticleEffect;
import com.Patane.Brewery.util.ErrorHandler;
import com.Patane.Brewery.util.ErrorHandler.BrLoadException;
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
	public int getIntFromString(boolean optional, String string, String name, String superName) throws BrLoadException{
		if(string == null){
			return ErrorHandler.optionalLoadError(Msg.WARNING, optional, "Failed to load "+superName+": There is no value or field set for '"+name+"'");
		}
		int integer;
		try{
			integer = Math.round(Float.parseFloat(string));
		}catch (NumberFormatException e){
			return ErrorHandler.optionalLoadError(Msg.WARNING, optional, "Failed to load "+superName+": Invalid value for '"+name+"' (Must be numerical)");
		}
		return integer;
	}
	public float getFloatFromString(boolean optional, String string, String name, String superName) throws BrLoadException{
		if(string == null){
			return ErrorHandler.optionalLoadError(Msg.WARNING, optional, "Failed to load "+superName+": There is no value or field set for '"+name+"'");
		}
		float integer;
		try{
			integer = Float.parseFloat(string);
		}catch (NumberFormatException e){
			return ErrorHandler.optionalLoadError(Msg.WARNING, optional, "Failed to load "+superName+": Invalid value for '"+name+"' (Must be numerical)");
		}
		return integer;
	}
	/**
	 * Extremely useful method to extract data from the YML and inject it into a new Class instance via reflection.
	 * @param clazz Class to create an instance from
	 * @param name String of class being searched for (for error handling) 
	 * @param superName String of what this method is being used for (for error handling) 
	 * @param optional Check to see if this value is optional (for error handling)
	 * @param path Where the classes information is stored within the YML file
	 * @return A new instance of the given class or Null if there is any sort of error and optional is true.
	 * @throws BrLoadException If there is any sort of error and optional is false
	 */
	public <T extends YMLParsable> T getByClass(Class<? extends T> clazz, String name, String superName, boolean optional, String...path) throws BrLoadException{
		setHeader(path);
		String typeName = header.getString("type");
		if(typeName == null)
			return ErrorHandler.optionalLoadError(Msg.WARNING, optional, "Failed to load "+superName+": Missing 'type' field for "+name+".");
		if(clazz  == null)
			return ErrorHandler.optionalLoadError(Msg.WARNING, optional, "Failed to load "+superName+": '"+typeName+"' not recognised as a valid "+name+".");
		Map<String, String> entries = new HashMap<String, String>();
		//Getting class Fields as Strings
		List<String> fields = new ArrayList<String>();
		for(Field field : clazz.getFields())
			fields.add(field.getName());
		//Defining fields that will need to be removed from YML file (to keep clean)
		List<String> fieldsToRemove = new ArrayList<String>();
		//Loops through YML keys, determining if they are to be used or removed later
		for(String key : header.getKeys(false)){
			if(fields.contains(key))
				entries.put(key, header.getString(key));
			else if(!key.equals("type"))
				fieldsToRemove.add(key);
		}//Building with unique field/values from 'entries'
		T object;
		try { 
			object = clazz.getConstructor(Map.class).newInstance(entries);
		} catch (NoSuchMethodException e) {
			try {object = clazz.getConstructor().newInstance();}
			catch (Exception e1) {
				return ErrorHandler.optionalLoadError(Msg.WARNING, optional, "Failed to load "+superName+": "+e.getCause().getMessage());
			}
		} catch (InvocationTargetException e) {
			return ErrorHandler.optionalLoadError(Msg.WARNING, optional, "Failed to load "+superName+": "+e.getCause().getMessage());
		} catch (Exception e){
			Messenger.warning("Failed to load "+superName+": "+e.getCause().getMessage());
			e.printStackTrace();
			return null;
		}
		//Removing invalid fields from YML
		if(!fieldsToRemove.isEmpty()){
			for(String removingField : fieldsToRemove)
				header.set(removingField, null);
			Messenger.warning("Removed the following invalid fields for "+superName+"'s "+object.name()+" "+name+": "+StringUtilities.stringJoiner(fieldsToRemove, ", "));
			config.save();
		}
		return object;
	}
	public <T extends Enum<T>> T getEnumFromString(Class<T> clazz, String string, String name, String superName, boolean optional) throws BrLoadException{
		if(string == null)
			return ErrorHandler.optionalLoadError(Msg.WARNING, optional, "Failed to load "+superName+": Missing field or value for '"+name+"'");
		T object;
		try{ 
			object = T.valueOf(clazz, string);
		} catch(IllegalArgumentException e){
			return ErrorHandler.optionalLoadError(Msg.WARNING, false, "Failed to load "+superName+": '"+string+"' not recognised as a valid "+name+".");
		}
		return object;
	}
	public BrParticleEffect getParticleEffect(String superName, boolean optional, String... path) throws BrLoadException{
		if(optional && !isSection(path))
			return null;
		setHeader(path);
		String typeName = header.getString("type");
		try{
			Particle particle = Particle.valueOf(typeName);
			int intensity = header.getInt("intensity");
			double velocity = header.getDouble("velocity");

			Messenger.debug(Msg.INFO, "     + Particle Effect["+particle+", "+intensity+", "+velocity+"]");
			return new BrParticleEffect(particle, intensity, velocity);
		} catch(IllegalArgumentException e){
			return ErrorHandler.optionalLoadError(Msg.WARNING, optional, "Failed to load "+superName+" particles: "+e.getCause().getMessage());
		} catch(NullPointerException e){
			return ErrorHandler.optionalLoadError(Msg.WARNING, optional, "Failed to load "+superName+" particles: "+e.getCause().getMessage());
		}
	}
}
