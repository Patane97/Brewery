package com.Patane.Brewery.Cooldowns;

import java.util.Date;

import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Item;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import com.Patane.Brewery.Brewery;
import com.Patane.Brewery.CustomItems.BrItem;
import com.Patane.handlers.PlayerDataHandler;
import com.Patane.runnables.PatTimedRunnable;
import com.Patane.util.YAML.types.YAMLData;
import com.Patane.util.general.Messenger;
import com.Patane.util.general.Messenger.Msg;
import com.Patane.util.ingame.InventoriesUtil;
import com.Patane.util.ingame.ItemEncoder;
import com.Patane.util.ingame.ItemsUtil;
import com.Patane.util.main.PataneUtil;


@Deprecated
public class oldCooldownHandler {
	final private BrItem brItem;
	final private float duration;
	final private ItemStack item;
	
	private boolean active;
	
	
	public oldCooldownHandler(BrItem brItem, float duration, ItemStack item) {
		this.brItem = brItem;
		this.duration = duration;
		this.item = item;
	}
	public boolean onoldCooldown() {
		return active;
	}
	/**
	 * @Deprecated
	 * Function outdated. Use {@link #start()} instead.
	 * @param entity
	 * @param slot
	 */
	@Deprecated
	public void start(LivingEntity entity, Integer slot) {
		// If we want to try to allow non-human entities to use items, maybe make two types of PatTimedRunnable cooldowns?
		// One for HumanEntity, another for other entities <--- No need to update item name, only the use of the item
		if(entity instanceof HumanEntity) {
			if(((HumanEntity) entity).getGameMode() == GameMode.CREATIVE) {
				Messenger.send(entity, "&coldCooldowns are currently disabled whilst in Creative Mode due to Spigot Bugs.");
				return;
			}
			new oldCooldown((HumanEntity) entity, brItem, slot);
		}
	}
	public void start() {
		
	}
	private class oldCooldown extends PatTimedRunnable implements Listener {
		HumanEntity owner;
		ItemStack invItem;
		BrItem brItem;
		String completeTime;
		
		String hiddenTag;
		
		State state;
		
		public oldCooldown(HumanEntity owner, BrItem brItem, Integer slot) {
			super(0,1f, duration);
			this.owner = owner;
			this.brItem = brItem;
			this.completeTime = completeTime();
			this.hiddenTag = "oldCooldown ["+this.getID()+"]";
			
			active = true;
			
			// Creates the invItem with the 'bar' as its name (should be full at this point) and the appropriate hiddenTag.
			invItem = ItemEncoder.addTag(ItemsUtil.createItem(item.getType(), 1, (short) 0, constructBar(ticksLeft(), duration(), 20)), "CDTAG", hiddenTag);
			
			// Finds the slot which contains the brItem (this wont always be the players hand. etc-passive items)
			owner.getInventory().setItem(InventoriesUtil.findSlot(owner.getInventory(), brItem.getItemStack().getItemMeta().getDisplayName()), invItem);
			
			// Registers the listener (this)
			Brewery.getInstance().getServer().getPluginManager().registerEvents(this, Brewery.getInstance());
			
			state = new InventoryState(this, owner.getInventory(), slot);
		}

		@EventHandler
		public void onItemDrop(PlayerDropItemEvent e) {
			if(!e.getPlayer().equals(owner))
				return;
			ItemStack dropped = e.getItemDrop().getItemStack();
			if(ItemEncoder.hasTag(dropped, hiddenTag)) {
				state.changeState(new DroppedState(this, e.getItemDrop()));
			}
		}
		
		@EventHandler
		public void onItemFrameRightClick(PlayerInteractEntityEvent e){
			if(!e.getPlayer().equals(owner)) 
				return;
			ItemStack item = e.getPlayer().getInventory().getItemInMainHand();
			if(ItemEncoder.hasTag(item, hiddenTag))
				e.setCancelled(true);
		}
		
		@Override
		public void task() {
			try {
				state.task();
			} catch (Exception e) {
				Messenger.send(Msg.WARNING, hiddenTag+" for item "+brItem.getName()+" has failed its task: ");
				e.printStackTrace();
			}
		}

