package com.Patane.Brewery.CustomEffects;

import java.util.Map;

import org.bukkit.Location;

import com.Patane.util.general.StringsUtil.LambdaStrings;
import com.Patane.util.ingame.Focusable;

public abstract class Formation extends Focusable{

	public Formation() {
		super();
	}
	
	public Formation(Map<String, String> fields) {
		super(fields);
	}
	
	public Formation(Focus focus) {
		super(focus);
	}
	
	@Override
	public LambdaStrings layout(){
		// Example: &2Type: &7Name
		return s -> "&2"+s[0]+"&2: &7"+s[1];
	}

	public abstract void form(BrEffect effect, Location location);

	
}
