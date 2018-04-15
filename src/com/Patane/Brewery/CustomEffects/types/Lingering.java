package com.Patane.Brewery.CustomEffects.types;

import java.util.Map;

import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;

import com.Patane.Brewery.Namer;
import com.Patane.Brewery.CustomEffects.EffectType;
import com.Patane.Brewery.CustomItems.BrItem.EffectContainer;
import com.Patane.Brewery.util.BrRunnable;

@Namer(name="LINGERING")
public class Lingering extends EffectType{
	public final float duration;
	public final float rate;
	
	public Lingering(Map<String, String> fields){
		duration = (float) getDouble(fields, "duration");
		rate = (float) getDouble(fields, "rate");
	}
	public Lingering(float duration, float rate){
		this.duration = duration;
		this.rate = rate;
	}
	
	@Override
	public void execute(EffectContainer container, LivingEntity shooter, Location location) {
		new LingeringTask(container, shooter, location);
	}
	protected class LingeringTask extends BrRunnable{
		private long newRate = ((long) (rate*20));
		private float roughTicksLeft = duration*20;
		private final EffectContainer container;
		private final LivingEntity shooter;
		private final Location location;
		
		public LingeringTask(EffectContainer container, LivingEntity shooter, Location location){
			super(0, (long) (rate*20));
			this.container = container;
			this.shooter = shooter;
			this.location = location;
		}
		
		@Override
		public void run() {
			applyParticles(container, location);
			applySounds(container, location);
			executeOnEntities(container, shooter, location);
			roughTicksLeft -= newRate;
			if(roughTicksLeft <= 0)
				this.cancel();
		}
	}
}
