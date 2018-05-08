package com.Patane.Brewery.CustomEffects;

import java.util.Map;

import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.metadata.FixedMetadataValue;

import com.Patane.Brewery.Brewery;
import com.Patane.util.YML.YMLParsable;

public abstract class Modifier extends YMLParsable{
	
	protected Modifier(){};
	public Modifier(Map<String, String> fields){}
	
	public abstract void modify(ModifierInfo info);
	
	public void damage(LivingEntity damagee, LivingEntity damager, double amount){
		damagee.setMetadata("Brewery_DAMAGE", new FixedMetadataValue(Brewery.getInstance(), null));
		damagee.damage(amount, damager);
	}
	public static class ModifierInfo {
		private final Location impact;
		private final LivingEntity targeter;
		private final LivingEntity target;

		public ModifierInfo(Location impact, LivingEntity targeter, LivingEntity target){
			this.impact = impact;
			this.targeter = targeter;
			this.target = target;
		}

		public Location getImpact() {
			return impact;
		}
		
		public LivingEntity getTargeter() {
			return targeter;
		}
		
		public LivingEntity getTarget() {
			return target;
		}
	}
}
