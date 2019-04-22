package com.Patane.Brewery.Cooldowns;

import java.util.Date;
import java.util.Map;
import java.util.TreeMap;
import java.util.UUID;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.Patane.Brewery.Brewery;
import com.Patane.Brewery.CustomItems.BrItem;
import com.Patane.Brewery.Handlers.BrMetaDataHandler;
import com.Patane.util.YAML.types.YAMLData;
import com.Patane.util.general.Messenger;
import com.Patane.util.ingame.ItemEncoder;
import com.Patane.util.main.PataneUtil;

public class CooldownHandler {
	/**
	 * ******************* STATIC YML SECTION *******************
	 */
	private static YAMLData yml;

	public static void setYML(YAMLData yml){
		CooldownHandler.yml = yml;
	}
	public static YAMLData YML(){
		return yml;
	}
	/**
	 * **********************************************************
	 */

	private static Map<UUID, CooldownTracker> cooldowns = new TreeMap<UUID, CooldownTracker>();
	
	public static Map<UUID, CooldownTracker> cooldowns(){
		return cooldowns;
	}
	public static boolean noCooldowns() {
		if(!YML().containsData() && cooldowns.isEmpty())
			return true;
		return false;
	}
	public static boolean start(LivingEntity entity, ItemStack item, BrItem brItem) {
		UUID uuid = getUUID(item);
		if(!ready(uuid))
			return false;
		cooldowns.put(uuid, new CooldownTracker(entity, uuid, brItem));
		YML().addData(cooldowns.get(uuid).getDate(), uuid.toString());
		Messenger.debug("Cooldown starting: UUID="+uuid.toString()+", brItem="+brItem.getName());
		return true;
	}
	public static boolean ready(UUID uuid) {
		return !cooldowns.containsKey(uuid);
	}
	public static void end(UUID uuid) {
		cooldowns.remove(uuid);
		YML().removeData(uuid.toString());
		Messenger.debug("Cooldown ending: UUID="+uuid.toString());
	}
	public static UUID getUUID(ItemStack item) throws IllegalArgumentException{
		String uuidString = ItemEncoder.getString(item, "UUID");
		if(uuidString == null)
			throw new IllegalArgumentException("UUID required for Cooldown is missing!");
		return UUID.fromString(uuidString);
	}
	
	/**
	 * ***When using this method, it is already assumed that 'item' is classified as a BrItem and therefore has a UUID attached to it. If not, an IllegalArgumentException will be thrown.
	 * @param entity
	 * @param item
	 * @param brItem
	 * @return
	 */
	public static boolean updateFromYAML(LivingEntity entity, ItemStack item, BrItem brItem) {
		// Nullchecks
		if(item == null || brItem == null)
			return false;
		
		// Constructs UUID taken from item (will throw IllegalArgumentException if item doesnt have UUID).
		UUID uuid = getUUID(item);
		
		// Checks if the uuid is currently on cooldown.
		if(cooldowns.containsKey(uuid)){
			// If the entity is a player and they are not on the display list for this cooldown, add them and return true.
			if(entity instanceof Player && !(cooldowns.get(uuid).hasPlayer((Player) entity))) {
				cooldowns.get(uuid).addPlayer((Player) entity);
				return true;
			}
			return false;
		}
		// Retrieve the uuid's date within 'cooldowns.yml'
		Date date = YML().retrieveData(Date.class, uuid.toString());
		if(date == null)
			return false;
		
		// Calculating the difference in milliseconds between the current time and the cooldown completion time.
		long difference = date.getTime()-System.currentTimeMillis();
		
		// If the cooldown is not meant to be over yet, it starts a new cooldown and subtracts the remaining time from it.
		if(difference > 0) {
			start(entity, item, brItem);
			cooldowns.get(uuid).subtract((cooldowns.get(uuid).duration()/20)-(difference/1000));
			return true;
		}
		else
			end(uuid);
		return false;
		
	}
	public static void onLoadChecks() {
		for(Player player : Brewery.getInstance().getServer().getOnlinePlayers()) {
			checkUpdateCooldowns(player);
		}
	}
	
	/**
	 * If there are cooldowns running either on server or saved in 'cooldowns.yml', this method will
	 * find it and ensure its updated, running, and displayed to the player if necessary.
	 * @param player
	 */
	public static void checkUpdateCooldowns(Player player) {
		// Checks if there are no cooldowns currently running.
		if(CooldownHandler.noCooldowns())
			return;
		// Running a task in the next available server tick.
		// Because this method is usually run with an event, this makes sure we are only working with the RESULT of the event and within the events changes.
		PataneUtil.getInstance().getServer().getScheduler().scheduleSyncDelayedTask(Brewery.getInstance(), new Runnable() {
			@Override
			public void run() {
				// Gravving ItemStack and uuidString information
				ItemStack item = player.getInventory().getItemInMainHand();
				String uuidString = ItemEncoder.getString(item, "UUID");
				if(uuidString == null)
					return;
				
				// If the player currently has a cooldown being shown,
				// and the UUID of cooldown ISNT the same as the UUID of the item in their hand (The UUID's are the same in the case of a /reload or server restart)
				// Then remove the player from the previous cooldown.
				// TLDR: When player swaps from one CD item to another CD item, it removes the previous CD from their display.
				if(BrMetaDataHandler.hasValue(player, "showing_cooldown") && !BrMetaDataHandler.getValue(player, "showing_cooldown").equals(UUID.fromString(uuidString)))
					CooldownHandler.cooldowns().get(BrMetaDataHandler.getValue(player, "showing_cooldown")).removePlayer(player);

				// Either adds the user to a currently runnning Cooldown with the same UUID as their item
				// or it extracts the saved cooldown from YAML and subtracts the remaining time appropriately, then adds the player.
				// Both of these cases return true, otherwise we continue.
				if(CooldownHandler.updateFromYAML(player, item, BrItem.get(item)))
					return;
				
				// Loops through each UUID on cooldown and compares it to their held item UUID.
				// If they match, player is added to the cooldown display list.
				for(UUID uuid : CooldownHandler.cooldowns().keySet()) {
					if(uuidString.equals(uuid.toString()))
						CooldownHandler.cooldowns().get(uuid).addPlayer(player);
				}
			}
		});
	}
}
