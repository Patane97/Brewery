package com.Patane.Brewery.YML;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.EntityType;
import org.bukkit.plugin.Plugin;

import com.Patane.Brewery.Brewery;
import com.Patane.Brewery.Messenger;
import com.Patane.Brewery.Messenger.Msg;
import com.Patane.Brewery.YMLParsable;
import com.Patane.Brewery.CustomEffects.BrEffect;
import com.Patane.Brewery.CustomEffects.EffectType;
import com.Patane.Brewery.CustomEffects.EffectTypeHandler;
import com.Patane.Brewery.CustomItems.BrItem.EffectContainer;
import com.Patane.Brewery.util.ErrorHandler;
import com.Patane.Brewery.util.ErrorHandler.BrLoadException;
import com.Patane.Brewery.util.ErrorHandler.Importance;
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
		path = (path.startsWith(root + ".") ? path : root + "." + path);
		if(isSection(path))
			return getSection(path);
		return config.createSection(path);
	}
	public ConfigurationSection clearCreateSection(String...strings) {
		String path = (strings.length > 1 ? StringUtilities.stringJoiner(strings, ".") : strings[0]);
		path = (path.startsWith(root + ".") ? path : root + "." + path);
		clearSection(path);
		return config.createSection(path);
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
		path = (path.startsWith(root + ".") ? path : root + "." + path);
		boolean section = config.isConfigurationSection(path);
		// If it isnt a configuration section, return whether it has a value set.
		return (!section ? config.isSet(path) : section);
	}
	public ConfigurationSection getRootSection() {
		return config.getConfigurationSection(root);
	}
	public ConfigurationSection getSection(String...strings) {
		String path = (strings.length > 1 ? StringUtilities.stringJoiner(strings, ".") : strings[0]);
		path = (path.startsWith(root + ".") ? path : root + "." + path);
		return config.getConfigurationSection(path);
	}
	public boolean isEmpty(String...strings) {
		String path = (strings.length > 1 ? StringUtilities.stringJoiner(strings, ".") : strings[0]);
		path = (path.startsWith(root + ".") ? path : root + "." + path);
		boolean empty;
		try{
			empty = getSection(path).getKeys(false).isEmpty();
		} catch (NullPointerException e){
			empty = true;
		}
		// If it is empty (or holds a value instead of keys), return whether it has a value in its path.
		return (empty ? !config.isSet(path) : empty);
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
	////////////////////////////////////// GETTERS //////////////////////////////////////
	public Integer getIntFromString(Importance importance, String string, String name, String superName) throws BrLoadException{
		if(string == null){
			return ErrorHandler.optionalLoadError(Msg.WARNING, importance, "Failed to load "+superName+": There is no value or field set for '"+name+"'");
		}
		Integer integer = null;
		try{
			integer = Math.round(Float.parseFloat(string));
		}catch (NumberFormatException e){
			return ErrorHandler.optionalLoadError(Msg.WARNING, importance, "Failed to load "+superName+": Invalid value for '"+name+"' (Must be numerical)");
		}
		return integer;
	}
	public Float getFloatFromString(Importance importance, String string, String name, String superName) throws BrLoadException{
		if(string == null){
			return ErrorHandler.optionalLoadError(Msg.WARNING, importance, "Failed to load "+superName+": There is no value or field set for '"+name+"'");
		}
		Float integer = null;
		try{
			integer = Float.parseFloat(string);
		}catch (NumberFormatException e){
			return ErrorHandler.optionalLoadError(Msg.WARNING, importance, "Failed to load "+superName+": Invalid value for '"+name+"' (Must be numerical)");
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
	public <T extends YMLParsable> T getByClass(Importance importance, Class<? extends T> clazz, String name, String superName, ConfigurationSection section, String masterField) throws BrLoadException{
		if(section == null || !isSection(section.getCurrentPath())){
			return ErrorHandler.optionalLoadError(Msg.WARNING, Importance.ERROR, "Failed to find '"+name+"': path cannot be found.");
		}
		if(header != section)
			setHeader(section);
		String typeName = header.getString(masterField);
		if(typeName == null)
			return ErrorHandler.optionalLoadError(Msg.WARNING, importance, "Failed to load "+superName+": Missing '"+masterField+"' field for "+name+".");
		if(clazz  == null)
			return ErrorHandler.optionalLoadError(Msg.WARNING, importance, "Failed to load "+superName+": '"+typeName+"' not recognised as a valid "+name+".");
		
		//Getting class Fields as Strings
		List<String> fieldNames = new ArrayList<String>();
		for(Field field : clazz.getFields())
			fieldNames.add(field.getName());
		Map<String, String> entries = new HashMap<String, String>();
		//Defining fields that will need to be removed from YML file (to keep clean)
		List<String> fieldsToRemove = new ArrayList<String>();
		//Loops through YML keys, determining if they are to be used or removed later
		for(String key : header.getKeys(false)){
			if(fieldNames.contains(key)){
				entries.put(key, header.getString(key));
			} else if(!key.equals(masterField))
				fieldsToRemove.add(key);
		}//Building with unique field/values from 'entries'
		T object;
		try { 
			object = clazz.getConstructor(Map.class).newInstance(entries);
		} catch (NoSuchMethodException e) {
			try {object = clazz.getConstructor().newInstance();}
			catch (Exception e1) {
				return ErrorHandler.optionalLoadError(Msg.WARNING, importance, "Failed to load "+superName+": "+e.getCause().getMessage());
			}
		} catch (InvocationTargetException e) {
			return ErrorHandler.optionalLoadError(Msg.WARNING, importance, "Failed to load "+superName+": "+e.getCause().getMessage());
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
		Messenger.debug(Msg.INFO, "     + "+name+"[("+typeName+") "+StringUtilities.stringJoiner(entries.values(), ", ")+"]");
		return object;
	}
	public <T extends Enum<T>> T getEnumFromString(Importance importance, Class<T> clazz, String string, String name, String superName) throws BrLoadException{
		if(string == null)
			return ErrorHandler.optionalLoadError(Msg.WARNING, importance, "Failed to load "+superName+": Missing field or value for '"+name+"'");
		T object;
		try{ 
			object = T.valueOf(clazz, string);
		} catch(IllegalArgumentException e){
			return ErrorHandler.optionalLoadError(Msg.WARNING, importance, "Failed to load "+superName+": '"+string+"' not recognised as a valid "+name+".");
		}
		return object;
	}
	public EffectContainer grabEffectContainer(Importance importance, ConfigurationSection section, String effectName) throws BrLoadException{
		try{
			String path = section.getCurrentPath();
			if(!effectName.equals(effectName.replace(" ", "_").toUpperCase()))
				ErrorHandler.optionalLoadError(Msg.WARNING, Importance.REQUIRED, "Failed to load "+effectName+" effect container: Name must be in upper case with no spacing, eg. '"+effectName.replace(" ", "_").toUpperCase()+"'");
			
			//BREFFECT
			BrEffect effect = Brewery.getEffectCollection().getItem(effectName);
			if(effect == null)
				ErrorHandler.optionalLoadError(Msg.WARNING, Importance.REQUIRED, "Failed to load "+effectName+" effect container: Effect does not exist (Did it fail to load?)");
			setHeader(path, effectName);
			//RADIUS
			int radius = getDefault(effect.getDefaultInfo().getRadius(), getIntFromString(Importance.NONE, header.getString("radius"), "radius", effectName+" effect"));
			
			//EFFECTTYPE
			setHeader(path, effectName, "trigger");
			String effectTypeName = header.getString("type");
			EffectType effectType = getDefault(effect.getDefaultInfo().getType(), getByClass(Importance.REQUIRED, EffectTypeHandler.get(effectTypeName), "trigger", effectName+" effect container", header, "type"));
			//ENTITIES
			List<EntityType> entities = null;
			if(isSection(path, effectName, "entities")){
				setHeader(path, effectName);
				entities = new ArrayList<EntityType>();
				for(String entityName : header.getStringList("entities")){
					try{
						EntityType entityType = getEnumFromString(Importance.REQUIRED, EntityType.class, entityName, "entity type", "a "+effectName+" effect entity");
						entities.add(entityType);
					} catch (BrLoadException e){
						Messenger.warning(e.getMessage());
					}
				}
			}
			EntityType[] entitiesArray = getDefault(effect.getDefaultInfo().getEntities(), entities.toArray(new EntityType[0]));
			return new EffectContainer(effect, radius, effectType, entitiesArray);
		} catch (BrLoadException e){
			if(importance == Importance.REQUIRED)
				throw e;
			Messenger.warning(e.getMessage());
			return null;
		}
	}
	public<T> T getDefault(T defaultValue, T value){
		if(value == null)
			return defaultValue;
		return value;
	}
}
