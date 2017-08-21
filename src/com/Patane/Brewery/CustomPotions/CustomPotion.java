package com.Patane.Brewery.CustomPotions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;

import com.Patane.Brewery.BrCollectable;
import com.Patane.Brewery.Brewery;
import com.Patane.Brewery.Messenger;
import com.Patane.Brewery.CustomEffects.CustomEffect;
import com.Patane.Brewery.util.BrItem;


public class CustomPotion extends BrCollectable{
	Material potionMaterial;
	String name;
	//NBT TAG
	Map<CustomEffect, LivingEntity[]> effectPerEntities = new HashMap<CustomEffect, LivingEntity[]>();
	ArrayList<CustomEffect> customEffects;
	ItemStack item;
	
	public CustomPotion(String name, Material potionMaterial, Map<CustomEffect, LivingEntity[]> effectPerEntities){
		super(name);
		this.item = new ItemStack(potionMaterial, 1);
		if(!(item.getItemMeta() instanceof PotionMeta)){
			Messenger.warning("CustomPotion '"+name+"' does not have a PotionMeta");
			return;
		}		
		ItemMeta itemMeta = item.getItemMeta();
		itemMeta.setDisplayName(name + BrItem.encodeItemData(" <Br-" + getID() + ">"));
		item.setItemMeta(itemMeta);
		this.effectPerEntities = effectPerEntities;
//		this.customEffects = new ArrayList<CustomEffect>(Arrays.asList(customEffects));
		
		Brewery.getCustomPotions().add(this);
	}
	public ItemStack getItem(){
		return item;
	}
	public void execute(LivingEntity shooter, Location location){
		for(CustomEffect customEffect : effectPerEntities.keySet()){
			customEffect.execute(shooter, location, effectPerEntities.get(customEffect));
		}
//		for(CustomEffect customEffect : customEffects){
//			customEffect.execute(shooter, location);
//		}
	}
}
