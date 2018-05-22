package com.Patane.Brewery.Handlers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.Patane.Brewery.CustomEffects.Formation;
import com.Patane.Brewery.CustomEffects.formations.Entity;
import com.Patane.Brewery.CustomEffects.formations.Face;
import com.Patane.Brewery.CustomEffects.formations.Point;
import com.Patane.Brewery.CustomEffects.formations.Radius;
import com.Patane.handlers.PatHandler;
import com.Patane.util.YML.Namer;
import com.Patane.util.general.Check;
import com.Patane.util.general.Messenger;
import com.Patane.util.general.Messenger.Msg;
import com.Patane.util.general.StringsUtil;

public class FormationHandler implements PatHandler{
private static HashMap<String, Formation> formations;
	
	public static Formation get(String itemName){
		Check.nulled(itemName, "Formation field is missing");
		for(String name : formations.keySet()){
			if(itemName.equals(name))
				return formations.get(name);
		}
		return null;
	}
	public static void registerAll() {
		formations = new HashMap<String, Formation>();
		register(new Radius());
		register(new Face());
		register(new Point());
		register(new Entity());
		Messenger.debug(Msg.INFO, "Registered Formations: "+StringsUtil.stringJoiner(formations.keySet(), ", "));
	}
	private static void register(Formation formation){
		Namer info = formation.getClass().getAnnotation(Namer.class);
		if(info == null){
			Messenger.warning("Failed to register Formation '"+formation.getClass().getSimpleName()+".class': Missing annotation!");
			return;
		}
		formations.put(info.name(), formation);
	}
	public static List<String> getKeys() {
		return new ArrayList<String>(formations.keySet());
	}
}
