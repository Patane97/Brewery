package com.Patane.Brewery.Listeners;

import java.util.List;

import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.libs.org.apache.commons.lang3.ArrayUtils;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerPickupArrowEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.Inventory;
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
import com.Patane.util.ingame.InventoriesUtil;
import com.Patane.util.ingame.ItemEncoder;
import com.Patane.util.location.LocationsUtil;
import com.Patane.util.location.RadiusUtil;
import com.Patane.util.main.PataneUtil;
import com.Patane.util.metadata.MetaDataUtil;

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
	public void onItemSwingBlock(PlayerInteractEvent e) {
		
		// If its not left clicking a block
		if(e.getAction() != Action.LEFT_CLICK_BLOCK
		// If its not in the main hand
		|| e.getHand() != EquipmentSlot.HAND)
			return;
		
		Player player = e.getPlayer();
		ItemStack item = player.getInventory().getItemInMainHand();
		BrItem brItem = BrItem.getFromItemStack(item);
		
		if(brItem == null
		|| brItem.getType() != CustomType.HITTABLE)
			return;
	
		Location blockLocation = LocationsUtil.getCentre(e.getClickedBlock());
//		Location location = blockLocation.add(new Vector(e.getBlockFace().getModX(), e.getBlockFace().getModY(), e.getBlockFace().getModZ()));
		
		if(PataneUtil.getDebug())
			blockLocation.getWorld().spawnParticle(Particle.FLAME, blockLocation, 0, 0,0,0);
		
		// Starts the cooldown (if any)
		if(brItem.hasCooldown() && !CooldownHandler.start(player, item, brItem))
			return;
		
		hitGround(brItem, blockLocation, player);
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
	public void onItemSwingEntity(EntityDamageByEntityEvent e) {
		if(!(e.getDamager() instanceof LivingEntity))
			return;
		// To avoid infinite damage loops, targets who are damaged by this plugin are tagged with "effect_damage".
		// If they have this tag, then we do not want to trigger this damage event as this would create an infinite damage loop.
		// This acts as "blocking" the registration of the BrItem's Damage and removing its DAMAGE metadata in the process.
		if(MetaDataUtil.has(e.getEntity(), "effect_damage")) {
			MetaDataUtil.remove(e.getEntity(), "effect_damage");
			return;
		}
		LivingEntity damager = (LivingEntity) e.getDamager();
		ItemStack item = damager.getEquipment().getItemInMainHand();
		BrItem brItem = BrItem.getFromItemStack(item);
		
		// Only 'HITTABLE' items will trigger in this way.
		if(brItem == null
		|| brItem.getType() != CustomType.HITTABLE)
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
	public void onItemRightClick(PlayerInteractEvent e) {
		// If its not right clicking air
		if((e.getAction() != Action.RIGHT_CLICK_AIR
		// If its not right clicking block
		&& e.getAction() != Action.RIGHT_CLICK_BLOCK)
		// If its not in HAND equipmentslot
		// TODO: Add option to choose which equipmentslot an item is active in
		|| e.getHand() != EquipmentSlot.HAND)
			return;
		
		Player player = e.getPlayer();
		ItemStack item = player.getInventory().getItemInMainHand();
		BrItem brItem = BrItem.getFromItemStack(item);

		if(brItem == null 
		|| !(brItem.getType() == CustomType.THROWABLE
		|| brItem.getType() == CustomType.CLICKABLE
		|| brItem.getType() == CustomType.CONSUMABLE))
			return;
		
		// Cancels if its something like SplashPotion
		e.setCancelled(true);
		
		// Starts the cooldown (if any)
		if(brItem.hasCooldown() && !CooldownHandler.start(player, item, brItem))
			return;
		
		switch(brItem.getType()) {
			case THROWABLE:
				throwBrItem(player, brItem);
				break;
			case CLICKABLE:
				hitEntity(brItem, player, player);
				break;
			case CONSUMABLE:
				item.setAmount(item.getAmount()-1);
				hitEntity(brItem, player, player);
				break;
			default:
				break;
		}
	}
	@EventHandler
	public void onProjectileLaunch(ProjectileLaunchEvent e) {
		Projectile projectile = e.getEntity();
		
		// If the shooter isnt a livingentity, they cannot be holding a brItem (at least not yet). Therefore, ignore them.
		if(!(projectile.getShooter() instanceof LivingEntity))
			return;
		
		// Turn ProjectileSource into LivingEntity.
		LivingEntity livingShooter = (LivingEntity) projectile.getShooter();
		
		ItemStack item = null;
		
		// If the projectile is an arrow, we must check the offHand OR the first available arrow itemstack
		if(InventoriesUtil.isArrowEntityType(projectile.getType())) {
			item = InventoriesUtil.getTargettedArrowStack(livingShooter);
		}
		// If its not an arrow, then it should be whatever is in the main hand of the entity!
		else {
			item = livingShooter.getEquipment().getItemInMainHand();
		}
		if(item == null)
			return;
		
		BrItem brItem = BrItem.getFromItemStack(item);
		if(brItem == null
		|| brItem.getType() != CustomType.PROJECTILE)
			return;
		
		// Starts the cooldown (if any)
		// If it is currently on cooldown, cancel the projectile being launched and do nothing.
		if(brItem.hasCooldown() && !CooldownHandler.start(livingShooter, item, brItem)) {
			e.setCancelled(true);
			return;
		}
		
//		// Save the br item name to be grabbed on Hit!
		MetaDataUtil.setFixed(projectile, "item_projectile", brItem.getName());
//		
		// Projectiles dont need UUIDs anymore, as they have a common_cooldown, not a unique_cooldown
//		// Save the UUID of item onto projectile to be grabbed on Hit!
//		MetaDataUtil.setFixed(projectile, "UUID", ItemEncoder.getString(item, "UUID"));
	}
	
	@EventHandler
	public void onProjectileHit(ProjectileHitEvent e) { 
		if(!MetaDataUtil.has(e.getEntity(), "item_projectile"))
			return;
		// Get the projectile
		Projectile projectile = e.getEntity();
		
		// Grab the name of the brItem through metadata
		String brItemName = MetaDataUtil.get(projectile, "item_projectile").asString();
		
		// Find the brItem
		BrItem brItem = Brewery.getItemCollection().getItem(brItemName);
		
		// If item cannot be found or its not a projectile type, do nothing
		if(brItem == null || brItem.getType() != CustomType.PROJECTILE)
			return;
		
		// If the shooter isnt a living entity
		// TODO: Make this work with Dispensers!!!
		if(!(projectile.getShooter() instanceof LivingEntity))
			return;
		
		LivingEntity shooter = (LivingEntity) projectile.getShooter();
		
		// Run the relevant effect code with the gathered information
		// If it his an entity
		if(e.getHitEntity() != null) {
			Entity target = e.getHitEntity();
			if(target instanceof LivingEntity)
				hitEntity(brItem, shooter, (LivingEntity) target);
			else
				hitGround(brItem, target.getLocation(), shooter);
		}
		// If it hit a block
		else if(e.getHitBlock() != null)
			hitGround(brItem, projectile.getLocation(), shooter);
	}
	
	@SuppressWarnings("deprecation")
	@EventHandler
	public void onArrowPickup(PlayerPickupArrowEvent e) {
		if(!MetaDataUtil.has(e.getArrow(), "item_projectile"))
			return;
		
		// Grab the name of the brItem through metadata
		String brItemName = MetaDataUtil.get(e.getArrow(), "item_projectile").asString();
		
		// Find the brItem
		BrItem brItem = Brewery.getItemCollection().getItem(brItemName);
		
		// If item cannot be found, do nothing
		if(brItem == null)
			return;
		
		// Projectiles dont need UUIDs anymore, as they have a common_cooldown, not a unique_cooldown
//		String brItemUUID = e.getArrow().getMetadata("UUID").get(0).asString();
				
		// TODO: This current iteration simply removes the arrow entity and adds the brItem back to inventory without the arrow moving to the player. Need to find a fix for this.
		// Cancel the arrow being retrieved and remove the entity.
		e.setCancelled(true);
		e.getArrow().remove();
		
		// Get the inventory
		Inventory inventory = e.getPlayer().getInventory();
		
		// And add the correctly generated item.
		inventory.addItem(brItem.generateItem());
		
		// Finally, update any cooldowns.
		// We cant really do this in a seperate method like the others below as its specially added
		CooldownHandler.checkUpdateCooldowns(e.getPlayer());
	}
	
	
	/** ==================================================================================================================
	 *  UUID Item Checks
	 *  ==================================================================================================================
	 */
	
	// TODO: When 'MaxStacks' is implemented, add a checker to ensure the dropped ItemStack adheres to MaxStacks. (
	@EventHandler
	public void onCreativeItemDrop(PlayerDropItemEvent e) {
		if(e.getPlayer().getGameMode() != GameMode.CREATIVE)
			return;
		ItemStack item = e.getItemDrop().getItemStack();
		BrItem brItem = BrItem.getFromItemStack(item);
		if(brItem == null || !ItemEncoder.hasTag(item, "UUID"))
			return;
		
		ItemStack[] invItems = grabInvItems(e.getPlayer().getOpenInventory());
		for(ItemStack invItem : invItems) {
			if(invItem == null)
				continue;
			if(ItemEncoder.hasTag(invItem, "UUID") && ItemEncoder.getString(invItem, "UUID").equals(ItemEncoder.getString(item, "UUID"))) {
				if(e.isCancelled())
					return;
				Messenger.debug("UUID Before: "+ItemEncoder.getString(item, "UUID"));
				e.getItemDrop().setItemStack(brItem.generateItem());
				Messenger.debug("UUID After: "+ItemEncoder.getString(e.getItemDrop().getItemStack(), "UUID"));
				return;
			}
		}
	}
	@EventHandler
	public void onDragItem(InventoryDragEvent e) {
		if(!(e.getWhoClicked() instanceof Player))
			return;
		ItemStack item = e.getOldCursor();
		BrItem brItem = BrItem.getFromItemStack(item);
		if(brItem == null || !ItemEncoder.hasTag(item, "UUID"))
			return;
		
		e.setCancelled(true);
		ItemStack[] invItems = grabInvItems(e.getView());
		
		for(ItemStack invItem : invItems) {
			if(invItem == null)
				continue;
			if(ItemEncoder.hasTag(invItem, "UUID") && ItemEncoder.getString(invItem, "UUID").equals(ItemEncoder.getString(item, "UUID"))) {
				PataneUtil.getInstance().getServer().getScheduler().scheduleSyncDelayedTask(Brewery.getInstance(), new Runnable() {
					@Override
					public void run() {
						if(e.isCancelled())
							return;
						for(int slot : e.getRawSlots()) {
							Messenger.debug("UUID Before: "+ItemEncoder.getString(item, "UUID"));
							e.getView().setItem(slot, brItem.generateItem());
							Messenger.debug("UUID After: "+ItemEncoder.getString(e.getView().getItem(slot), "UUID"));
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
		BrItem brItem = BrItem.getFromItemStack(item);
		if(brItem == null || !ItemEncoder.hasTag(item, "UUID"))
			return;
		
		ItemStack[] invItems = grabInvItems(e.getView());
		
		for(ItemStack invItem : invItems) {
			if(invItem == null)
				continue;
			if(ItemEncoder.hasTag(invItem, "UUID") && ItemEncoder.getString(invItem, "UUID").equals(ItemEncoder.getString(item, "UUID"))) {
				PataneUtil.getInstance().getServer().getScheduler().scheduleSyncDelayedTask(Brewery.getInstance(), new Runnable() {
					@Override
					public void run() {
						if(e.isCancelled())
							return;
						Messenger.debug("UUID Before: "+ItemEncoder.getString(item, "UUID"));
						e.setCurrentItem(brItem.generateItem());
						Messenger.debug("UUID After: "+ItemEncoder.getString(e.getCurrentItem(), "UUID"));
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

	
	@EventHandler
	public void onStorageOpen(InventoryOpenEvent e) {
		// Ignore player inventories
		if(e.getInventory().getHolder() instanceof Player)
			return;
		BrItem.refreshInventory(e.getInventory());
	}

	/** ==================================================================================================================
	 *  Cooldown Checks
	 *  ==================================================================================================================
	 */
	/*
	 * TODO: MaxStacks checks for each cooldown checker (when maxstacks is implemented)
	 */
	
	@EventHandler
	public void onItemSwitch(PlayerItemHeldEvent e) {
		CooldownHandler.checkUpdateCooldowns(e.getPlayer());
	}
	@EventHandler
	public void onJoin(PlayerJoinEvent e) {
		CooldownHandler.checkUpdateCooldowns(e.getPlayer());
		BrItem.refreshInventory(e.getPlayer().getInventory());
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
	
	/* ================================================================================================
	 * Misc. Useful Methods
	 * ================================================================================================
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
		// TODO: Optimise this a little. Many corners can be cut, especially with the entities detection system (eg. just search for closest living instead of ALL)
		@Override
		public void run() {
			List<Entity> entities = item.getNearbyEntities(item.getWidth(), item.getHeight(), item.getWidth());
			// This allows us to ignore the executor whilst the object is being thrown away from them,
			// but lets them be targetted on the way back (eg. throwing it directly upwards)
			
			// If executor is not part of the list, show theyve left
			if(!entities.contains(executor))
				hasLeftExecutor = true;
			// If they havent left, remove them from found list
			if(!hasLeftExecutor)
				entities.remove(executor);
			
			// If entities arent empty
			if(!entities.isEmpty()) {
				List<LivingEntity> living = GeneralUtil.getLiving(entities);
				// If there was a living entity found.
				// TODO: Maybe optimize this with a simple "hasLiving"?
				if(living.isEmpty()) {
					// Stops anyone from picking item up
					item.setPickupDelay(0);
					// Removes the item
					item.remove();
					
					// Hit ground execution
					hitGround(brItem, item.getLocation(), executor);
					
					// Stop this task
					this.cancel();
					return;
				}
				item.setPickupDelay(0);
				item.remove();
				hitEntity(brItem, executor, RadiusUtil.getClosestLivingEntity(living, item.getLocation()));
				this.cancel();
				return;
			}
			Location loc = item.getLocation();
			Block hitBlock;
			if((hitBlock = loc.clone().add(item.getVelocity().multiply(2)).getBlock()).getType().isSolid()) {
				item.setPickupDelay(0);
				item.remove();
				hitGround(brItem, LocationsUtil.getCentre(hitBlock), executor);
				this.cancel();
				return;
			}
		}
	}
}
