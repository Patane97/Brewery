package com.Patane.Brewery.Cooldowns;

import java.util.ArrayList;
import java.util.UUID;

import org.bukkit.NamespacedKey;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.Patane.Brewery.CustomItems.BrItem;
import com.Patane.util.general.Chat;
import com.Patane.util.general.Messenger;
import com.Patane.util.ingame.InventoriesUtil;
import com.Patane.util.main.PataneUtil;
import com.Patane.util.metadata.MetaDataUtil;
import com.Patane.util.metadata.persistent.PersistentDataType;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;

public class CommonCooldown extends CooldownTracker {
	
	private final LivingEntity entity;

	public static final String META_KEY = "common_cooldown";
	public static final NamespacedKey TEMP_UUID_KEY = new NamespacedKey(PataneUtil.getInstance(), "temp_uuid");
	
	public CommonCooldown(LivingEntity entity, BrItem item) {
		super(entity, item);
		this.entity = entity;
		
		if(entity instanceof Player)
			addPlayer((Player) entity);
		// If theyre not a player, give them a persistent (will stay through restarts), temporary UUID to track their common cooldown!
		else if(!entity.getPersistentDataContainer().has(TEMP_UUID_KEY, PersistentDataType.UUID));
			entity.getPersistentDataContainer().set(TEMP_UUID_KEY, PersistentDataType.UUID, UUID.randomUUID());

	}
	
	public UUID trackableUUID() {
		if(entity instanceof Player)
			return((Player) entity).getUniqueId();
		return entity.getPersistentDataContainer().get(TEMP_UUID_KEY, PersistentDataType.UUID);
	}
	
	@Override
	public boolean addPlayer(Player player) {
		if(showing.add(player)) {
			MetaDataUtil.setFixed(player, META_KEY, item.getName());
			updateActionBar(player);
			return true;
		}
		return false;
	}

	@Override
	public boolean removePlayer(Player player) {
		if(showing.remove(player)) {
			MetaDataUtil.remove(player, META_KEY);
			clearActionBar(player);
			return true;
		}
		return false;
	}

	@Override
	public void task() {
		// Loop through each player being shown.
		for(Player player : new ArrayList<Player>(showing)) {
			// Grab the itemstack in their main hand. We should show this cooldown!
			ItemStack itemStack = player.getInventory().getItemInMainHand();
			
			// If its a bow, actually show the cooldown of the arrow it will be loading.
			// TODO: Should maybe check if the BOW ITSELF has a cooldown first?
			if(InventoriesUtil.isBowMaterial(itemStack.getType())) {
				// Grab the arrow that will be loaded into the bow, or null if none exists.
				ItemStack arrow = InventoriesUtil.getTargettedArrowStack(player);
				// Save item as the arrow IF its not null.
				itemStack = (arrow == null ? itemStack : arrow);
			}
			// Get the brItem from the chosen ItemStack.
			BrItem brItem = BrItem.getFromItemStack(itemStack);
			
			// If the chosen ItemStack is not a BrItem OR is not the SAME BrItem as this cooldown, then we stop showing.
			if(brItem == null || !brItem.getName().equals(item.getName()))
				removePlayer(player);
			
			// Otherwise, the player IS holding this item, so we show them the cooldown in the action bar!
			else
				Messenger.sendRaw(player, ChatMessageType.ACTION_BAR, new TextComponent(Chat.translate(constructBar(ticksLeft(), duration(), 20))));
		}
	}

	@Override
	public void complete() {
		CooldownHandler.end(trackableUUID());
		// If theyre not a player, remove their temp UUID
		if(!(entity instanceof Player))
			entity.getPersistentDataContainer().remove(TEMP_UUID_KEY);
	}

}
