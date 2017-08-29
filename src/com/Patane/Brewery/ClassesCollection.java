package com.Patane.Brewery;

import java.util.HashMap;

public class ClassesCollection <T extends Nameable>{
	private HashMap<String, T> items;
	public T get(String name){
		for(String currentName : items.keySet()){
			if(name.contains(currentName))
				return items.get(currentName);
		}
		return null;
	}
	@SuppressWarnings("unchecked")
	public void registerAll(Class< ? extends T>... clazzes) {
		items = new HashMap<String, T>();
		for(Class< ? extends T> clazz : clazzes){
			register(clazz);
		}
	}
	public void register(Class< ? extends T> clazz){
		Namer info = clazz.getAnnotation(Namer.class);
		if(info == null) {
			return;
		}
		try {
			items.put(info.name(), clazz.newInstance());
		} catch (InstantiationException | IllegalAccessException e) {
			e.printStackTrace();
		}

	}
}
