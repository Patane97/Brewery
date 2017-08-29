package com.Patane.Brewery.Listeners;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PotionSplashEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.Patane.Brewery.Brewery;
import com.Patane.Brewery.Messenger;
import com.Patane.Brewery.CustomItems.BrItem;
import com.Patane.Brewery.CustomItems.BrItem.CustomType;
import com.Patane.Brewery.util.ItemUtilities;
import com.Patane.Brewery.util.LocationUtilities;

public class GlobalListener implements Listener{
	
	/**
	 * Detects when a potion has been hit
	 * Checks if it is from a BrItem
	 * Checks if BrItem is of type "THROWABLE"
	 * Executes BrItem
	 * @param e
	 */
	@EventHandler
	public void potionSplash (PotionSplashEvent e){
		// If it is not a BR item, return.
		BrItem brPotion = getBrItem(e.getPotion().getItem());
		if(brPotion == null)
			return;
		// If it is not of type THROWABLE, return.
		if(brPotion.getType() != CustomType.THROWABLE)
			return;
		e.setCancelled(true);
		Location location = e.getEntity().getLocation();
		LivingEntity shooter = (e.getEntity().getShooter() instanceof LivingEntity ? (LivingEntity) e.getEntity().getShooter() : null);
		brPotion.execute(shooter, location);
	}
	@EventHandler
	public void onLivingEntityDeath(EntityDamageByEntityEvent e){
		if(e.getEntity() instanceof LivingEntity && ((LivingEntity) e.getEntity()).getLastDamage() >= ((LivingEntity) e.getEntity()).getHealth())
			Messenger.debug(e.getDamager(), "&7You &ahave killed &7"+e.getEntity().getName());
	}
	/**
	 * 
	 * @param e
	 */
	@EventHandler
	public void onItemSwingBlock(PlayerInteractEvent e){
		if(!(e.getHand().equals(EquipmentSlot.HAND) && e.getAction().equals(Action.LEFT_CLICK_BLOCK)))
			return;
		Player player = e.getPlayer();
		BrItem brItem = getBrItem(player.getInventory().getItemInMainHand());
		if(brItem == null)
			return;
		if(brItem.getType() != CustomType.HITTABLE)
			return;
		Location blockLocation = LocationUtilities.getCentre(e.getClickedBlock());
		Location location = new Location(blockLocation.getWorld(), blockLocation.getX() + e.getBlockFace().getModX(), blockLocation.getY() + e.getBlockFace().getModY(), blockLocation.getZ() + e.getBlockFace().getModZ());
		brItem.execute(player, location);
	}
	@EventHandler
	public void onItemSwingEntity(EntityDamageByEntityEvent e){
		if(!(e.getDamager() instanceof LivingEntity))
			return;
		if(e.getEntity().hasMetadata("Brewery_DAMAGE")){
			e.getEntity().removeMetadata("Brewery_DAMAGE", Brewery.getInstance());
			return;
		}
		LivingEntity damager = (LivingEntity) e.getDamager();
		BrItem brItem = getBrItem(damager.getEquipment().getItemInMainHand());
		if(brItem == null)
			return;
		if(brItem.getType() != CustomType.HITTABLE)
			return;
		Location location = (e.getEntity() instanceof LivingEntity ? ((LivingEntity) e.getEntity()).getEyeLocation() : e.getEntity().getLocation());
		brItem.execute(damager, location);
	}
	
	private BrItem getBrItem(ItemStack item){
		if(!item.hasItemMeta())
			return null;
		ItemMeta itemMeta = item.getItemMeta();
		if(itemMeta.getDisplayName() == null)
			return null;
		Matcher match = Pattern.compile(".* <Br\\-(\\w+)>").matcher(ItemUtilities.decodeItemData(itemMeta.getDisplayName()));
		if(!match.matches())
			return null;
		String decodedName = match.group(1);
		return Brewery.getItemCollection().getItem(decodedName);
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
