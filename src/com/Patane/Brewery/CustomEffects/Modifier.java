package com.Patane.Brewery.CustomEffects;

import java.util.Map;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.metadata.FixedMetadataValue;

import com.Patane.Brewery.Brewery;
import com.Patane.Brewery.YMLParsable;

public abstract class Modifier extends YMLParsable{
	
	protected Modifier(){};
	public Modifier(Map<String, String> fields){}
	
	public abstract void modify(ModifierInfo info);
	
	public void damage(LivingEntity damagee, Entity damager, double amount){
		damagee.setMetadata("Brewery_DAMAGE", new FixedMetadataValue(Brewery.getInstance(), null));
		damagee.damage(amount, damager);
	}
	public static class ModifierInfo {
		private final LivingEntity target;
		private final Entity targeter;
		private final Location impact;

		public ModifierInfo(LivingEntity target, Entity targeter, Location impact){
			this.target = target;
			this.targeter = targeter;
			this.impact = impact;
		}

		public LivingEntity getTarget() {
			return target;
		}
		public Entity getTargeter() {
			return targeter;
		}
		public Location getImpact() {
			return impact;
		}
	}
}
