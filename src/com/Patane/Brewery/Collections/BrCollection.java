package com.Patane.Brewery.Collections;

import java.util.ArrayList;
import java.util.HashMap;

import com.Patane.Brewery.Messenger;
import com.Patane.Brewery.Messenger.ChatType;

public class BrCollection<T extends BrCollectable> {
	private HashMap<String, T> collection = new HashMap<String, T>();
	
	public T add(T newItem){
		
		Messenger.debug(ChatType.INFO, "Adding "+newItem.getID()+" to Collection");
		return collection.put(newItem.getID(), newItem);
	}
	public T remove(String id){
		T removed = collection.remove(id);
		if(removed != null){
			// If element was removed,
			// Do something
		}
		return removed;
	}
	public T getItem(String id){
		return collection.get(id.replace(" ", "_").toUpperCase());
	}
	public ArrayList<T> getAllItems(){
		return new ArrayList<T>(collection.values());
	}
	public ArrayList<String> getAllIDs(){
		return new ArrayList<String>(collection.keySet());
	}
	public boolean contains(String id){
		return collection.keySet().contains(id);
	}
}
