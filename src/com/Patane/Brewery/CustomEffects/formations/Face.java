package com.Patane.Brewery.CustomEffects.formations;

import java.util.ArrayList;
import java.util.Map;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;

import com.Patane.Brewery.CustomEffects.BrEffect;
import com.Patane.Brewery.CustomEffects.Formation;
import com.Patane.util.YAML.Namer;
import com.Patane.util.general.GeneralUtil;
import com.Patane.util.general.Messenger;
import com.Patane.util.general.Messenger.Msg;
import com.Patane.util.ingame.LocationsUtil;

@Namer(name="FACE")
public class Face extends Formation{
	
	public Face() {
		super(Focus.BLOCK);
	}
	
	public Face(Map<String, String> fields) {
		super(fields);
	}

	@Override
	public void form(BrEffect effect, Location location) {
		if(!effect.hasRadius()) {
			Messenger.send(Msg.WARNING, "'"+className()+"' Formation needs a radius to be formed.");
			return;
		}
		ArrayList<Location> locs = new ArrayList<Location>();
		for(Block block : LocationsUtil.getNonAirBlocks(location, effect.getRadius())){
			// Possibly add all blockfaces (North, south, east, west, down)
			if(block.getRelative(BlockFace.UP).getType() == Material.AIR)
				locs.add(new Location(block.getWorld(), block.getX()+BlockFace.UP.getModX()+GeneralUtil.random(0, 1), block.getY()+BlockFace.UP.getModY(), block.getZ()+BlockFace.UP.getModZ()+GeneralUtil.random(0, 1)));
		}
		for(Location loc : locs)
			effect.getParticleEffect().spawn(loc, 0.5f, effect.getParticleEffect().intensity);
	}

}
