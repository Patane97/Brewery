package com.Patane.Brewery.CustomEffects.triggers;

import java.util.List;
import java.util.Map;

import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;

import com.Patane.Brewery.CustomEffects.BrEffect;
import com.Patane.Brewery.CustomEffects.Trigger;
import com.Patane.Brewery.Handlers.BrMetaDataHandler;
import com.Patane.runnables.PatTimedRunnable;
import com.Patane.util.annotations.ClassDescriber;
import com.Patane.util.annotations.ParseField;
import com.Patane.util.general.Check;
import com.Patane.util.ingame.Focus;

@ClassDescriber(
		name="lingering",
		desc="Applies the Modifier at a fixed rate over a duration. Calculates which living entities are hit at each rate.")
public class Lingering extends Trigger{
	@ParseField(desc="Rate at which to apply the Modifier. Measured in seconds.")
	public float rate;
	@ParseField(desc="Duration for the effect to linger. This must be greater than or equal to the rate and is measured in seconds.")
	public float duration;
	
	public Lingering() {
		super();
	}
	
	public Lingering(Map<String, String> fields) {
		super(fields);
	}

	@Override
	protected void populateFields(Map<String, String> fields) {
		rate = Check.greaterThan((float) getDouble(fields, "rate"), 0, "Rate must be greater than 0.");
		duration = Check.greaterThanEqual((float) getDouble(fields, "duration"), rate, "Duration must be greater than or equal to the rate ("+rate+").");
	}
	public Lingering(float rate, float duration) {
		this.rate = rate;
		this.duration = duration;
		construct();
	}

	/* 
	 * ================================================================================
	 */

	@Override
	protected void valueConverts() {
		// converted from MC ticks to seconds (20 ticks = 1 second)
		customValueConverter.put("rate", i -> (float)i+"s");
		customValueConverter.put("duration", i -> (float)i+"s");
	}

	/* 
	 * ================================================================================
	 */
	
	@Override
	public void execute(BrEffect effect, Location impact, LivingEntity executor) {
		new LingeringTask(effect, impact, executor);
	}
	@Override
	public void execute(BrEffect effect, LivingEntity executor, LivingEntity target) {
		new LingeringTask(effect, executor, target);
	}

	/* 
	 * ================================================================================
	 */

	protected class LingeringTask extends PatTimedRunnable{
		private final BrEffect effect;
		private final Location impact;
		private final LivingEntity executor;
		private final LivingEntity target;
		
		public LingeringTask(BrEffect effect, Location impact, LivingEntity executor) {
			super(0, rate, duration);
			this.effect = effect;
			this.impact = impact;
			this.executor = executor;
			this.target = null;
		}
		public LingeringTask(BrEffect effect, LivingEntity executor, LivingEntity target) {
			super(0, rate, duration);
			this.effect = effect;
			this.impact = null;
			this.executor = executor;
			this.target = target;
		}

		@Override
		public void task() {
			// A dynamicImpact location is used to simulate the effect either 
			// hitting and sticking to a spot on the ground OR sticking to an entity and moving the effect along to wherever they are.
			Location dynamicImpact = (impact == null ? target.getLocation() : impact);
			// Applies Visual/Auditory effects via a focus point.
			applyByFocus(effect, dynamicImpact, Focus.BLOCK);

			// Executes tasks on hit LivingEntities and adds the appropriate metadata to each.
			List<LivingEntity> hit = (impact == null ? executeMany(effect, executor, target) : executeMany(effect, impact, executor));
			BrMetaDataHandler.addClean(this, hit, effect.getName(), "lingering");
			effect.applyTag(this, hit);
		}

		@Override
		public void complete() {
			// Removes any effect metadatas leftover from the final task() tick.
			BrMetaDataHandler.remove(this, effect.getName());
			effect.clearTag(this);
		}
	}
}