		@Override
		public void complete() {
			active = false;
			try {
				HandlerList.unregisterAll(this);
				Messenger.debug(owner, "&aItem oldCooldown complete! Ending current state...");
				state.complete();
			} catch (Exception e) {
				state.finish();
				Messenger.send(Msg.WARNING, hiddenTag+" for item "+brItem.getName()+" has failed its completion: ");
				e.printStackTrace();
			}
		}
	}
	/*
	 * ==============================================================================================
	 * 										   Dropped State
	 * 
	 * ==============================================================================================
	 */
	private class DroppedState extends State {
		Item itemEntity;
		
		DroppedState(oldCooldown cooldown, Item itemEntity){
			super(cooldown);
			this.itemEntity = itemEntity;
		}

		/**
		 * Event for oldCooldownItem entity being picked up by an entity
		 * @param e
		 */
		@EventHandler
		public void onItemPickup(EntityPickupItemEvent e) {
			Messenger.debug(cooldown.owner, ">>>>>> Entity Pickup Item ["+e.getEntity().getName()+"]");
			ItemStack pickup = e.getItem().getItemStack();
			if(ItemEncoder.hasTag(pickup, cooldown.hiddenTag)) {
				if(e.getEntity() instanceof HumanEntity) {
					// If the entity is not the original owner, change owners.
					if(cooldown.owner != (HumanEntity) e.getEntity()) {
						Messenger.debug(cooldown.owner, "&cYou are no longer the owner of a oldCooldown Item");
						cooldown.owner = (HumanEntity) e.getEntity();
						Messenger.debug(cooldown.owner, "&aYou are now the owner of a oldCooldown Item");
					}
					changeState(new InventoryState(cooldown, ((HumanEntity) e.getEntity()).getInventory(), null));
				}
				else
					e.setCancelled(true);
			}
		}
		
		/**
		 * Event for oldCooldownItem entity being dropped into a Hopper
		 * @param e
		 */
		@EventHandler
		public void onItemMoveInventory(InventoryMoveItemEvent e) {
			Messenger.debug(cooldown.owner, ">>>>>> Inventory Move Item ["+e.getDestination().getType()+"]");
			ItemStack item = e.getItem();
			if(ItemEncoder.hasTag(item, cooldown.hiddenTag)) {
				if(e.getDestination().getType() != InventoryType.HOPPER && e.getInitiator().getType() != InventoryType.HOPPER) {
					e.setCancelled(true);
					return;
				}
				changeState(new InventoryState(cooldown, e.getDestination(), null));
			}
		}
		
		@Override
		public void complete() {
			itemEntity.setItemStack(cooldown.brItem.getItemStack());
			finish();
		}
		
	}
	/*
	 * ==============================================================================================
	 * 										   Cursor State
	 * 
	 * ==============================================================================================
	 */
	private class CursorState extends State {
		Boolean creativeComplete;
		
		CursorState(oldCooldown cooldown, boolean creativeView){
			super(cooldown);
			this.creativeComplete = (creativeView == true ? false : null);
		}

