package com.Patane.Brewery.Listeners;

import java.util.ArrayList;

import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PotionSplashEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;

import com.Patane.Brewery.util.BrItem;
import com.Patane.Brewery.util.ColouredParticle;
import com.Patane.Brewery.util.Locations;

public class GlobalListener implements Listener{
	//listenens for potion throw/on hit
	//scans NBT tag
	static ArrayList<Location> storedParticleLocations = new ArrayList<Location>();
	
	public static ArrayList<Location> getParticleLoc(){
		return storedParticleLocations;
	}
	@EventHandler
	public void potionSplash (PotionSplashEvent e){
		ItemStack potion = e.getPotion().getItem();
		PotionMeta pm = (PotionMeta) potion.getItemMeta();
		// If it is not a BR item, return.
		if(!(pm.getDisplayName() != null && BrItem.decodeItemData(pm.getDisplayName()).contains("Br-"))) return;
		e.setCancelled(true);
		Location loc = e.getEntity().getLocation();
		Location storedLoc = new Location (loc.getWorld(), loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());
		storedParticleLocations.add(storedLoc);
		
//		loc.getWorld().spawnParticle(Particle.SPELL_MOB, loc, 200, 0.4D, 0.5D, 0.4D, Color.RED);
		ColouredParticle.RED_DUST.send(loc, 16, 23, 23, 23);
		loc.getWorld().playSound(loc, Sound.ENTITY_SPLASH_POTION_BREAK, 1, 1);
		
		for (Entity entity : Locations.getNearbyEntities(loc, 10)){
			if(!(entity instanceof LivingEntity)) return;
			LivingEntity lEntity = (LivingEntity) entity;
			lEntity.damage(lEntity.getHealth());
		}

	}
	@EventHandler
	public void onProjectileLaunch (ProjectileLaunchEvent e){
//		Projectile projectile = e.getEntity();
//		String projectileName = "";
//		ItemStack item = null;
//		if(projectile instanceof ThrownPotion){
//			ThrownPotion tp = (ThrownPotion) projectile;
//			item = tp.getItem();
//			projectileName = item.getItemMeta().getDisplayName();
//		}
//		projectileName = BrItem.decodeItemData(projectileName);
//		if(!projectileName.contains("Br-")) return;
//		e.setCancelled(true);
//		ProjectileSource ps = projectile.getShooter();
//		if(!(ps instanceof LivingEntity)) return;
//		LivingEntity le = (LivingEntity) ps;
////		le.launchProjectile(projectile.getClass());
//		Entity drop = (Entity) le.getWorld().dropItem(le.getEyeLocation(), item);
//		drop.setVelocity(le.getLocation().getDirection().multiply(0.65D));
	}
}
