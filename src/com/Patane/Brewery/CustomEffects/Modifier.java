package com.Patane.Brewery.CustomEffects;

import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

public interface Modifier {
	public abstract void modify(LivingEntity entity1, Entity entity2);
	
	public static class Damage implements Modifier{
		@SuppressWarnings("unused")
		final private DamageCause damageCause;
		final private double damage;
		
		public Damage(DamageCause damageCause, double damage){
			this.damageCause = damageCause;
			this.damage = damage;
		}
		@Override
		public void modify(LivingEntity damagee, Entity damager) {
			if(damage > 0){
				if(damager != null){
					damagee.damage(damage, damager);
				}
				damagee.damage(damage);
			}
		}
	}
	public static class Heal implements Modifier{
		final private double amount;
		
		public Heal(double amount){
			this.amount = amount;
		}
		@Override
		public void modify(LivingEntity healee, Entity nullEntity) {
			if(amount > 0)
				healee.setHealth(Math.min(20, healee.getHealth() + amount));
		}
	}
	public static class Feed implements Modifier{
		final private double amount;
		
		public Feed(double amount){
			this.amount = amount;
		}
		@Override
		public void modify(LivingEntity entity, Entity nullEntity) {
			if(entity instanceof Player){
				Player player = (Player) entity;
				player.setFoodLevel((int) Math.min(20, (player.getFoodLevel() + amount)));
			}
		}
	}
}
