package com.Patane.Brewery.Cooldowns;

import java.util.ArrayList;
import java.util.UUID;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.Patane.Brewery.CustomItems.BrItem;
import com.Patane.util.general.Chat;
import com.Patane.util.general.Messenger;
import com.Patane.util.ingame.InventoriesUtil;
import com.Patane.util.ingame.ItemEncoder;
import com.Patane.util.metadata.MetaDataUtil;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;

public class UniqueCooldown extends CooldownTracker {
	
	private final UUID itemUUID;
	
	public static final String META_KEY = "unique_cooldown";
	
	public UniqueCooldown(LivingEntity entity, BrItem item, UUID itemUUID) {
		super(entity, item);
		this.itemUUID = itemUUID;
		if(entity instanceof Player)
			addPlayer((Player) entity);
	}

	@Override
	public boolean addPlayer(Player player) {
		if(showing.add(player)) {
			MetaDataUtil.setFixed(player, META_KEY, itemUUID);
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
			// Get the UUID string from the chosen ItemStack.
			String uuidString = ItemEncoder.getString(itemStack, "UUID");
			
			// If the UUID does not exist OR has changed, then we are not focussed on this cooldown item. Therefore, stop showing it to the player.
			if(uuidString == null || !uuidString.equals(itemUUID.toString()))
				removePlayer(player);
			
			// Otherwise, the player IS holding this item, so we show them the cooldown in the action bar!
			else
				Messenger.sendRaw(player, ChatMessageType.ACTION_BAR, new TextComponent(Chat.translate(constructBar(ticksLeft(), duration(), 20))));
		}
	}

	@Override
	public void complete() {
		CooldownHandler.end(itemUUID);
	}

}
