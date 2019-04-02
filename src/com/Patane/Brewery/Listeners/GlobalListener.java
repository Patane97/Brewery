package com.Patane.Brewery.Listeners;

import java.util.List;

import org.apache.commons.lang3.ArrayUtils;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import com.Patane.Brewery.Brewery;
import com.Patane.Brewery.Cooldowns.CooldownHandler;
import com.Patane.Brewery.CustomItems.BrItem;
import com.Patane.Brewery.CustomItems.BrItem.CustomType;
import com.Patane.listeners.BaseListener;
import com.Patane.runnables.PatRunnable;
import com.Patane.util.general.GeneralUtil;
import com.Patane.util.general.Messenger;
import com.Patane.util.general.Messenger.Msg;
import com.Patane.util.ingame.ItemEncoder;
import com.Patane.util.ingame.LocationsUtil;
import com.Patane.util.main.PataneUtil;

public class GlobalListener extends BaseListener{
	
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
		ItemStack item = player.getInventory().getItemInMainHand();
		BrItem brItem = BrItem.get(item);
		if(brItem == null || brItem.getType() != CustomType.HITTABLE)
			return;
		
		
		Location blockLocation = LocationsUtil.getCentre(e.getClickedBlock());
		Location location = new Location(blockLocation.getWorld(), blockLocation.getX() + e.getBlockFace().getModX(), blockLocation.getY() + e.getBlockFace().getModY(), blockLocation.getZ() + e.getBlockFace().getModZ());

		// Starts the cooldown (if any)
		if(brItem.hasCooldown() && !CooldownHandler.start(player, item, brItem))
			return;
		
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
		ItemStack item = damager.getEquipment().getItemInMainHand();
		BrItem brItem = BrItem.get(item);
		
		// Only 'HITTABLE' items will trigger in this way.
		if(brItem == null || brItem.getType() != CustomType.HITTABLE)
			return;
		
		// Starts the cooldown (if any)
		if(brItem.hasCooldown() && !CooldownHandler.start(damager, item, brItem))
			return;
		
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
		ItemStack item = player.getInventory().getItemInMainHand();
		BrItem brItem = BrItem.get(item);
		if(brItem == null)
			return;
		// Cancels if its something like SplashPotion
		e.setCancelled(true);
		
		// Starts the cooldown (if any)
		if(brItem.hasCooldown() && !CooldownHandler.start(player, item, brItem))
			return;
		
		if(brItem.getType() == CustomType.THROWABLE) {
			throwBrItem(player, brItem);
		}
		if(brItem.getType() == CustomType.CLICKABLE) {
			hitEntity(brItem, player, player);
		}
	}

