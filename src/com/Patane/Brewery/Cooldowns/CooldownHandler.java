package com.Patane.Brewery.Cooldowns;

import java.util.Date;
import java.util.Map;
import java.util.TreeMap;
import java.util.UUID;

import javax.annotation.Nonnull;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.Patane.Brewery.Brewery;
import com.Patane.Brewery.CustomItems.BrItem;
import com.Patane.util.YAML.types.YAMLData;
import com.Patane.util.general.Messenger;
import com.Patane.util.ingame.InventoriesUtil;
import com.Patane.util.ingame.ItemEncoder;
import com.Patane.util.main.PataneUtil;
import com.Patane.util.metadata.MetaDataUtil;
import com.Patane.util.metadata.persistent.PersistentDataType;

public class CooldownHandler {
	/** =========================================================
	 *  Static YML Section
	 *  =========================================================
	 */
	private static YAMLData yml;

	public static void setYML(YAMLData yml) {
		CooldownHandler.yml = yml;
	}
	public static YAMLData YML() {
		return yml;
	}
	/**
	 *  =========================================================
	 */

	private static Map<UUID, CooldownTracker> cooldowns = new TreeMap<UUID, CooldownTracker>();
	
	public static Map<UUID, CooldownTracker> cooldowns() {
		return cooldowns;
	}
	public static boolean noCooldowns() {
		if(!YML().containsData() && cooldowns.isEmpty())
			return true;
		return false;
	}
	public static boolean start(@Nonnull LivingEntity entity, @Nonnull ItemStack item, @Nonnull BrItem brItem) {
		UUID uuid = getUUID(entity, item, brItem);
		Messenger.debug(uuid.toString());
		// If it is currently on cooldown, return false.
		if(cooldowns.containsKey(uuid))
			return false;
		
		CooldownTracker tracker;
		// Getting the appropriate cooldown tracker
		if(brItem.getType().isUnique())
			tracker = new UniqueCooldown(entity, brItem, uuid);
		else
			tracker = new CommonCooldown(entity, brItem);
		
		// Add cooldown to the masterlist
		cooldowns.put(uuid, tracker);
		
		// Add cooldown to cooldowns.yml
		YML().addData(cooldowns.get(uuid).getCompleteTime(), uuid.toString());
		
		Messenger.debug(String.format("+ Cooldown(%s, %s)", brItem.getName(), uuid.toString()));
		return true;
	}
	
	public static void end(UUID uuid) {
		String itemName = null;
		if(cooldowns.containsKey(uuid))
			itemName = cooldowns.get(uuid).getItem().getName();
		
		cooldowns.get(uuid).clearPlayers();
		
		// Remove cooldown from masterList
		cooldowns.remove(uuid);
		
		// Remove cooldown from cooldowns.yml
		YML().removeData(uuid.toString());
		
		Messenger.debug(String.format("- Cooldown(%s, %s)", itemName, uuid.toString()));
	}
	private static UUID getUUID(@Nonnull LivingEntity entity, @Nonnull ItemStack item, @Nonnull BrItem brItem) throws IllegalArgumentException {
		if(brItem.getType().isUnique()) {
			String itemUUID = ItemEncoder.getString(item, "UUID");
			if(itemUUID == null)
				throw new IllegalArgumentException(String.format("%s is missing is a Unique Item and is missing its ItemStacks UUID tag.", brItem.getName()));
			
			return UUID.fromString(itemUUID);
		}
		else {
			if(entity instanceof Player)
				return ((Player) entity).getUniqueId();
			else {
				UUID entityTempUUID = entity.getPersistentDataContainer().get(CommonCooldown.TEMP_UUID_KEY, PersistentDataType.UUID);
				if(entityTempUUID == null)
					throw new IllegalArgumentException(String.format("%s entity is missing its temporary UUID tag.", entity.getName()));
				return entityTempUUID;
			}
		}
	}
	
	/**
	 * Note: When using this method, it is already assumed that 'item' is classified as a BrItem and therefore has a UUID attached to it. If not, an IllegalArgumentException will be thrown.
	 * @param entity
	 * @param item
	 * @param brItem
	 * @return
	 */
	public static boolean updateFromYAML(@Nonnull LivingEntity entity, @Nonnull ItemStack item, @Nonnull BrItem brItem) {
		if(item == null || brItem == null)
			return false;
		
		UUID uuid = getUUID(entity, item, brItem);
		
		// Checks if the uuid is currently on cooldown.
		if(cooldowns.containsKey(uuid)) {
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
			// duration remaining minus difference in ticks (seconds * 20)
			cooldowns.get(uuid).subtract((cooldowns.get(uuid).duration())-((difference/1000)*20));
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
				// Grabbing ItemStack and uuidString information
				ItemStack item = player.getInventory().getItemInMainHand();
				
				// If its a bow, actually show the cooldown of the arrow.
				// *** Should maybe check if the BOW ITSELF has a cooldown first?
				if(InventoriesUtil.isBowMaterial(item.getType())) {
					ItemStack arrow = InventoriesUtil.getTargettedArrowStack(player);
					item = (arrow == null ? item : arrow);
				}
				
				BrItem brItem = BrItem.getFromItemStack(item);
				
				if(brItem == null)
					return;
				
				UUID uuid;
				try{
					uuid = getUUID(player, item, brItem);
				} catch (IllegalArgumentException e) {
					return;
				}
				
				
				
				if(MetaDataUtil.has(player, UniqueCooldown.META_KEY)) {
					UUID previousUUID = (UUID) MetaDataUtil.get(player, UniqueCooldown.META_KEY).value();
					if(!uuid.equals(previousUUID))
						CooldownHandler.cooldowns().get(previousUUID).removePlayer(player);
				}
				else if(MetaDataUtil.has(player, CommonCooldown.META_KEY)) {
					String previousItem = MetaDataUtil.get(player, CommonCooldown.META_KEY).asString();
					if(!brItem.getName().equals(previousItem))
						CooldownHandler.cooldowns().get(player.getUniqueId()).removePlayer(player);
				}
				// If the player currently has a cooldown being shown,
				// and the UUID of cooldown ISNT the same as the UUID of the item in their hand (The UUID's are the same in the case of a /reload or server restart)
				// Then remove the player from the previous cooldown.
				// TLDR: When player swaps from one CD item to another CD item, it removes the previous CD from their display.
//				if(MetaDataUtil.has(player, "showing_cooldown") && !MetaDataUtil.get(player, "showing_cooldown").value().equals(UUID.fromString(uuidString))) {
//					UUID uuid = (UUID) MetaDataUtil.get(player, "showing_cooldown").value();
//					CooldownHandler.cooldowns().get(uuid).removePlayer(player);
//				}
					
				// Either adds the user to a currently runnning Cooldown with the same UUID as their item
				// or it extracts the saved cooldown from YAML and subtracts the remaining time appropriately, then adds the player.
				// Both of these cases return true, otherwise we continue.
				if(CooldownHandler.updateFromYAML(player, item, BrItem.getFromItemStack(item)))
					return;
				
				// Loops through each UUID on cooldown and compares it to their held item UUID.
				// If they match, player is added to the cooldown display list.
				for(UUID cooldownUUID : CooldownHandler.cooldowns().keySet()) {
					if(cooldownUUID.equals(uuid))
						CooldownHandler.cooldowns().get(uuid).addPlayer(player);
				}
			}
		});
	}
}