		/**
		 * Event for oldCooldownItem ItemStack being dragged from the cursor to an inventory
		 * @param e
		 */
		@EventHandler
		public void onItemDrag(InventoryDragEvent e) {
			Messenger.debug(cooldown.owner, ">>>>>> Cursor Drag");
			// If clicking outside inventory.
			if(e.getInventory() == null)
				return;
			if(!validInventory(e.getInventory())) {
				e.setCancelled(true);
				Messenger.send(cooldown.owner, "&c &7"+cooldown.brItem.getName()+" &cis on cooldown. You cannot put a cooldown item in this type of inventory.");
				return;
			}
			ItemStack cursor = e.getOldCursor();
			// If the player clicks something that isnt a slot (such as the border of the Inventory)
			if(cursor == null)
				return;
			if(ItemEncoder.hasTag(cursor, cooldown.hiddenTag))
				changeState(new InventoryState(cooldown, e.getInventory(), null));
		}
		/**
		 * Event for oldCooldownItem ItemStack being placed into an inventory
		 * @param e
		 */
		@EventHandler
		public void onItemClick(InventoryClickEvent e) {
			Messenger.debug(cooldown.owner, ">>>>>> Cursor Click ["+e.getAction()+"]");
			Messenger.debug(cooldown.owner, ">>>>>> view ["+e.getView().getType()+"]");
			if(!e.getWhoClicked().equals(cooldown.owner) || e.getAction() == InventoryAction.NOTHING)
				return;
			
			/*
			 * STOPPING PLAYER FROM USING INVALID INVENTORIES
			 * (eg. Crafting Table)
			 * 
			 */
			// If clicking outside inventory.
			if(e.getClickedInventory() == null)
				return;
			
			// Checking if the inventory is valid.
			if(!validInventory(e.getClickedInventory())) {
				e.setCancelled(true);
				Messenger.send(cooldown.owner, "&c &7"+cooldown.brItem.getName()+" &cis on cooldown. You cannot put a cooldown item in this type of inventory.");
				return;
			}
			// Stops the system from breaking when player double-clicks to place the item from cursor to inventory.
			if(e.getAction() == InventoryAction.COLLECT_TO_CURSOR) {
				e.setCancelled(true);
				return;
			}
			ItemStack clicked = e.getCurrentItem();
			// If the player clicks something that isnt a slot (such as the border of the Inventory)
			if(clicked == null)
				return;
			
			/* *************************************************************************
			 * SPIGOT BUXFIX CODE FOR CREATIVE MODE
			 * 
			 * If the cooldown is over and the player is placing the held item back into the inventory.
			 * This is to avoid the following issue with no known cause:
			 *		Holding item after it changes to brItem.getItem() within complete() causes
			 *		the brItem.getItem() to drop from the player once inventory is closed.
			 */		
			if(creativeComplete != null && creativeComplete == true) {
				// If an actual ItemStack was clicked
				if(clicked.getType() != Material.AIR) {
					cooldown.owner.setItemOnCursor(clicked);
				}
				// If air/nothing was clicked, causing the cursor item to be placed in the clicked slot
				else {
					cooldown.owner.setItemOnCursor(null);
					// Must be 1 tick after the @EventHandler event
					PataneUtil.getInstance().getServer().getScheduler().runTask(PataneUtil.getInstance(), new Runnable() {
						public void run(){
							finish();
						}
					});
				}
				return;
			}
			
			// *************************************************************************
			ItemStack cursor = e.getCursor();
			// oldCooldown ItemStack is being placed in an inventory. 
			// Change state to that inventory.
			if(ItemEncoder.hasTag(cursor, cooldown.hiddenTag))
				changeState(new InventoryState(cooldown, e.getClickedInventory(), null));
		}

		/**
		 * Event for Inventory being closed whilst item is on the cursor
		 * @param e
		 */
		@EventHandler
		public void onInventoryClose(InventoryCloseEvent e) {
			Messenger.debug(cooldown.owner, ">>>>>> Inventory Close ["+e.getInventory().getName()+"]");
			if(!e.getPlayer().equals(cooldown.owner))
				return;
			finish();
		}
		
		@Override
		public void complete() {
			// Causes bug (Fix shown above)
			// When an item is set using below function, it doesnt change item on cursor  after 
			// item has been removed from cursor (such as placing it in an inventory).
			
			cooldown.owner.setItemOnCursor(cooldown.brItem.getItemStack());
			PataneUtil.getInstance().getServer().getScheduler().runTask(PataneUtil.getInstance(), new Runnable() {
				public void run(){
					if(creativeComplete != null)
						creativeComplete = true;
					else
						finish();
				}
			});
		}
		
	}
	/*
	 * ==============================================================================================
	 * 										Inventory State
	 * 
	 * ==============================================================================================
	 */
	private class InventoryState extends State {
		Inventory inventory;
		
		// Used within task to reduce lag.
		// Achieves this by removing the need to search for the item each tick.
		// Instead, searches for the item only if it cant find the correct ItemStack in slot.
		Integer slot;
		
