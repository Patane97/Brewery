package com.Patane.Brewery.CustomEffects.types;

import java.util.List;
import java.util.Map;

import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;

import com.Patane.Brewery.Brewery;
import com.Patane.Brewery.CustomEffects.BrEffect;
import com.Patane.Brewery.CustomEffects.EffectType;
import com.Patane.util.YML.Namer;
import com.Patane.util.general.PatRunnable;

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
	public void execute(BrEffect effect, LivingEntity shooter, Location location) {
		new LingeringTask(effect, shooter, location);
	}
	protected class LingeringTask extends PatRunnable{
		private long newRate = ((long) (rate*20));
		private float roughTicksLeft = duration*20;
		private final BrEffect effect;
		private final LivingEntity shooter;
		private final Location location;
		
		public LingeringTask(BrEffect effect, LivingEntity shooter, Location location){
			super(Brewery.getInstance(), 0, (long) (rate*20));
			this.effect = effect;
			this.shooter = shooter;
			this.location = location;
		}
		
		@Override
		public void run() {
			List<LivingEntity> entitiesHit = executeOnEntities(effect, shooter, location);
			applyParticles(effect, location, entitiesHit);
			applySounds(effect, location);
			roughTicksLeft -= newRate;
			if(roughTicksLeft <= 0)
				this.cancel();
		}
	}
}
