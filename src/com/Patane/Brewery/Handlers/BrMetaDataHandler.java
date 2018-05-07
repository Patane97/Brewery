package com.Patane.Brewery.Handlers;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.entity.LivingEntity;

import com.Patane.handlers.MetaDataHandler;
import com.Patane.runnables.PatRunnable;
import com.Patane.runnables.PatTimedRunnable;
import com.Patane.util.general.StringsUtil;

public class BrMetaDataHandler extends MetaDataHandler{
	
//	public static boolean add(BrEffect effect, LivingEntity entity, String metaName, Object value){
//		String newMetaName = StringsUtil.normalize(effect.getID()+"-"+metaName);
//		return add(entity, newMetaName, value);
//	}
//	public static void addTimed(BrEffect effect, LivingEntity entity, String metaName, Object value, float duration){
//		String newMetaName = StringsUtil.normalize(effect.getID()+"-"+metaName);
//		addTimed(entity, newMetaName, value, duration);
//	}
//	public static boolean remove(BrEffect effect, LivingEntity entity, String metaName){
//		String newMetaName = StringsUtil.normalize(effect.getID()+"-"+metaName);
//		return remove(entity, newMetaName);
//	}
//	public static boolean remove(BrEffect effect, String metaName){
//		String newMetaName = StringsUtil.normalize(effect.getID()+"-"+metaName);
//		return remove(newMetaName);
//	}
//	public static Object getValue(BrEffect effect, LivingEntity entity, String metaName){
//		String newMetaName = StringsUtil.normalize(effect.getID()+"-"+metaName);
//		return getValue(entity, newMetaName);
//	}

	public static boolean add(PatRunnable task, LivingEntity entity, String metaName, Object value){
		return add(entity, packMetaName(task, metaName), value);
	}
	public static boolean add(PatRunnable task, List<LivingEntity> entities, String metaName, Object value){
		return add(entities, packMetaName(task, metaName), value);
	}
	public static boolean remove(PatRunnable task, LivingEntity entity, String metaName){
		return remove(entity, packMetaName(task, metaName));
	}
	public static boolean remove(PatRunnable task, String metaName){
		return remove(packMetaName(task, metaName));
	}
	public static boolean check(LivingEntity entity, String regex){
		return check(entity, "\\[\\d+\\]"+regex);
	}
	
	private static String packMetaName(PatRunnable task, String metaName){
		return StringsUtil.normalize("["+task.getID()+"]"+metaName+"");
	}
	
	/**
	 * Adds metadata to a list of entities. 
	 * Also removes entities that are in the metadatas collection List but not in this new 'added' list.
	 * This works well for things such as Lingering effects where it would usually trigger once every tick.
	 * @param task PatRunnable task needed to print unique ID on metadata.
	 * @param entities Entities which have the metadata.
	 * @param metaName Name portion of the metadata.
	 * @param value Value to be added within the metadata.
	 * @return
	 */
	public static boolean addClean(PatRunnable task, List<LivingEntity> entities, String metaName, Object value){
		// Firstly adds any entities which are not already on the collection List.
		boolean result = add(task, entities, metaName, value);
		
		// If the metaNames List is available.
		if(collection.get(packMetaName(task, metaName)) != null){
			// Loop through each entity on the collection list and if they are not in the newly added List, remove them from the collection List.
			for(LivingEntity entity : new ArrayList<LivingEntity>(collection.get(packMetaName(task, metaName))))
				if(!entities.contains(entity))
					remove(task, entity, metaName);
		}
		return result;
	}

	/**
	 * Checks if the entity already has another instance of the same task (eg. StickyTask from a Lingering effect).
	 * If this is the case, instead of adding the entity to yet another instance this simply resets the timer on
	 * the original instance.
	 * 
	 * @param task PatTimedRunnable task to attach to the metadata.
	 * @param entities Entities which have the metadata.
	 * @param metaName Name portion of the metadata.
	 */
	public static void addOrReset(PatTimedRunnable task, List<LivingEntity> entities, String metaName){
		
		// Loops through entities made from a cloned List.
		for(LivingEntity entity : new ArrayList<LivingEntity>(entities)){
			
			// If the entity is in within metadata collection found from using the given regex.
			if(BrMetaDataHandler.check(entity, "\\[\\d+\\]"+metaName)){
				try{
					// Grab the first metadata value (in this case, a PatTimedRunnable) stored within the entity from using the given regex.
					PatTimedRunnable storedTask = (PatTimedRunnable) BrMetaDataHandler.grabFirst(entity, "\\[\\d+\\]"+metaName);
					
					// Reset the tasks timer.
					storedTask.reset();
					
					// Remove the entity from the origial List so they arent added into the new Task
					entities.remove(entity);
				} 
				// In the rare case that the grabbed Object from 'grabFirst' isnt a PatTimedRunnable.
				catch (ClassCastException e){
					BrMetaDataHandler.add(task, entity, metaName, task);
				}
			}
			// If they do not have any metadata yet, then add them to the collection and give them the relevant metadata.
			else
				BrMetaDataHandler.add(task, entity, metaName, task);
		}
	}
}
