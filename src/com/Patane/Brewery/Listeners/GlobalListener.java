package com.Patane.Brewery.Listeners;

import java.util.List;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import com.Patane.Brewery.Brewery;
import com.Patane.Brewery.CustomItems.BrItem;
import com.Patane.Brewery.CustomItems.BrItem.CustomType;
import com.Patane.runnables.PatRunnable;
import com.Patane.util.general.GeneralUtil;
import com.Patane.util.ingame.ItemEncoder;
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
//		LivingEntity executor = (e.getEntity().getShooter() instanceof LivingEntity ? (LivingEntity) e.getEntity().getShooter() : null);
//		brItem.execute(executor, location);
//	}
//	@EventHandler
//	public void onLivingEntityDeath(EntityDamageByEntityEvent e){
//		if(e.getEntity() instanceof LivingEntity && ((LivingEntity) e.getEntity()).getLastDamage() >= ((LivingEntity) e.getEntity()).getHealth())
//			Messenger.debug(e.getDamager(), "&7You &ahave killed &7"+e.getEntity().getName());
//	}
	
	/**
	 * Called when a player Swings (Default left click) at a block.
	 * 
	 * If item in hand is a brItem, it will trigger as if it hit the ground at the swung block.
	 * 
	 * ItemTypes that can trigger from this: HITTABLE
	 * 
	 * @param e
	 */
	@EventHandler
	public void onItemSwingBlock(PlayerInteractEvent e){
		if(!(e.getHand().equals(EquipmentSlot.HAND) && (e.getAction() != null && e.getAction().equals(Action.LEFT_CLICK_BLOCK))))
			return;
		Player player = e.getPlayer();
		BrItem brItem = getBrItem(player.getInventory().getItemInMainHand());
		if(brItem == null || brItem.getType() != CustomType.HITTABLE)
			return;
		
		
		Location blockLocation = LocationsUtil.getCentre(e.getClickedBlock());
		Location location = new Location(blockLocation.getWorld(), blockLocation.getX() + e.getBlockFace().getModX(), blockLocation.getY() + e.getBlockFace().getModY(), blockLocation.getZ() + e.getBlockFace().getModZ());

		// Starts the cooldown (if any)
		brItem.getCD().start(player, player.getInventory().getHeldItemSlot());
		
		hitGround(brItem, location, player);
	}
	/**
	 * Called when an entity Damages another entity.
	 * 
	 * If item in hand is a brItem, it will trigger on the hit entity.
	 * 
	 * ItemTypes that can trigger from this: HITTABLE
	 * 
	 * @param e
	 */
	@EventHandler
	public void onItemSwingEntity(EntityDamageByEntityEvent e){
		if(!(e.getDamager() instanceof LivingEntity))
			return;
		// To avoid infinite damage loops, targets who are damaged by this plugin are tagged with "Brewery_DAMAGE".
		// If they have this tag, then we do not want to trigger this damage event as this would create an infinite damage loop.
		// This acts as "blocking" the registration of the BrItem's Damage and removing its DAMAGE metadata in the process.
		if(e.getEntity().hasMetadata("Brewery_DAMAGE")){
			e.getEntity().removeMetadata("Brewery_DAMAGE", Brewery.getInstance());
			return;
		}
		LivingEntity damager = (LivingEntity) e.getDamager();
		BrItem brItem = getBrItem(damager.getEquipment().getItemInMainHand());
		
		// Only 'HITTABLE' items will trigger in this way.
		if(brItem == null || brItem.getType() != CustomType.HITTABLE)
			return;
		
		// Starts the cooldown (if any)
		brItem.getCD().start(damager, null);
		
		if(!(e.getEntity() instanceof LivingEntity)) {
			Location location = e.getEntity().getLocation();
			hitGround(brItem, location, damager);
			return;
		}
		LivingEntity damaged = (LivingEntity) e.getEntity();
		hitEntity(brItem, damager, damaged);
	}
	/**
	 * Called when a Player right clicks another entity.
	 * 
	 * If item in hand is a brItem, it will trigger on the hit entity.
	 * 
	 * ItemTypes that can trigger from this: THROWABLE, CLICKABLE
	 * 
	 * @param e
	 */
	@EventHandler
	public void onItemRightClick(PlayerInteractEvent e){
		if(!(e.getHand().equals(EquipmentSlot.HAND) && (e.getAction() != null  && (e.getAction().equals(Action.RIGHT_CLICK_AIR) || e.getAction().equals(Action.RIGHT_CLICK_BLOCK)))))
			return;
		Player player = e.getPlayer();
		BrItem brItem = getBrItem(player.getInventory().getItemInMainHand());
		if(brItem == null)
			return;
		// Cancels if its something like SplashPotion
		e.setCancelled(true);
		
		// Starts the cooldown (if any)
		brItem.getCD().start(player, player.getInventory().getHeldItemSlot());
		
		if(brItem.getType() == CustomType.THROWABLE) {
			throwBrItem(player, brItem);
		}
		if(brItem.getType() == CustomType.CLICKABLE) {
			hitEntity(brItem, player, player);
		}
	}
	@EventHandler
	public void onItemSwitch(PlayerItemHeldEvent e) {
		Player player = e.getPlayer();
		BrItem brItem = getBrItem(player.getInventory().getItem(e.getNewSlot()));
		int slot = e.getNewSlot();
		if(brItem == null || brItem.getType() != CustomType.MAIN_HAND)
			return;
		Brewery.getInstance().getServer().getPluginManager().registerEvents(new PassiveItem(brItem, player, slot), Brewery.getInstance());
	}
	private class PassiveItem extends PatRunnable implements Listener{
		private final BrItem brItem;
		private final Player player ;
		private final int slot;
		
		public PassiveItem(BrItem brItem, Player player, int slot) {
			super(0, 20);
			this.brItem = brItem;
			this.player = player;
			this.slot = slot;
		}

		@Override
		public void run() {
			hitEntity(brItem, player, player);
		}
		@EventHandler
		public void onItemSwitch(PlayerItemHeldEvent e) {
			if(!e.getPlayer().equals(player))
				return;
			if(e.getPreviousSlot() == slot && e.getNewSlot() != slot)
				HandlerList.unregisterAll(this);
				this.cancel();
		}
	}
	/*================================================================================================
	 * 										Useful Methods
	 * 
	 *================================================================================================
	 */
	private void hitGround(BrItem item, Location location, LivingEntity executor) {
		item.execute(location, executor);
	}
	private void hitEntity(BrItem item, LivingEntity executor, LivingEntity target) {
		item.execute(executor, target);
	}
	/**
	 * Handles throwing an item based on a LivingEntity and a BrItem.
	 * @param entity LivingEntity that is throwing the brItem.
	 * @param brItem BrItem that is being thrown.
	 */
	private void throwBrItem(LivingEntity entity, BrItem brItem) {
		Location throwingLoc = new Location(entity.getWorld(), entity.getEyeLocation().getX(), entity.getEyeLocation().getY()-0.5, entity.getEyeLocation().getZ());
		Item item = entity.getWorld().dropItem(throwingLoc, brItem.getItem());
		item.setPickupDelay(Integer.MAX_VALUE);
		double pitch = ((entity.getLocation().getPitch() + 90) * Math.PI)/180;
		double yaw = ((entity.getLocation().getYaw() + 90) * Math.PI)/180;
		double x = Math.sin(pitch) * Math.cos(yaw);
		double y = Math.sin(pitch) * Math.sin(yaw);
		double z = Math.cos(pitch);
		item.setVelocity(new Vector(x,z,y).multiply(0.5));
		new ItemDetection(entity, brItem, item);
	}
	private class ItemDetection extends PatRunnable {
		private final Item item;
		private final BrItem brItem;
		private final LivingEntity executor;
		
		private boolean hasLeftExecutor = false;
		
		public ItemDetection(LivingEntity executor, BrItem brItem, Item item) {
			super(0, 1);
			this.item = item;
			this.brItem = brItem;
			this.executor = executor;
		}
		@Override
		public void run() {
			Location loc = item.getLocation();
			List<Entity> entities = item.getNearbyEntities(item.getWidth(), item.getHeight(), item.getWidth());
			// This allows us to ignore the executor whilst the object is being thrown away from them,
			// but lets them be targetted on the way back (eg. throwing it directly upwards)
			if(!entities.contains(executor))
				hasLeftExecutor = true;
			if(!hasLeftExecutor)
				entities.remove(executor);
			if(!entities.isEmpty()) {
				List<LivingEntity> living = GeneralUtil.getLiving(entities);
				if(living.isEmpty()) {
					item.setPickupDelay(0);
					item.remove();
					hitGround(brItem, item.getLocation(), executor);
					this.cancel();
					return;
				}
				item.setPickupDelay(0);
				item.remove();
				hitEntity(brItem, executor, LocationsUtil.getClosest(living, item.getLocation()));
				this.cancel();
				return;
			}
			if(loc.add(0, -0.5, 0).getBlock().getType() != Material.AIR || loc.add(item.getVelocity().multiply(2)).getBlock().getType() != Material.AIR){
				item.setPickupDelay(0);
				item.remove();
				hitGround(brItem, item.getLocation(), executor);
				this.cancel();
				return;
			}
		}
	}

	private static BrItem getBrItem(ItemStack item){
		String brItemName = ItemEncoder.extractTag(item);
		if(brItemName == null)
			return null;
		return Brewery.getItemCollection().getItem(brItemName);
	}
}
