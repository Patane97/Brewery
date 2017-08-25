package com.Patane.Brewery.CustomEffects;

import java.lang.reflect.Field;

import org.bukkit.plugin.Plugin;

import com.Patane.Brewery.Brewery;
import com.Patane.Brewery.CustomEffects.types.EffectTypeInfo;
import com.Patane.Brewery.YML.BasicYML;

public class BrEffectYML extends BasicYML{

	public BrEffectYML(Plugin plugin) {
		super(plugin, "effects.yml", "effects");
	}

	@Override
	public void save() {
		for(BrEffect effect : Brewery.getEffectCollection().getAllItems()){
			String effectName = effect.getName();
			setHeader(createSection(effectName));
			// TYPE
			header.set("type", effect.getType().getClass().getAnnotation(EffectTypeInfo.class).name());
			for(Field field : effect.getType().getClass().getFields()){
				try {
					header.set(field.getName(), field.get(effect.getType()));
				} catch (IllegalArgumentException | IllegalAccessException e) {
					e.printStackTrace();
				}
			}
		}
	}

	@Override
	public void load() {
	}

}
