package com.Patane.Brewery.collections;

import java.util.ArrayList;
import java.util.HashMap;

import com.Patane.Brewery.CustomPotions.CustomPotion;
import com.Patane.Brewery.CustomPotions.classes.BrTag;

public class CustomPotions {
	private static HashMap<BrTag, CustomPotion> potions = new HashMap<BrTag, CustomPotion>();

	public static void addAll(ArrayList<CustomPotion> potionList){
		for(CustomPotion selectedPotion : potionList)
			potions.put(selectedPotion.getBrTag(), selectedPotion);
	}
	public static void add(CustomPotion potion){
		potions.put(potion.getBrTag(), potion);
	}
	public static boolean remove(CustomPotion potion){
		if(potions.remove(potion.getBrTag()) != null)
			return true;
		return false;
	}
	public static CustomPotion grab(BrTag brTag){
		for(BrTag selectedTag : potions.keySet()){
			if(brTag.equals(selectedTag))
				return potions.get(selectedTag);
		}
		return null;
	}
	public static CustomPotion grab(String potionName){
		for(CustomPotion selectedPotion : potions.values()){
			if(selectedPotion.getName().equalsIgnoreCase(potionName))
				return selectedPotion;
		}
		return null;
	}
}
