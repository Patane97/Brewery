package com.Patane.Brewery.Cooldowns;

import java.util.ArrayList;
import java.util.Date;
import java.util.UUID;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import com.Patane.Brewery.CustomItems.BrItem;
import com.Patane.Brewery.Handlers.BrMetaDataHandler;
import com.Patane.runnables.PatTimedRunnable;
import com.Patane.util.general.Chat;
import com.Patane.util.general.Messenger;
import com.Patane.util.general.Messenger.Msg;
import com.Patane.util.ingame.ItemEncoder;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;

public class CooldownTracker extends PatTimedRunnable {
	final private Date date;
	final private UUID uuid;
	private ArrayList<Player> showing = new ArrayList<Player>();
	public CooldownTracker(LivingEntity entity, UUID uuid, BrItem item) {
		super(0, 0.05f, item.getCooldownDuration());
		this.uuid = uuid;
		this.date = completeTime(item.getCooldownDuration());
		if(entity instanceof Player)
			addPlayer((Player) entity);
	}
	public Date getDate() {
		return date;
	}
	public void addPlayer(Player player) {
		if(showing.contains(player))
			return;
		Messenger.debug(Msg.INFO, "Adding "+player.getDisplayName()+" to cooldown (UUID="+uuid.toString()+")");
		Messenger.sendRaw(player, ChatMessageType.ACTION_BAR, new TextComponent(Chat.translate(constructBar(ticksLeft(), duration(), 20))));
		showing.add(player);
		BrMetaDataHandler.add(player, "showing_cooldown", uuid);
	}
	public void removePlayer(Player player) {
		if(!showing.contains(player))
			return;
		Messenger.debug(Msg.INFO, "Removing "+player.getDisplayName()+" from cooldown (UUID="+uuid.toString()+")");
		Messenger.sendRaw(player, ChatMessageType.ACTION_BAR, new TextComponent(""));
		showing.remove(player);
		BrMetaDataHandler.remove("showing_cooldown");
	}
	@Override
	public void task() {
		for(Player player : new ArrayList<Player>(showing)) {
			String uuidString = ItemEncoder.extractTag(player.getInventory().getItemInMainHand(), "UUID");
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
			bar = bar+"█";
		// Background colour
		bar = bar+"&0";
		for(int i=0 ; i < remainder ; i++)
			bar = bar+"█";
		return bar;
	}
	private static Date completeTime(float duration) {
		return new Date(System.currentTimeMillis() + ((long)(duration * 1000)));
	}
}