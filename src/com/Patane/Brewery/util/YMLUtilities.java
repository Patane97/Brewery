package com.Patane.Brewery.util;

import org.bukkit.entity.EntityType;

public class YMLUtilities {
	public static String[] getEntityTypeNames(EntityType[] entityTypes){
		String[] result = new String[entityTypes.length];
		for(int i=0 ; i<entityTypes.length ; i++){
			result[i] = entityTypes[i].name();
		}
		return result;
	}
}
