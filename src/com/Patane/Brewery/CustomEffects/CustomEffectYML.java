package com.Patane.Brewery.CustomEffects;

import org.bukkit.plugin.Plugin;

import com.Patane.Brewery.Brewery;
import com.Patane.Brewery.YML.BasicYML;

public class CustomEffectYML extends BasicYML{

	public CustomEffectYML(Plugin plugin) {
		super(plugin, "effects.yml", "effects");
	}

	@Override
	public void save() {
		for(CustomEffect effect : Brewery.getEffectCollection().getAllItems()){
			String effectName = effect.getName();
			setHeader(clearCreateSection(effectName));
			// TYPE
			
		}
	}

	@Override
	public void load() {
		
	}

}
