package com.Patane.Brewery.Cooldowns;

import java.util.Date;
import java.util.HashSet;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import com.Patane.Brewery.CustomItems.BrItem;
import com.Patane.runnables.PatTimedRunnable;
import com.Patane.util.general.Chat;
import com.Patane.util.general.Messenger;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;

public abstract class CooldownTracker extends PatTimedRunnable {

	final protected BrItem item;
	final protected Date completeTime;
		
	protected HashSet<Player> showing = new HashSet<Player>();
	
	public CooldownTracker(LivingEntity entity, BrItem item) {
		super(0, 0.05f, item.getCooldown());
		this.item = item;
		this.completeTime = completeTime(item.getCooldown());
		
	}

	public BrItem getItem() {
		return item;
	}
	public Date getCompleteTime() {
		return completeTime;
	}
	
	public abstract boolean addPlayer(Player player);
	
	public abstract boolean removePlayer(Player player);
	
	public boolean hasPlayer(Player player) {
		return showing.contains(player);
	}
	
	public void clearPlayers() {
		for(Player player : showing) {
			removePlayer(player);
		}
	}
	
	protected static String constructBar(float current, float max, int width) {
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
	protected static Date completeTime(float duration) {
		return new Date(System.currentTimeMillis() + ((long)(duration * 1000)));
	}
	protected void updateActionBar(Player player) {
		Messenger.sendRaw(player, ChatMessageType.ACTION_BAR, new TextComponent(Chat.translate(constructBar(ticksLeft(), duration(), 20))));
	}
	protected void clearActionBar(Player player) {
		Messenger.sendRaw(player, ChatMessageType.ACTION_BAR, new TextComponent(""));

	}
}
