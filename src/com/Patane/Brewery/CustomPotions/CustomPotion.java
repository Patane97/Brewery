package com.Patane.Brewery.CustomPotions;

import java.util.ArrayList;
<<<<<<< HEAD
import java.util.Arrays;
=======
>>>>>>> origin/master
import java.util.HashMap;

import org.bukkit.Location;
import org.bukkit.Material;
<<<<<<< HEAD
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
	HashMap<CustomEffect, LivingEntity[]> EffectPerEntity = new HashMap<CustomEffect, LivingEntity[]>();
	ArrayList<CustomEffect> customEffects;
	ItemStack item;
	
	public CustomPotion(String name, Material potionMaterial, CustomEffect... customEffects){
		super(name);
		this.item = new ItemStack(potionMaterial, 1);
		if(!(item.getItemMeta() instanceof PotionMeta)){
			Messenger.warning("CustomPotion '"+name+"' does not have a PotionMeta");
			return;
		}		
		ItemMeta itemMeta = item.getItemMeta();
		itemMeta.setDisplayName(name + BrItem.encodeItemData(" <Br-" + getID() + ">"));
		item.setItemMeta(itemMeta);
		this.customEffects = new ArrayList<CustomEffect>(Arrays.asList(customEffects));
		
		Brewery.getCustomPotions().add(this);
	}
	public ItemStack getItem(){
		return item;
	}
	public void execute(LivingEntity shooter, Location location){
		for(CustomEffect customEffect : customEffects){
			customEffect.execute(shooter, location);
=======
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionType;

import com.Patane.Brewery.Chat;
import com.Patane.Brewery.Messenger;
import com.Patane.Brewery.CustomEffects.CustomEffect;
import com.Patane.Brewery.CustomPotions.classes.BrTag;
import com.Patane.Brewery.util.BrItem;
import com.Patane.Brewery.util.Locations;


public class CustomPotion {
	String name;
	Material potionMaterial;
	BrTag brTag;
	PotionType potionType;
	ItemStack itemStack;

	private ArrayList<CustomEffect> immediateEffects = new ArrayList<CustomEffect>();
	private ArrayList<CustomEffect> lastingEffects = new ArrayList<CustomEffect>();
	
	
	public CustomPotion(String name, Material potionMaterial, PotionType potionType){
		this.name = name;
		this.potionMaterial = potionMaterial;
		this.brTag = new BrTag(name);
		this.potionType = potionType;
		Messenger.broadcast(brTag.toString());
		this.itemStack = createItemStack();
	}
	private ItemStack createItemStack(){
		ItemStack item = new ItemStack(potionMaterial, 1);
		PotionMeta pm = (PotionMeta) item.getItemMeta();
		pm.setDisplayName(Chat.translate(name) + BrItem.encodeItemData(brTag.fullTag()));
        pm.setBasePotionData(new PotionData(potionType));
		pm.addItemFlags(ItemFlag.HIDE_POTION_EFFECTS);
		item.setItemMeta(pm);
		
		return item;
	}

	public String getName() {
		return name;
	}
	public Material getPotionMaterial(){
		return potionMaterial;
	}
	public BrTag getBrTag(){
		return brTag;
	}
	public ItemStack getItemStack() {
		return itemStack;
	}
	public void execute(Location location) {
		for (Entity entity : Locations.getNearbyEntities(location, 10)){
			if(!(entity instanceof LivingEntity)) return;
			Messenger.broadcast("hit");
			LivingEntity lEntity = (LivingEntity) entity;
			lEntity.damage(lEntity.getHealth()); 
>>>>>>> origin/master
		}
	}
}
