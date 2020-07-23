package com.Patane.Brewery.CustomEffects.modifiers;

import java.util.Map;

import org.bukkit.Location;
import org.bukkit.util.Vector;

import com.Patane.Brewery.Brewery;
import com.Patane.Brewery.CustomEffects.Modifier;
import com.Patane.util.annotations.ClassDescriber;
import com.Patane.util.annotations.ParseField;
import com.Patane.util.general.Check;

@ClassDescriber(
		name="force",
		desc="Force or push a living entity into a direction.")
public class Force extends Modifier{
	@ParseField(desc="Direction to push the living entity from the impact location.")
	private Direction direction;
	@ParseField(desc="Intensity of the push.")
	private double intensity;
	
	public Force() {
		super();
	}
	
	public Force(Map<String, String> fields) {
		super(fields);
	}
	

	@Override
	protected void populateFields(Map<String, String> fields) {
		direction = getEnumValue(Direction.class, fields, "direction");
		intensity = Check.greaterThan(getDouble(fields, "intensity"), 0, "Intensity must be greater than 0.");
	}
	public Force(Direction direction, double intensity) {
		this.direction = direction;
		this.intensity = intensity;
		construct();
	}

	/* 
	 * ================================================================================
	 */
	
	@Override
	public void modify(ModifierInfo info) {
		// We must do the velocity addition 1 tick after this, as the knockback of a weapon can OVERRIDE the current velocity.
		// Therefore, this will add ON TOP of the knockback velocity (plus any other velocities on the entity)
        Brewery.getInstance().getServer().getScheduler().scheduleSyncDelayedTask(Brewery.getInstance(), new Runnable() {
			@Override
			public void run() {
				double speed = direction.getIntensity(info.getTarget().getLocation(), info.getImpact())*(1+intensity/10);
				Vector addingVelocity = direction.getVector(info.getTarget().getLocation(), info.getImpact()).normalize().multiply(speed);
		        info.getTarget().setVelocity(info.getTarget().getVelocity().add(addingVelocity));
			}
        }, 1);
	}

	/* 
	 * ================================================================================
	 */
	
	public enum Direction {
		TOWARDS(new ForceAction() {
			public Vector getVector(Location from, Location to) {
		        return to.toVector().subtract(from.toVector());
			}
			public double getIntensity(Location from, Location to) {
				return from.distance(to)/10;
			}
		}),
		AWAY(new ForceAction() {
			public Vector getVector(Location from, Location to) {
		        return to.toVector().subtract(from.toVector()).multiply(-1);
			}
			public double getIntensity(Location from, Location to) {
				return 1/from.distance(to);
			}
		}),
		UP(new ForceAction() {
			public Vector getVector(Location from, Location to) {
		        return new Vector(0,0.1,0);
			}
			public double getIntensity(Location from, Location to) {
				return 1;
			}
		}),
		DOWN(new ForceAction() {
			public Vector getVector(Location from, Location to) {
		        return new Vector(0,-0.1,0);
			}
			public double getIntensity(Location from, Location to) {
				return 1;
			}
		}),
		RANDOM(new ForceAction() {
			public Vector getVector(Location from, Location to) {
		        return new Vector(0,0,0);
			}
			public double getIntensity(Location from, Location to) {
				return 1;
			}
		});
		
		private final ForceAction action;
		Direction(ForceAction action) {
			this.action = action;
		}
		public Vector getVector(Location from, Location to) {
			return action.getVector(from, to);
		}
		public double getIntensity(Location from, Location to) {
			return action.getIntensity(from, to);
		}
	}
	interface ForceAction{
		public Vector getVector(Location from, Location to);
		public double getIntensity(Location from, Location to);
	}
}
