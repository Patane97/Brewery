package com.Patane.Brewery.Listeners;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PotionSplashEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;

import com.Patane.Brewery.CustomPotions.CustomPotion;
import com.Patane.Brewery.CustomPotions.classes.BrTag;
import com.Patane.Brewery.collections.CustomPotions;
import com.Patane.Brewery.util.BrItem;

public class GlobalListener implements Listener{
	//listenens for potion throw/on hit
	//scans NBT tag
	static ArrayList<Location> storedParticleLocations = new ArrayList<Location>();
	
	public static ArrayList<Location> getParticleLoc(){
		return storedParticleLocations;
	}
	@EventHandler
	public void potionSplash (PotionSplashEvent e){
		ItemStack itemStack = e.getPotion().getItem();
		PotionMeta pm = (PotionMeta) itemStack.getItemMeta();

		// If it is not a BR item, return.
		String brTagStr;
		if(pm.getDisplayName() == null) return;

		String str = BrItem.decodeItemData(pm.getDisplayName());
		Matcher matcher = Pattern.compile("BR\\{(.*?)\\}").matcher(str);
		brTagStr = (matcher.find() ? matcher.group(1) : null);
		if(brTagStr == null) return;
		e.setCancelled(true);
		CustomPotion brPotion = CustomPotions.grab(BrTag.toItemName(brTagStr));
		if(brPotion == null) return;
		
		
		Location splashLocation = e.getEntity().getLocation();
		
		brPotion.execute(splashLocation);
		
		Location storedLoc = new Location (splashLocation.getWorld(), splashLocation.getBlockX(), splashLocation.getBlockY(), splashLocation.getBlockZ());
		storedParticleLocations.add(storedLoc);
		
		splashLocation.getWorld().spawnParticle(Particle.SPELL_MOB, splashLocation, 200, 0.4D, 0.5D, 0.4D);
//		ColouredParticle.RED_DUST.send(loc, 16, 23, 23, 23);
		splashLocation.getWorld().playSound(splashLocation, Sound.ENTITY_SPLASH_POTION_BREAK, 1, 1);
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
//		le.launchProjectile(projectile.getClass());
//		Entity drop = (Entity) le.getWorld().dropItem(le.getEyeLocation(), item);
//		drop.setVelocity(le.getLocation().getDirection().multiply(0.65D));
	}
}
