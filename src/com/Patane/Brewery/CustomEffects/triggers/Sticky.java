package com.Patane.Brewery.CustomEffects.triggers;

import java.util.List;
import java.util.Map;

import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;

import com.Patane.Brewery.CustomEffects.BrEffect;
import com.Patane.Brewery.CustomEffects.Trigger;
import com.Patane.Brewery.Handlers.BrMetaDataHandler;
import com.Patane.handlers.MetaDataHandler;
import com.Patane.runnables.PatTimedRunnable;
import com.Patane.util.YML.Namer;
import com.Patane.util.general.Check;
import com.Patane.util.ingame.Focusable.Focus;

@Namer(name="STICKY")
public class Sticky extends Trigger{
	public final float rate;
	public final float duration;
	
	public Sticky(Map<String, String> fields){
		rate = Check.greaterThan((float) getDouble(fields, "rate"), 0, "Rate must be greater than 0.");
		duration = Check.greaterThanEqual((float) getDouble(fields, "duration"), rate, "Duration must be greater than or equal to the rate ("+rate+").");
	}
	public Sticky(float rate, float duration){
		this.rate = rate;
		this.duration = duration;
	}
	@Override
	public void execute(BrEffect effect, Location impact, LivingEntity executor) {
		List<LivingEntity> hitEntities = effect.getFilter().filter(impact, effect.getRadius());
//		// If the effect ignores the executor, then remove them from the list (if they're on there)
//		if(effect.ignoreUser())
//			hitEntities.remove(executor);
		new StickyTask(effect, impact, executor, hitEntities);
	}

	@Override
	public void execute(BrEffect effect, LivingEntity executor, LivingEntity target) {
		// Adding Single Hit entity into a 'hitEntity' array. This is purely to fit the format of a StickyTask.
		List<LivingEntity> hitEntities = (effect.hasRadius() ? effect.getFilter().filter(target.getLocation(), effect.getRadius()) : effect.getFilter().filter(target));
		new StickyTask(effect, executor, target, hitEntities);
	}
	protected class StickyTask extends PatTimedRunnable{
		private final BrEffect effect;
		private final Location impact;
		private final LivingEntity executor;
		private final LivingEntity target;
		private final List<LivingEntity> entities;
		
		public StickyTask(BrEffect effect, Location impact, LivingEntity executor, List<LivingEntity> entities){
			super(0, rate, duration);
			this.effect = effect;
			this.impact = impact;
			this.executor = executor;
			this.entities = entities;
			this.target = null;
			
			// Adds effect metadata to entities hit.
			BrMetaDataHandler.addOrReset(this, entities, MetaDataHandler.id(effect.getName(), "sticky"));
			effect.applyTag(this, entities);
		}
		public StickyTask(BrEffect effect, LivingEntity executor, LivingEntity target, List<LivingEntity> entities){
			super(0, rate, duration);
			this.effect = effect;
			this.impact = null;
			this.executor = executor;
			this.target = target;
			this.entities = entities;
			
			// Adds effect metadata to entities hit.
			BrMetaDataHandler.addOrReset(this, entities, MetaDataHandler.id(effect.getName(), "sticky"));
			effect.applyTag(this, entities);
		}
		@Override
		public void task() {
			Location dynamicImpact = (impact == null ? target.getLocation() : impact);
			applyByFocus(effect, dynamicImpact, Focus.BLOCK);
			executeMany(effect, dynamicImpact, executor, entities);
		}

		@Override
		public void complete() {
			// Removes any effect metadatas leftover from the final task() tick.
			BrMetaDataHandler.remove(this, MetaDataHandler.id(effect.getName(), "sticky"));
			effect.clearTag(this);
		}
	}
}
