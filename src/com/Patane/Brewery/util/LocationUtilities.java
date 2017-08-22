package com.Patane.Brewery.util;

import java.util.ArrayList;
import java.util.Arrays;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;

public class LocationUtilities {
	public static ArrayList<LivingEntity> getEntities(Location location, double radius, EntityType[] hitableEntities){
//	    int chunkRadius = radius < 16 ? 1 : (radius - (radius % 16)) / 16;
		ArrayList<EntityType> entityTypes = new ArrayList<EntityType>(Arrays.asList(hitableEntities));
	    ArrayList<LivingEntity> radiusEntities = new ArrayList<LivingEntity>();
	    
	    for(Entity entity : location.getWorld().getNearbyEntities(location, radius, radius, radius)){
	    	if(entity instanceof LivingEntity && entityTypes.contains(entity.getType()))
	    		radiusEntities.add((LivingEntity) entity);
	    }
//	    for (int chX = 0 - chunkRadius; chX <= chunkRadius; chX++) {
//	        for (int chZ = 0 - chunkRadius; chZ <= chunkRadius; chZ++) {
//	            int x = (int) l.getX(), y = (int) l.getY(), z = (int) l.getZ();
//	            for (Entity e: new Location(l.getWorld(), x + (chX * 16), y, z + (chZ * 16)).getChunk().getEntities()) {
//	                if (e.getClass().isInstance(clazz)
//	                		&& e.getLocation().distance(l) <= radius && e.getLocation().getBlock() != l.getBlock())
//	                    radiusEntities.add((T) e);
//	            }
//	        }
//	    }
	    return radiusEntities;
	}
}
