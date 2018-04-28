package com.Patane.Brewery.Listeners;

import java.util.Collection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.util.Vector;

import com.Patane.Brewery.Brewery;
import com.Patane.Brewery.CustomItems.BrItem;
import com.Patane.Brewery.CustomItems.BrItem.CustomType;
import com.Patane.util.general.PatRunnable;
import com.Patane.util.ingame.ItemsUtil;
import com.Patane.util.ingame.LocationsUtil;

public class GlobalListener implements Listener{
	
	/**
	 * Detects when a potion has been hit
	 * Checks if it is from a BrItem
	 * Checks if BrItem is of type "THROWABLE"
	 * Executes BrItem
	 * @param e
	 */
//	@EventHandler
//	public void potionSplash (PotionSplashEvent e){
//		// If it is not a BR item, return.
//		BrItem brItem = getBrItem(e.getPotion().getItem());
//		if(brItem == null || brItem.getType() != CustomType.THROWABLE)
//			return;
//		e.setCancelled(true);
////		Location location = e.getEntity().getLocation();
//		Location location = e.getHitEntity().getLocation();
//		LivingEntity shooter = (e.getEntity().getShooter() instanceof LivingEntity ? (LivingEntity) e.getEntity().getShooter() : null);
//		brItem.execute(shooter, location);
//	}
//	@EventHandler
//	public void onLivingEntityDeath(EntityDamageByEntityEvent e){
//		if(e.getEntity() instanceof LivingEntity && ((LivingEntity) e.getEntity()).getLastDamage() >= ((LivingEntity) e.getEntity()).getHealth())
//			Messenger.debug(e.getDamager(), "&7You &ahave killed &7"+e.getEntity().getName());
//	}
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
		if(brItem == null || brItem.getType() != CustomType.HITTABLE)
			return;
		Location blockLocation = LocationsUtil.getCentre(e.getClickedBlock());
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
		if(brItem == null || brItem.getType() != CustomType.HITTABLE)
			return;
		Location location = (e.getEntity() instanceof LivingEntity ? ((LivingEntity) e.getEntity()).getEyeLocation() : e.getEntity().getLocation());
		brItem.execute(damager, location);
	}

	/**
	 * 
	 * @param e
	 */
	@EventHandler
	public void onItemRightClick(PlayerInteractEvent e){
		if(!(e.getHand().equals(EquipmentSlot.HAND) && (e.getAction().equals(Action.RIGHT_CLICK_AIR) || e.getAction().equals(Action.RIGHT_CLICK_BLOCK))))
			return;
		Player player = e.getPlayer();
		BrItem brItem = getBrItem(player.getInventory().getItemInMainHand());
		if(brItem == null || brItem.getType() != CustomType.THROWABLE)
			return;
		// Cancels if its something like SplashPotion
		e.setCancelled(true);
		Location throwingLoc = new Location(player.getWorld(), player.getEyeLocation().getX(), player.getEyeLocation().getY()-0.5, player.getEyeLocation().getZ());
		Item item = player.getWorld().dropItem(throwingLoc, brItem.getItem());
		item.setPickupDelay(Integer.MAX_VALUE);
		double pitch = ((player.getLocation().getPitch() + 90) * Math.PI)/180;
		double yaw = ((player.getLocation().getYaw() + 90) * Math.PI)/180;
		double x = Math.sin(pitch) * Math.cos(yaw);
		double y = Math.sin(pitch) * Math.sin(yaw);
		double z = Math.cos(pitch);
		item.setVelocity(new Vector(x,z,y).multiply(0.5));
		new ItemDetection(player, brItem, item);
	}
	private class ItemDetection extends PatRunnable {
		private final Item item;
		private final BrItem brItem;
		private final LivingEntity shooter;
		
		public ItemDetection(LivingEntity shooter, BrItem brItem, Item item) {
			super(Brewery.getInstance(), 2, 1);
			this.item = item;
			this.brItem = brItem;
			this.shooter = shooter;
		}

		@Override
		public void run() {
			Location loc = item.getLocation();
			Collection<Entity> entities = item.getNearbyEntities(item.getWidth(), item.getHeight(), item.getWidth());
			entities.remove(shooter);
			if(loc.add(0, -0.5, 0).getBlock().getType() != Material.AIR || loc.add(item.getVelocity().multiply(2)).getBlock().getType() != Material.AIR || !entities.isEmpty()){
				item.setPickupDelay(0);
				item.remove();
				brItem.execute(shooter, item.getLocation());
				this.cancel();
				return;
			}
		}
		
	}
	private static BrItem getBrItem(ItemStack item){
		if(!item.hasItemMeta())
			return null;
		ItemMeta itemMeta = item.getItemMeta();
		if(itemMeta.getDisplayName() == null)
			return null;
		Matcher match = Pattern.compile(".* <Br\\-(\\w+)>").matcher(ItemsUtil.decodeItemData(itemMeta.getDisplayName()));
		if(!match.matches())
			return null;
		String decodedName = match.group(1);
		return Brewery.getItemCollection().getItem(decodedName);
	}
}
