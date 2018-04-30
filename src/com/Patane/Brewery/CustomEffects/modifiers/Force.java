package com.Patane.Brewery.CustomEffects.modifiers;

import java.util.Map;

import org.bukkit.Location;
import org.bukkit.util.Vector;

import com.Patane.Brewery.CustomEffects.Modifier;
import com.Patane.util.YML.Namer;

@Namer(name="FORCE")
public class Force extends Modifier{
	final public Direction direction;
	final public double intensity;

	public Force(Map<String, String> fields){
		direction = getEnumValue(Direction.class, fields, "direction");
		intensity = getDouble(fields, "intensity");
	}
	public Force(Direction direction, double intensity){
		this.direction = direction;
		this.intensity = intensity;
	}
	@Override
	public void modify(ModifierInfo info) {
		double speed = direction.getIntensity(info.getTarget().getLocation(), info.getImpact())*(1+intensity/10);
        Vector velocity = direction.getVector(info.getTarget().getLocation(), info.getImpact()).normalize().multiply(speed);
        info.getTarget().setVelocity(velocity);
	}
	
	public enum Direction {
		TOWARDS(new ForceAction(){
			public Vector getVector(Location from, Location to){
		        return to.toVector().subtract(from.toVector());
			}
			public double getIntensity(Location from, Location to) {
				return from.distance(to)/10;
			}
		}),
		AWAY(new ForceAction(){
			public Vector getVector(Location from, Location to){
		        return to.toVector().subtract(from.toVector()).multiply(-1);
			}
			public double getIntensity(Location from, Location to) {
				return 1/from.distance(to);
			}
		}),
		UP(new ForceAction(){
			public Vector getVector(Location from, Location to){
		        return new Vector(0,0.1,0);
			}
			public double getIntensity(Location from, Location to) {
				return 1;
			}
		}),
		DOWN(new ForceAction(){
			public Vector getVector(Location from, Location to){
		        return new Vector(0,-0.1,0);
			}
			public double getIntensity(Location from, Location to) {
				return 1;
			}
		}),
		RANDOM(new ForceAction(){
			public Vector getVector(Location from, Location to){
		        return new Vector(0,0,0);
			}
			public double getIntensity(Location from, Location to) {
				return 1;
			}
		});
		
		private final ForceAction action;
		Direction(ForceAction action){
			this.action = action;
		}
		public Vector getVector(Location from, Location to){
			return action.getVector(from, to);
		}
		public double getIntensity(Location from, Location to){
			return action.getIntensity(from, to);
		}
	}
	interface ForceAction{
		public Vector getVector(Location from, Location to);
		public double getIntensity(Location from, Location to);
	}
}
