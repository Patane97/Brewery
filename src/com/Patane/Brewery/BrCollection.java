package com.Patane.Brewery;

import java.util.ArrayList;
import java.util.HashMap;

public class BrCollection<T extends BrCollectable> {
	private HashMap<String, T> collection = new HashMap<String, T>();
	
	public T add(T newItem){
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
		return collection.get(id);
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
