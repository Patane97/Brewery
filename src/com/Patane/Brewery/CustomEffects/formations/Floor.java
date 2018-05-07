package com.Patane.Brewery.CustomEffects.formations;

import java.util.ArrayList;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;

import com.Patane.Brewery.CustomEffects.BrEffect;
import com.Patane.Brewery.CustomEffects.Formation;
import com.Patane.util.YML.Namer;
import com.Patane.util.general.GeneralUtil;
import com.Patane.util.ingame.LocationsUtil;

@Namer(name="FLOOR")
public class Floor extends Formation{
	
	public Floor() {
		super(Focus.BLOCK);
	}

	@Override
	public void form(BrEffect effect, Location location) {
		ArrayList<Location> locs = new ArrayList<Location>();
		for(Block block : LocationsUtil.getNonAirBlocks(location, effect.getRadius())){
			// Possibly add all blockfaces (North, south, east, west, down)
			if(block.getRelative(BlockFace.UP).getType() == Material.AIR)
				locs.add(new Location(block.getWorld(), block.getX()+BlockFace.UP.getModX()+GeneralUtil.random(0, 1), block.getY()+BlockFace.UP.getModY(), block.getZ()+BlockFace.UP.getModZ()+GeneralUtil.random(0, 1)));
		}
		for(Location loc : locs)
			effect.getParticleEffect().spawn(loc, 2, (effect.getParticleEffect().intensity*10)/locs.size());
	}

}
