package com.Patane.Brewery.Cooldowns;

import java.util.Date;
import java.util.Map;
import java.util.TreeMap;
import java.util.UUID;

import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.ItemStack;

import com.Patane.Brewery.CustomItems.BrItem;
import com.Patane.util.YAML.types.YAMLData;
import com.Patane.util.general.Messenger;
import com.Patane.util.general.Messenger.Msg;
import com.Patane.util.ingame.ItemEncoder;

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
		Messenger.debug(Msg.INFO, "Cooldown starting: UUID="+uuid.toString()+", brItem="+brItem.getName());
		return true;
	}
	public static boolean ready(UUID uuid) {
		return !cooldowns.containsKey(uuid);
	}
	public static void end(UUID uuid) {
		cooldowns.remove(uuid);
		YML().removeData(uuid.toString());
		Messenger.debug(Msg.INFO, "Cooldown ending: UUID="+uuid.toString());
	}
	public static UUID getUUID(ItemStack item) {
		String uuidString = ItemEncoder.extractTag(item, "UUID");
		if(uuidString == null)
			throw new IllegalArgumentException("UUID required for Cooldown is missing!");
		return UUID.fromString(uuidString);
	}
	public static boolean updateFromYAML(LivingEntity entity, ItemStack item, BrItem brItem) {
		if(item == null || brItem == null)
			return false;
		UUID uuid = getUUID(item);
		if(cooldowns.containsKey(uuid)) {
			return false;
		}
		Date date;
		try {
			date = (Date) YML().retrieveData(uuid.toString());
			if(date == null) {
				return false;
			}
		} catch (ClassCastException e) {
			e.printStackTrace();
			return false;
		}
		// FIX THIS (DOESNT GET REMAINING TIME PROPERLY)
		// If the current time is past the retrieved time
		long difference = System.currentTimeMillis()-date.getTime();
		if(difference < 0) {
			start(entity, item, brItem);
			cooldowns.get(uuid).add(cooldowns.get(uuid).duration()/20-(difference/60));
			return true;
		}
		else
			end(uuid);
		return false;
		
	}
}
