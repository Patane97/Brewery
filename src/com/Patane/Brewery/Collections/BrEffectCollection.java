package com.Patane.Brewery.Collections;

import java.util.ArrayList;
import java.util.List;

import com.Patane.Brewery.CustomEffects.BrEffect;
import com.Patane.util.collections.PatCollection;

public class BrEffectCollection extends PatCollection<BrEffect>{
	// Effects are processed to avoid any infinite loops primarily found when using the 'Effect' Modifier.
	private static List<String> currentlyProcessing = new ArrayList<String>();
	
	public static void addProcessing(String effectName) {
		currentlyProcessing.add(effectName);
	}
	public static void delProcessing(String effectName) {
		currentlyProcessing.remove(effectName);
	}
	public static boolean isProcessing(String effectName) {
		return currentlyProcessing.contains(effectName);
	}
}