///////////////////////////////////////////////////////////////////////////////////////////////////////////////
/////////////////////////////////////////// UUID ITEM CHECKS //////////////////////////////////////////////////
///////////////////////////////////////////////////////////////////////////////////////////////////////////////
	// ***************** IMPORTANT *********************
	// When 'MaxStacks' is implemented, add a checker to ensure the dropped ItemStack adheres to MaxStacks. (
	// ***************** IMPORTANT *********************
	@EventHandler
	public void onCreativeItemDrop(PlayerDropItemEvent e){
		if(e.getPlayer().getGameMode() != GameMode.CREATIVE)
			return;
		ItemStack item = e.getItemDrop().getItemStack();
		BrItem brItem = BrItem.get(item);
		if(brItem == null)
			return;
		if(!ItemEncoder.hasTag(item, "UUID")) {
			Messenger.warning("Item '"+item.getItemMeta().getDisplayName()+"' in "+e.getPlayer().getDisplayName()+"'s inventory is missing UUID. Cooldowns will not work with this item.");
			return;
		}
		ItemStack[] invItems = grabInvItems(e.getPlayer().getOpenInventory());
		for(ItemStack invItem : invItems) {
			if(ItemEncoder.hasTag(invItem, "UUID") && ItemEncoder.getString(invItem, "UUID").equals(ItemEncoder.getString(item, "UUID"))) {
				if(e.isCancelled())
					return;
				Messenger.debug(Msg.INFO, "UUID Before: "+ItemEncoder.getString(item, "UUID"));
				e.getItemDrop().setItemStack(brItem.generateItem());
				Messenger.debug(Msg.INFO, "UUID After: "+ItemEncoder.getString(e.getItemDrop().getItemStack(), "UUID"));
				return;
			}
		}
	}
	@EventHandler
	public void onDragItem(InventoryDragEvent e) {
		if(!(e.getWhoClicked() instanceof Player))
			return;
		ItemStack item = e.getOldCursor();
		BrItem brItem = BrItem.get(item);
		if(brItem == null)
			return;
		if(!ItemEncoder.hasTag(item, "UUID")) {
			Messenger.warning("Item '"+item.getItemMeta().getDisplayName()+"' in "+((Player)e.getWhoClicked()).getDisplayName()+"'s inventory is missing UUID. Cooldowns will not work with this item.");
			return;
		}
		e.setCancelled(true);
		Messenger.debug(Msg.INFO, "Size="+e.getRawSlots().size());
		ItemStack[] invItems = grabInvItems(e.getView());
		
		for(ItemStack invItem : invItems) {
			if(ItemEncoder.hasTag(invItem, "UUID") && ItemEncoder.getString(invItem, "UUID").equals(ItemEncoder.getString(item, "UUID"))) {
				PataneUtil.getInstance().getServer().getScheduler().scheduleSyncDelayedTask(Brewery.getInstance(), new Runnable() {
					@Override
					public void run() {
						if(e.isCancelled())
							return;
						for(int slot : e.getRawSlots()) {
							Messenger.debug(Msg.INFO, "UUID Before: "+ItemEncoder.getString(item, "UUID"));
							e.getView().setItem(slot, brItem.generateItem());
							Messenger.debug(Msg.INFO, "UUID After: "+ItemEncoder.getString(e.getView().getItem(slot), "UUID"));
						}
					}
				});
				return;
			}
		}
	}
	/**
	 * Detects when a player places an ItemStack from the cursor to their inventory.
	 * If cursor ItemStack is a BrItem, it checks the inventory for a BrItem with the same UUID.
	 * If this returns true, then it uses {@link BrItem.generateItem()} to give the item a new UUID.
	 * 
	 * This is to avoid creative users duplicating the item (middle click) and having the same UUID as the original item.
	 * @param e
	 */
	@EventHandler
	public void onCreativePlaceItem(InventoryClickEvent e) {
		if(!(e.getWhoClicked() instanceof Player) || e.getClickedInventory() == null || ((Player) e.getWhoClicked()).getGameMode() != GameMode.CREATIVE)
			return;
		ItemStack item = e.getCursor();
		BrItem brItem = BrItem.get(item);
		if(brItem == null)
			return;
		if(!ItemEncoder.hasTag(item, "UUID")) {
			Messenger.warning("Item '"+item.getItemMeta().getDisplayName()+"' in "+((Player)e.getWhoClicked()).getDisplayName()+"'s inventory is missing UUID. Cooldowns will not work with this item.");
			return;
		}
		ItemStack[] invItems = grabInvItems(e.getView());
		
		for(ItemStack invItem : invItems) {
			if(ItemEncoder.hasTag(invItem, "UUID") && ItemEncoder.getString(invItem, "UUID").equals(ItemEncoder.getString(item, "UUID"))) {
				PataneUtil.getInstance().getServer().getScheduler().scheduleSyncDelayedTask(Brewery.getInstance(), new Runnable() {
					@Override
					public void run() {
						if(e.isCancelled())
							return;
						Messenger.debug(Msg.INFO, "UUID Before: "+ItemEncoder.getString(item, "UUID"));
						e.setCurrentItem(brItem.generateItem());
						Messenger.debug(Msg.INFO, "UUID After: "+ItemEncoder.getString(e.getCurrentItem(), "UUID"));
					}
				});
				return;
			}
		}
	}

	private static ItemStack[] grabInvItems(InventoryView invView) {
		if(invView.getTopInventory() != invView.getBottomInventory())
			return ArrayUtils.addAll(invView.getTopInventory().getContents(), invView.getBottomInventory().getContents());
		return invView.getBottomInventory().getContents();
	}
///////////////////////////////////////////////////////////////////////////////////////////////////////////////
/////////////////////////////////////////// COOLDOWN CHECKERS /////////////////////////////////////////////////
///////////////////////////////////////////////////////////////////////////////////////////////////////////////
	/*
	 * TODO: MaxStacks checks for each cooldown checker (when maxstacks is implemented)
	 */
	
	// 4/8/18: Testing if this can be removed/replaced with method below it.
