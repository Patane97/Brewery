package com.Patane.Brewery.Listeners;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PotionSplashEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;

import com.Patane.Brewery.Brewery;
import com.Patane.Brewery.Messenger;
import com.Patane.Brewery.CustomPotions.CustomPotion;
import com.Patane.Brewery.util.BrItem;

public class GlobalListener implements Listener{
	//listenens for potion throw/on hit
	//scans NBT tag
	Pattern pattern = Pattern.compile(".* <Br\\-(\\w+)>");
	
	@EventHandler
	public void potionSplash (PotionSplashEvent e){
		ItemStack potion = e.getPotion().getItem();
		PotionMeta pm = (PotionMeta) potion.getItemMeta();
		// If it is not a BR item, return.
		if(pm.getDisplayName() == null)
			return;
		Matcher match = pattern.matcher(BrItem.decodeItemData(pm.getDisplayName()));
		if(!match.matches())
			return;
		String decodedName = match.group(1);
		if(!Brewery.getCustomPotions().contains(decodedName))
			return;
		CustomPotion customPotion = Brewery.getCustomPotions().getItem(decodedName);
		e.setCancelled(true);
		Location location = e.getEntity().getLocation();
		LivingEntity livingShooter = null;
		if(e.getPotion().getShooter() instanceof LivingEntity)
			livingShooter = (LivingEntity) e.getPotion().getShooter();
		
		customPotion.execute(livingShooter, location);
		if(e.getPotion().getShooter() instanceof Player)
			Messenger.debug((Player) e.getEntity().getShooter(), "&7"+decodedName+" &apotion detected");
	}
//	@EventHandler
//	public void onDamageTaken (EntityDamageEvent e){
//		if(e instanceof EntityDamageByEntityEvent)
//			Messenger.debug(ChatType.BROADCAST, "Damager: YOU, Cause: "+e.getCause());
//		else
//			Messenger.debug(ChatType.BROADCAST, "Damager: UNKNOWN, Cause: "+e.getCause());
//	}
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
