package com.Patane.Brewery.util;

import java.util.HashSet;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;

public class Locations {
	public static LivingEntity[] getNearbyEntities(Location l, int radius) {
	    int chunkRadius = radius < 16 ? 1 : (radius - (radius % 16)) / 16;
	    HashSet <LivingEntity> radiusEntities = new HashSet<LivingEntity>();
	 
	    for (int chX = 0 - chunkRadius; chX <= chunkRadius; chX++) {
	        for (int chZ = 0 - chunkRadius; chZ <= chunkRadius; chZ++) {
	            int x = (int) l.getX(), y = (int) l.getY(), z = (int) l.getZ();
	            for (Entity e: new Location(l.getWorld(), x + (chX * 16), y, z + (chZ * 16)).getChunk().getEntities()) {
	                if (e instanceof LivingEntity 
	                		&& e.getLocation().distance(l) <= radius && e.getLocation().getBlock() != l.getBlock())
	                    radiusEntities.add((LivingEntity) e);
	            }
	        }
	    }
	 
	    return radiusEntities.toArray(new LivingEntity[radiusEntities.size()]);
	}
}
