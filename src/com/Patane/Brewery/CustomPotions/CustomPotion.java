package com.Patane.Brewery.CustomPotions;

import java.util.ArrayList;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import com.Patane.Brewery.BrCollectable;
import com.Patane.Brewery.CustomEffects.CustomEffect;


public class CustomPotion extends BrCollectable{
	public CustomPotion(String name) {
		// CONVERT NAME TO CORRECT ID FORMAT (ALL CAPS AND SPACES REPLACED WITH UNDERSCORES) [Maybe do this inside BrCollectable & BrCollection??]
		super(name);
	}
	Material potionMaterial;
	String name;
	//NBT TAG
	ArrayList<CustomEffect> customEffects;
	ItemStack item;
}
