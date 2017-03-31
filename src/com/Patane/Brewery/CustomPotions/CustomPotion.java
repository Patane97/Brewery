package com.Patane.Brewery.CustomPotions;

import java.util.ArrayList;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import com.Patane.Brewery.CustomEffects.CustomEffect;


public class CustomPotion {
	Material potionMaterial;
	String name;
	//NBT TAG
	ArrayList<CustomEffect> customEffects;
	ItemStack item;
}
