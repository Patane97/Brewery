package com.Patane.Brewery.Listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PotionSplashEvent;
import org.bukkit.projectiles.ProjectileSource;

import com.Patane.Brewery.Messenger;

public class GlobalListener implements Listener{
	//listenens for potion throw/on hit
	//scans NBT tag
	@EventHandler
	public void potionSplash (PotionSplashEvent e){
		ProjectileSource source = e.getPotion().getShooter();
		Messenger.broadcast("Somebody threw a potion!");
		if(source instanceof Player){
			Player player = (Player) source;
			Messenger.send(player, "It was you!");
		}
	}
}