//	@EventHandler
//	public void onItemSwitch(PlayerItemHeldEvent e) {
//		if(CooldownHandler.noCooldowns())
//			return;
//		Player player = e.getPlayer();
//		ItemStack item = player.getInventory().getItem(e.getNewSlot());
//		if(CooldownHandler.updateFromYAML(player, item, BrItem.get(item)))
//			return;
//		if(item == null || item.getType() == Material.AIR) {
//			if(BrMetaDataHandler.hasValue(player, "showing_cooldown") && CooldownHandler.cooldowns().containsKey(BrMetaDataHandler.getValue(player, "showing_cooldown")))
//				CooldownHandler.cooldowns().get(BrMetaDataHandler.getValue(player, "showing_cooldown")).removePlayer(player);
//			return;
//		}
//		String uuidString = ItemEncoder.getString(item, "UUID");
//		if(uuidString != null) {
//			if(BrMetaDataHandler.hasValue(player, "showing_cooldown"))
//				CooldownHandler.cooldowns().get(BrMetaDataHandler.getValue(player, "showing_cooldown")).removePlayer(player);
//			for(UUID uuid : CooldownHandler.cooldowns().keySet()) {
//				if(uuidString.equals(uuid.toString())) {
//					CooldownHandler.cooldowns().get(uuid).addPlayer(player);
//					return;
//				}
//			}
//		}
//	}
	@EventHandler
	public void onItemSwitch(PlayerItemHeldEvent e) {
		CooldownHandler.checkUpdateCooldowns(e.getPlayer());
	}
	@EventHandler
	public void onJoin(PlayerJoinEvent e) {
		CooldownHandler.checkUpdateCooldowns(e.getPlayer());
	}
	@EventHandler
	public void onItemPickup(EntityPickupItemEvent e) {
		if(!(e.getEntity() instanceof Player))
			return;
		CooldownHandler.checkUpdateCooldowns((Player) e.getEntity());
	}
	@EventHandler
	public void onItemMove(InventoryClickEvent e) {
		if(!(e.getWhoClicked() instanceof Player))
			return;
		CooldownHandler.checkUpdateCooldowns((Player) e.getWhoClicked());
	}
	@EventHandler
	public void onItemOffhandSwitch(PlayerSwapHandItemsEvent e) {
		CooldownHandler.checkUpdateCooldowns(e.getPlayer());
	}
	
	
///////////////////////////////////////////////////////////////////////////////////////////////////////////////
//////////////////////////////////////// Passive ItemType Tests ///////////////////////////////////////////////
///////////////////////////////////////////////////////////////////////////////////////////////////////////////
//	@EventHandler
//	public void onItemSwitch(PlayerItemHeldEvent e) {
//		Player player = e.getPlayer();
//		BrItem brItem = BrItem.get(player.getInventory().getItem(e.getNewSlot()));
//		int slot = e.getNewSlot();
//		if(brItem == null || brItem.getType() != CustomType.MAIN_HAND)
//			return;
//		Brewery.getInstance().getServer().getPluginManager().registerEvents(new PassiveItem(brItem, player, slot), Brewery.getInstance());
//	}
//	private class PassiveItem extends PatRunnable implements Listener{
//		private final BrItem brItem;
//		private final Player player ;
//		private final int slot;
//		
//		public PassiveItem(BrItem brItem, Player player, int slot) {
//			super(0, 20);
//			this.brItem = brItem;
//			this.player = player;
//			this.slot = slot;
//		}
//
//		@Override
//		public void run() {
//			hitEntity(brItem, player, player);
//		}
//		@EventHandler
//		public void onItemSwitch(PlayerItemHeldEvent e) {
//			if(!e.getPlayer().equals(player))
//				return;
//			if(e.getPreviousSlot() == slot && e.getNewSlot() != slot)
//				HandlerList.unregisterAll(this);
//				this.cancel();
//		}
//	}
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
	/*
	 * TODO: Refine throwing mechanic to be more accurate to potions. Maybe add options for throwing intensity, direction, etc.
	 */
	private void throwBrItem(LivingEntity entity, BrItem brItem) {
		Location throwingLoc = new Location(entity.getWorld(), entity.getEyeLocation().getX(), entity.getEyeLocation().getY()-0.5, entity.getEyeLocation().getZ());
		Item item = entity.getWorld().dropItem(throwingLoc, brItem.getItemStack());
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
}
