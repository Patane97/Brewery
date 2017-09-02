package com.Patane.Brewery.CustomEffects.modifiers;

import java.util.Map;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.util.Vector;

import com.Patane.Brewery.Namer;
import com.Patane.Brewery.CustomEffects.Modifier;

@Namer(name="FORCE")
public class Force extends Modifier{
	public Direction direction;
	public double intensity;

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
		moveToward(info.getTarget(), info.getImpact(), intensity);
	}
	private void moveToward(Entity entity, Location to, double speed){
        Location loc = entity.getLocation();
        double x = loc.getX() - to.getX();
        double y = loc.getY() - to.getY();
        double z = loc.getZ() - to.getZ();
        Vector velocity = new Vector(x, y, z).normalize().multiply(-speed);
        entity.setVelocity(velocity);   
    }
	private enum Direction {
		IN(), OUT(), RANDOM();
	}
}
