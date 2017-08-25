package com.Patane.Brewery.CustomEffects.types;

import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;

import com.Patane.Brewery.Brewery;
import com.Patane.Brewery.Messenger;
import com.Patane.Brewery.CustomEffects.BrEffect;
import com.Patane.Brewery.Messenger.ChatType;

@EffectTypeInfo(
	name="LINGERING"
)
public class Lingering extends EffectType{
	private final float duration;
	private final float rate;

	public Lingering(float duration, float rate){
		this.duration = duration;
		this.rate = rate;
	}
	
	@Override
	public void execute(BrEffect effect, LivingEntity shooter, Location location, EntityType[] hitableEntities) {
		new LingeringTask(effect, shooter, location, hitableEntities);
	}
	protected class LingeringTask implements Runnable{
		private final int scheduleID;
		private long newRate = ((long) (rate*20));
		private float roughTicksLeft = duration*20;
		private final BrEffect effect;
		private final LivingEntity shooter;
		private final Location location;
		private final EntityType[] hitableEntities;
		
		public LingeringTask(BrEffect effect, LivingEntity shooter, Location location, EntityType[] hitableEntities){
			scheduleID = Brewery.getInstance().getServer().getScheduler().scheduleSyncRepeatingTask(Brewery.getInstance(), this, 0, newRate);
			this.effect = effect;
			this.shooter = shooter;
			this.location = location;
			this.hitableEntities = hitableEntities;
			Messenger.debug(ChatType.BROADCAST, "Lingering Info[Duration(ticks): "+roughTicksLeft+", Rate(ticks): "+newRate+"]");
		}
		
		@Override
		public void run() {
			executeOnEntities(effect, shooter, location, hitableEntities);
			roughTicksLeft -= newRate;
			if(roughTicksLeft <= 0)
				Brewery.getInstance().getServer().getScheduler().cancelTask(scheduleID);
		}
		
	}
}