		InventoryState(oldCooldown cooldown, Inventory inventory , Integer slot){
			super(cooldown);
			this.inventory = inventory;
			this.slot = slot;
			addData(cooldown.owner, slot);
		}
		/**
		 * Event for oldCooldownItem Itemstack being clicked within an inventory
		 * @param e
		 */
		@EventHandler
		public void onItemClick(InventoryClickEvent e) {
			if(!e.getWhoClicked().equals(cooldown.owner) || e.getAction() == InventoryAction.NOTHING)
				return;
			Messenger.debug(cooldown.owner, ">>>>>> Inventory Click ["+e.getAction()+"]");
			onItemClickRun(e, e.getView().getType() == InventoryType.CREATIVE);
		}
		
		private void onItemClickRun(InventoryClickEvent e, boolean creativeInventory) {
			Messenger.debug(cooldown.owner, ">>>>>> view ["+e.getView().getType()+"]");
			Messenger.debug(cooldown.owner, ">>>>>> click ["+e.getClick()+"]");
			Messenger.debug(cooldown.owner, ">>>>>> slot ["+e.getSlot()+"]");
			Messenger.debug(cooldown.owner, ">>>>>> rawSlot ["+e.getRawSlot()+"]");
			ItemStack clicked = e.getCurrentItem();
			if(ItemEncoder.hasTag(clicked, cooldown.hiddenTag)) {
				// 'ClickType.MIDDLE never triggers in a creative inventory. Find workaround
//				if(e.getClick() == ClickType.MIDDLE && creativeInventory) {
//					Messenger.debug(cooldown.owner, "&7Cloning Stack 1");
//					e.setCancelled(true);
//					PataneUtil.getInstance().getServer().getScheduler().runTask(PataneUtil.getInstance(), new Runnable() {
//						public void run() {
//							Messenger.(cooldown.owner, "&7Cloning Stack 2");
//							cooldown.owner.setItemOnCursor(cooldown.brItem.getItem());
//						}
//					});
//					return;
//				}
				if(e.getAction() == InventoryAction.MOVE_TO_OTHER_INVENTORY) {
					if(e.getRawSlot() < e.getView().getTopInventory().getSize())
						inventory = e.getView().getBottomInventory();
					else
						inventory = e.getView().getTopInventory();
				}
				else if(e.getAction() == InventoryAction.PICKUP_ALL
					 || e.getAction() == InventoryAction.PICKUP_HALF
				  	 || e.getAction() == InventoryAction.PICKUP_ONE
					 || e.getAction() == InventoryAction.PICKUP_SOME
					 || e.getAction() == InventoryAction.PLACE_ALL
					 || e.getAction() == InventoryAction.PLACE_ONE
					 || e.getAction() == InventoryAction.PLACE_SOME
					 || e.getAction() == InventoryAction.COLLECT_TO_CURSOR
					 || e.getAction() == InventoryAction.SWAP_WITH_CURSOR) {
						changeState(new CursorState(cooldown, creativeInventory));
				}
			}
		}
		/**
		 * Event for oldCooldownItem ItemStack being moved to another inventory
		 * @param e
		 */
		@EventHandler
		public void onItemMoveInventory(InventoryMoveItemEvent e) {
			Messenger.debug(cooldown.owner, ">>>>>> Inventory Move ["+e.getDestination().getName()+"]");
			ItemStack moved = e.getItem();
			if(ItemEncoder.hasTag(moved, cooldown.hiddenTag)) {
				if(!validInventory(e.getDestination())) {
					e.setCancelled(true);
					return;
				}
				HumanEntity before;
				HumanEntity after;
				if(inventory.getType() == InventoryType.PLAYER || inventory.getType() == InventoryType.CREATIVE) {
					before = cooldown.owner;
					after = null;
				} else {
					before = null;
					after = cooldown.owner;
				}
				delData(before);
				inventory = e.getDestination();
				Brewery.getInstance().getServer().getScheduler().runTask(Brewery.getInstance(), new Runnable() {
					public void run() {
						addData(after, InventoriesUtil.findSlotWithTag(inventory, cooldown.hiddenTag));
					}
				});
			}
		}
		
