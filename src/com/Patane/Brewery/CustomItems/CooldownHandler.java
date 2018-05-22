package com.Patane.Brewery.CustomItems;

import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;

import com.Patane.Brewery.Brewery;
import com.Patane.runnables.PatTimedRunnable;
import com.Patane.util.general.Messenger;
import com.Patane.util.general.Messenger.Msg;
import com.Patane.util.ingame.ItemEncoder;
import com.Patane.util.ingame.ItemsUtil;

public class CooldownHandler {
	final private BrItem brItem;
	final private float duration;
	final private ItemStack item;
	
	private boolean active;
	
	public CooldownHandler(BrItem brItem, float duration, ItemStack item) {
		this.brItem = brItem;
		this.duration = duration;
		this.item = item;
	}
	public boolean onCooldown() {
		return active;
	}
	public void start(LivingEntity entity) {
		if(entity instanceof HumanEntity) {
			new Cooldown((HumanEntity) entity, ((HumanEntity) entity).getInventory().getHeldItemSlot());
		}
	}
	private class Cooldown extends PatTimedRunnable implements Listener {
		HumanEntity entity;
		ItemStack taggedItem;
		String metaTag;
		String bar;
		int currentSlot;
		
		boolean cursor;
		boolean complete;
		
		public Cooldown(HumanEntity entity, int currentSlot) {
			super(0,0.05f, duration);
			this.entity = entity;
			this.currentSlot = currentSlot;
			metaTag = "Cooldown ["+this.getID()+"]";
			bar = constructBar(ticksLeft(), duration(), 20);
			taggedItem = ItemsUtil.createItem(item.getType(), 1, (short) 0, bar);
			taggedItem = ItemEncoder.addTag(taggedItem, metaTag);
			Messenger.send(Msg.BROADCAST, ItemEncoder.extractTag(taggedItem));
			entity.getInventory().setItem(currentSlot, taggedItem);
			Brewery.getInstance().getServer().getPluginManager().registerEvents(this, Brewery.getInstance());
		}
		/**
		 * Detects when the user moves the Cooled-down item around their inventory.
		 * @param e
		 */
		@EventHandler
		public void onItemClick(InventoryClickEvent e) {
			// Only interact with entity.
			if(!e.getWhoClicked().equals(entity))
				return;
			// Get the item which is being clicked in the inventory.
			ItemStack tempItem = e.getCurrentItem();
			// If the cooldown is over and the player is placing the held item back into the inventory.
			// This is to avoid the following issue with no known cause:
			//		Holding item after it changes to brItem.getItem() within complete() causes
			//		the brItem.getItem() to drop from the player once inventory is closed.
			if(cursor && complete) {
				Messenger.debug(entity, "cursorAndComplete");
				
				// If the item thats being swapped is a blank slot
				if(tempItem != null && tempItem.getType() == Material.AIR) {
					Messenger.debug(entity, "itemIsAir");
					
					// Sets the item on the cursor to be null.
					// If the item on the cursor is null when the inventory closes, the issue TECHNICALLY still happens,
					// however dropping a 'null' item from the inventory does nothing.
					entity.setItemOnCursor(null);
					
					Messenger.debug(entity, "Unregistering 2");
					
					// We can stop monitoring this entities inventory movements once they finally put their cursor item down.
					HandlerList.unregisterAll(this);
					return;
				}
				else if(tempItem != null && tempItem.getType() != Material.AIR) {
					Messenger.debug(entity, "itemIsNOTAir");
					entity.setItemOnCursor(tempItem);
					return;
				}
			}
			//
			if(tempItem != null && ItemsUtil.getDisplayName(tempItem) != null && metaTag.equals(ItemEncoder.extractTag(tempItem))) {
				Messenger.debug(entity, "itemToCursor: SLOT="+e.getSlot());
				// This means that the item is being switched to the cursor.
				cursor = true;
				return;
			}
			tempItem = e.getCursor();
			if(tempItem != null && ItemsUtil.getDisplayName(tempItem) != null && metaTag.equals(ItemEncoder.extractTag(tempItem))) {
				// This means that the item was just placed from the cursor to the e.getSlot() slot.
				Messenger.debug(entity, "itemToSlot="+ItemsUtil.getDisplayName(tempItem));
				currentSlot = e.getSlot();
				cursor = false;
			}
		}
		@EventHandler
		public void onInventoryClose(InventoryCloseEvent e) {
			if(!e.getPlayer().equals(entity))
				return;
			if(cursor && complete) {
				Messenger.debug(entity, "Unregistering 3");
				HandlerList.unregisterAll(this);
			}
			
		}
//		@EventHandler
//		public void onItemDrag(InventoryDragEvent e) {
//			if(!e.getWhoClicked().equals(entity))
//				return;
//			ItemStack tempItem = e.getOldCursor();
//			if(tempItem != null && ItemsUtil.getDisplayName(tempItem) != null)
//				if(ItemEncoder.extractTag(tempItem).equals(metaTag))
//					e.setCancelled(true);
//		}
		@Override
		public void task() {
			// If item is on the cursor, do nothing
			if(cursor)
				return;
			bar = constructBar(ticksLeft(), duration(), 20);
			entity.getInventory().setItem(currentSlot, ItemEncoder.addTag(ItemsUtil.setItemNameLore(taggedItem, bar), metaTag));
		}

		@Override
		public void complete() {
			if(cursor) {
				entity.setItemOnCursor(brItem.getItem());
				Messenger.debug(entity, "Swaping Hand");
				complete = true;
			}
			else {
				Messenger.debug(entity, "Unregistering 1");
				HandlerList.unregisterAll(this);
				entity.getInventory().setItem(currentSlot, brItem.getItem());
				Messenger.debug(entity, "Swaping slot: "+currentSlot);
			}
		}
	}
	private String constructBar(float current, float max, int width) {
		current = (width/max)*current;
		max = width;
		int blockCount = (Math.round(current));
		int remainder = width-blockCount;
		// Colour of remaining time
		String bar = "&f";
		for(int i=0 ; i<=blockCount ; i++)
			bar = bar+"█";
		// Background colour
		bar = bar+"&0";
		for(int i=0 ; i<=remainder ; i++)
			bar = bar+"█";
		return bar;
	}
}
