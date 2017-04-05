package com.Patane.Brewery.Listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PotionSplashEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;

import com.Patane.Brewery.util.BrItem;

public class GlobalListener implements Listener{
	//listenens for potion throw/on hit
	//scans NBT tag
	@EventHandler
	public void potionSplash (PotionSplashEvent e){
		ItemStack potion = e.getPotion().getItem();
		PotionMeta pm = (PotionMeta) potion.getItemMeta();
		if(pm.getDisplayName() != null && BrItem.decodeItemData(pm.getDisplayName()).contains("Br-")){
			e.setCancelled(true);
		}
	}
	@EventHandler
	public void onProjectileLaunch (ProjectileLaunchEvent e){
//		Projectile projectile = e.getEntity();
//		String projectileName = "";
//		if(projectile instanceof ThrownPotion){
//			ThrownPotion tp = (ThrownPotion) projectile;
//			projectileName = tp.getItem().getItemMeta().getDisplayName();
//		}
//		projectileName = BrItem.decodeItemData(projectileName);
//		if(projectileName.contains("Br-")){
//			e.setCancelled(true);
//		}
	}
}
