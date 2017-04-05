package com.Patane.Brewery.Listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PotionSplashEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.projectiles.ProjectileSource;

import com.Patane.Brewery.Messenger;
import com.Patane.Brewery.util.BrItem;

public class GlobalListener implements Listener{
	//listenens for potion throw/on hit
	//scans NBT tag
	@EventHandler
	public void potionSplash (PotionSplashEvent e){
		ProjectileSource source = e.getPotion().getShooter();
		if(source instanceof Player){
			Player player = (Player) source;
			Messenger.send(player, "Potion Splash!");
			ItemStack potion = e.getPotion().getItem();
			PotionMeta pm = (PotionMeta) potion.getItemMeta();
			if(pm.getDisplayName() != null && BrItem.decodeItemData(pm.getDisplayName()).contains("Brewery")){
				Messenger.send(player, "Brewery Potion");
			}
		}
	}
}
