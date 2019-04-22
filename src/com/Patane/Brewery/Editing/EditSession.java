package com.Patane.Brewery.Editing;

import java.util.HashMap;
import java.util.Map;

import com.Patane.Brewery.Brewery;
import com.Patane.util.collections.PatCollectable;
import com.Patane.util.general.Messenger;

public class EditSession {
	private static Map<String, PatCollectable> editing = new HashMap<String, PatCollectable>();
	
	public static void reset() {
		for(String playerName : editing.keySet())
			end(playerName);
	}
	
	public static void start(String name, PatCollectable object) {
		editing.put(name, object);
		try {
			Messenger.send(Brewery.getInstance().getServer().getPlayer(name), "&2Edit Session: &aYou are now editing &7"+object.getName()+"&a.");
		} catch (Exception e) {}
	}
	public static void end(String name) {
		try {
			Messenger.send(Brewery.getInstance().getServer().getPlayer(name), "&2Edit Session: &aYou are no longer editing.");
		} catch (Exception e) {}
		editing.remove(name);
	}
	
	public static boolean active(String name) {
		return editing.containsKey(name);
	}
	
	public static PatCollectable get(String name) {
		return editing.get(name);
	}
}
