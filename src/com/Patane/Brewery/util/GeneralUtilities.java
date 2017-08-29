package com.Patane.Brewery.util;

import org.bukkit.entity.Entity;
import org.bukkit.metadata.FixedMetadataValue;

import com.Patane.Brewery.Brewery;

public class GeneralUtilities {
	public static void timedMetadata(Entity entity, String metaName, double time){
		entity.setMetadata(metaName, new FixedMetadataValue(Brewery.getInstance(), null));
		Brewery.getInstance().getServer().getScheduler().scheduleSyncDelayedTask(Brewery.getInstance(), new Runnable(){
			@Override
			public void run(){
				entity.removeMetadata(metaName, Brewery.getInstance());
			}
		}, Math.round(time*20));
	}
}
