package com.Patane.Brewery.CustomEffects;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.LivingEntity;

import com.Patane.Brewery.CustomEffects.Modifier.ModifierInfo;
import com.Patane.util.YML.YMLParsable;
import com.Patane.util.general.GeneralUtil;
import com.Patane.util.general.Messenger;
import com.Patane.util.ingame.LocationsUtil;

public abstract class EffectType extends YMLParsable{

	protected EffectType(){};
	public EffectType(Map<String, String> fields){}
	
	public abstract void execute(BrEffect effect, LivingEntity shooter, Location location);

	protected void applyModifiers(BrEffect effect, Location location, LivingEntity hitEntity, LivingEntity shooter){
		effect.getModifier().modify(new ModifierInfo(hitEntity, shooter, location));
	}
	protected void applyParticles(BrEffect effect, Location location, List<LivingEntity> entitiesHit){
		if(effect.hasParticle()){
			switch(effect.getParticleEffect().formation){
			case POINT:
				effect.getParticleEffect().spawn(location, 0);
				break;
			case RADIUS:
				effect.getParticleEffect().spawn(location, effect.getRadius());
				break;
			case RADIUS_FACE:
				ArrayList<Location> locs = new ArrayList<Location>();
				for(Block block : LocationsUtil.getNonAirBlocks(location, effect.getRadius())){
					// Possibly add all blockfaces (North, south, east, west, down)
					if(block.getRelative(BlockFace.UP).getType() == Material.AIR)
						locs.add(new Location(block.getWorld(), block.getX()+BlockFace.UP.getModX()+GeneralUtil.random(0, 1), block.getY()+BlockFace.UP.getModY(), block.getZ()+BlockFace.UP.getModZ()+GeneralUtil.random(0, 1)));
				}
				for(Location loc : locs)
					effect.getParticleEffect().spawn(loc, 2, (effect.getParticleEffect().intensity*10)/locs.size());
				break;
			case ENTITIES:
				for(LivingEntity entity : entitiesHit)
					effect.getParticleEffect().spawn(entity.getEyeLocation(), 1);
				break;
			default:
				break;
			}
		}
	}
	protected void applySounds(BrEffect effect, Location location){
		if(effect.hasSound())
			effect.getSoundEffect().spawn(location);
	}
	protected List<LivingEntity> executeOnEntities(BrEffect effect, LivingEntity shooter, Location location) {
		List<LivingEntity> hitEntities = LocationsUtil.getEntities(location, effect.getRadius(), effect.getEntitiesArray());
		for(LivingEntity hitEntity : hitEntities){
			hitEntity.addPotionEffects(effect.getPotions());
			applyModifiers(effect, location, hitEntity, shooter);
			Messenger.debug(hitEntity, "&cAffected by &7"+effect.getName()+"&c effect.");
		}
		return hitEntities;
	}
}
