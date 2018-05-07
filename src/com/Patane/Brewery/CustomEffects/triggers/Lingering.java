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

@Namer(name="LINGERING")
public class Lingering extends Trigger{
	public final float rate;
	public final float duration;
	
	public Lingering(Map<String, String> fields){
		rate = Check.greaterThan((float) getDouble(fields, "rate"), 0, "Rate must be greater than 0.");
		duration = Check.greaterThanEqual((float) getDouble(fields, "duration"), rate, "Duration must be greater than or equal to the rate ("+rate+").");
	}
	public Lingering(float rate, float duration){
		this.rate = rate;
		this.duration = duration;
	}
	
	@Override
	public void execute(BrEffect effect, Location impact, LivingEntity executor) {
		new LingeringTask(effect, impact, executor);
	}

	protected class LingeringTask extends PatTimedRunnable{
		private final BrEffect effect;
		private final Location impact;
		private final LivingEntity executor;
		
		public LingeringTask(BrEffect effect, Location impact, LivingEntity executor){
			super(0, rate, duration);
			this.effect = effect;
			this.impact = impact;
			this.executor = executor;
		}

		@Override
		public void task() {
			// Applies Visual/Auditory effects via a focus point.
			applyByFocus(effect, impact, Focus.BLOCK);

			// Executes tasks on hit LivingEntities and adds the appropriate metadata to each.
			List<LivingEntity> hit = executeOnEntities(effect, impact, executor);
			BrMetaDataHandler.addClean(this, hit, MetaDataHandler.id(effect.getName(), "lingering"), null);
			effect.applyTag(this, hit);
		}

		@Override
		public void complete() {
			// Removes any effect metadatas leftover from the final task() tick.
			BrMetaDataHandler.remove(this, MetaDataHandler.id(effect.getName(), "lingering"));
			effect.clearTag(this);
		}
	}
}
