package com.Patane.Brewery.CustomEffects;

import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.potion.PotionEffect;

import com.Patane.Brewery.Brewery;
import com.Patane.Brewery.Messenger;
import com.Patane.Brewery.Messenger.ChatType;

public class LingeringEffect extends CustomEffect{
	private final float duration;
	private final float rate;
	
	public LingeringEffect(String name, DamageContainer damageContainer, int radius, float duration, float rate, PotionEffect... potionEffects) {
		super(name, damageContainer, radius, potionEffects);
		this.duration = duration;
		this.rate = rate;
	}
	@Override
	public void execute(LivingEntity shooter, Location location) {
		new LingeringTask(shooter, location);
	}
	protected class LingeringTask implements Runnable{
		private final int scheduleID;
		private long newRate = ((long) (rate*20));
		private float roughTicksLeft = duration*20;
		private final LivingEntity shooter;
		private final Location location;
		
		public LingeringTask(LivingEntity shooter, Location location){
			scheduleID = Brewery.getInstance().getServer().getScheduler().scheduleSyncRepeatingTask(Brewery.getInstance(), this, 0, newRate);
			this.shooter = shooter;
			this.location = location;
			Messenger.debug(ChatType.BROADCAST, "nR: "+newRate);
		}
		
		@Override
		public void run() {
			executeOnEntities(shooter, location, EntityType[] hitableEntities);
			roughTicksLeft -= newRate;
			if(roughTicksLeft <= 0)
				Brewery.getInstance().getServer().getScheduler().cancelTask(scheduleID);
		}
		
	}
}
