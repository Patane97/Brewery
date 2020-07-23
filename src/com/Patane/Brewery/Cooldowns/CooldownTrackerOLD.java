package com.Patane.Brewery.Cooldowns;

import java.util.ArrayList;
import java.util.Date;
import java.util.UUID;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.Patane.Brewery.CustomItems.BrItem;
import com.Patane.Brewery.Handlers.BrMetaDataHandler;
import com.Patane.runnables.PatTimedRunnable;
import com.Patane.util.general.Chat;
import com.Patane.util.general.Messenger;
import com.Patane.util.ingame.InventoriesUtil;
import com.Patane.util.ingame.ItemEncoder;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;

@Deprecated
public class CooldownTrackerOLD extends PatTimedRunnable {
	final private Date completeTime;
	final private UUID uuid;
//	final private BrItem item;
	private ArrayList<Player> showing = new ArrayList<Player>();
	public CooldownTrackerOLD(LivingEntity entity, UUID uuid, BrItem item) {
		super(0, 0.05f, item.getCooldown());
		this.uuid = uuid;
//		this.item = item;
		this.completeTime = completeTime(item.getCooldown());
		if(entity instanceof Player)
			addPlayer((Player) entity);
	}
	public Date getCompleteTime() {
		return completeTime;
	}
	public void addPlayer(Player player) {
		if(showing.contains(player))
			return;
//		Messenger.debug("Adding "+player.getDisplayName()+" to cooldown (UUID="+uuid.toString()+")");
//		Messenger.sendRaw(player, ChatMessageType.ACTION_BAR, new TextComponent(Chat.translate(constructBar(ticksLeft(), duration(), 20))));
		showing.add(player);
		BrMetaDataHandler.add(player, "showing_cooldown", uuid);
	}
	public boolean hasPlayer(Player player) {
		return showing.contains(player);
	}
	public void removePlayer(Player player) {
		if(!showing.contains(player))
			return;
//		Messenger.debug("Removing "+player.getDisplayName()+" from cooldown (UUID="+uuid.toString()+")");
		Messenger.sendRaw(player, ChatMessageType.ACTION_BAR, new TextComponent(""));
		showing.remove(player);
		BrMetaDataHandler.remove("showing_cooldown");
	}
	@Override
	public void task() {
		for(Player player : new ArrayList<Player>(showing)) {
			ItemStack item = player.getInventory().getItemInMainHand();
			// If its a bow, actually show the cooldown of the arrow.
			// *** Should maybe check if the BOW ITSELF has a cooldown first?
			if(InventoriesUtil.isBowMaterial(item.getType())) {
				ItemStack arrow = InventoriesUtil.getTargettedArrowStack(player);
				item = (arrow == null ? item : arrow);
			}
			String uuidString = ItemEncoder.getString(item, "UUID");
			if(uuidString == null || !uuidString.equals(uuid.toString())) {
				removePlayer(player);
				return;
			}
			Messenger.sendRaw(player, ChatMessageType.ACTION_BAR, new TextComponent(Chat.translate(constructBar(ticksLeft(), duration(), 20))));
		}
	}
	@Override
	public void complete() {
		CooldownHandler.end(uuid);
		for(Player player : new ArrayList<Player>(showing))
			removePlayer(player);
	}
	private static String constructBar(float current, float max, int width) {
		current = (width/max)*current;
		max = width;
		int blockCount = (Math.round(current));
		int remainder = width-blockCount;
		// Colour of remaining time
		String bar = "&f";
		for(int i=0 ; i < blockCount ; i++)
			bar = bar+"\u2588";
		// Background colour
		bar = bar+"&0";
		for(int i=0 ; i < remainder ; i++)
			bar = bar+"\u2588";
		return bar;
	}
	private static Date completeTime(float duration) {
		return new Date(System.currentTimeMillis() + ((long)(duration * 1000)));
	}
}