		@Override
		public void task() {
			// slot will only ever be null on the first task().
			ItemStack itemInSlot = (slot == null ? null : inventory.getItem(slot));
			
			// If the item within slot is not the invItem in which is should be.
			if(itemInSlot == null 
			|| !ItemsUtil.hasDisplayName(itemInSlot) 
			|| !ItemsUtil.getDisplayName(itemInSlot).equals(ItemsUtil.getDisplayName(cooldown.invItem))) {
				slot = InventoriesUtil.findSlotWithTag(inventory, cooldown.hiddenTag);
				Messenger.debug(cooldown.owner, "&boldCooldown slot updated to &7"+slot+"&b...");
				itemInSlot = inventory.getItem(slot);
			}
			// Creates a new item with the correct bar size and hiddenTag and sets 'slot' to that item.
			//***************************
			// If in an inventory thats not players, make it so it only updates if the inventory (eg. chest) has a VIEWER. Reduces lag.
			//***************************
			cooldown.invItem = ItemEncoder.addTag(ItemsUtil.setItemNameLore(itemInSlot, constructBar(cooldown.ticksLeft(), cooldown.duration(), 20)), "CDTAG", cooldown.hiddenTag);
		}
		@Override
		public void complete() {
			inventory.setItem(slot, cooldown.brItem.getItemStack());
			finish();
		}
	}
	
	private abstract class State implements Listener {
		protected oldCooldown cooldown;
		
		State(oldCooldown cooldown) {
			this.cooldown = cooldown;
			Brewery.getInstance().getServer().getPluginManager().registerEvents(this, Brewery.getInstance());
		}
		
		public abstract void complete();
		
		public void task() {}
		protected State changeState(State newState) {
			Messenger.debug(cooldown.owner, "&bChanging CD State to &7"+newState.getClass().getSimpleName()+"&b...");
			HandlerList.unregisterAll(this);
			cooldown.state = newState;
			return newState;
		}

		protected void addData(HumanEntity entity, int slot) {
			if(entity == null || entity instanceof Player) {
				PlayerDataHandler.addData((Player) entity, cooldown.completeTime, "item_cooldowns", brItem.getName()+"["+cooldown.getID()+"]", "timestamp");
				PlayerDataHandler.addData((Player) entity, slot, "item_cooldowns", brItem.getName()+"["+cooldown.getID()+"]", "slot");
			}
		}
		protected void  delData(HumanEntity entity) {
			if(entity == null || entity instanceof Player) {
				PlayerDataHandler.removeData((Player) entity, "item_cooldowns", brItem.getName()+"["+cooldown.getID()+"]");
			}
		}
		protected void finish() {
			HandlerList.unregisterAll(this);
			Messenger.debug(cooldown.owner, "&2Ended state &7"+this.getClass().getSimpleName()+"&2!");
		}
	}
	/**
	 * Checks if the given inventory is allowed to hold a oldCooldown Item.
	 * @param inventory
	 * @return
	 */
	private boolean validInventory(Inventory inventory) {
		if(inventory.getType() == InventoryType.PLAYER
		|| inventory.getType() == InventoryType.CREATIVE
		|| inventory.getType() == InventoryType.CHEST
		|| inventory.getType() == InventoryType.HOPPER)
			return true;
		return false;
	}
	
	/**
	 * Constructs the ItemStack displayName for the oldCooldown Item.
	 * @param current
	 * @param max
	 * @param width
	 * @return
	 */
	private String constructBar(float current, float max, int width) {
		current = (width/max)*current;
		max = width;
		int blockCount = (Math.round(current));
		int remainder = width-blockCount;
		// Colour of remaining time
		String bar = "&f";
		for(int i=0 ; i < blockCount ; i++)
			bar = bar+"█";
		// Background colour
		bar = bar+"&0";
		for(int i=0 ; i < remainder ; i++)
			bar = bar+"█";
		return bar;
	}
	private String completeTime() {
		return YAMLData.simpleDateFormat().format(new Date(System.currentTimeMillis() + ((long)(duration * 1000))));
	}
}
