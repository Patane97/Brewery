package com.Patane.Brewery.CustomEffects;

import java.util.ArrayList;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;

import com.Patane.util.general.GeneralUtil;
import com.Patane.util.general.Messenger;
import com.Patane.util.general.Messenger.Msg;
import com.Patane.util.ingame.Focus;
import com.Patane.util.ingame.LocationsUtil;

public enum Formation {
	RADIUS(Focus.BLOCK, (effect, location) -> {
		if(!effect.hasRadius()) {
			Messenger.send(Msg.WARNING, "The 'Radius' formation requires '"+effect.getName()+"' effect to have a radius.");
			return;
		}
		effect.getParticleEffect().spawn(location, effect.getRadius());
	}, "Forms a flat radius around the target location."),
	
	FACE_UP(Focus.BLOCK, (effect, location) -> {
		if(!effect.hasRadius()) {
			Messenger.send(Msg.WARNING, "The 'Face' formation requires '"+effect.getName()+"' effect to have a radius.");
			return;
		}
		ArrayList<Location> locs = new ArrayList<Location>();
		// Looping through all non-air blocks at location within effects radius
		for(Block block : LocationsUtil.getNonAirBlocks(location, effect.getRadius())) {
			// Possibly add all blockfaces (North, south, east, west, down)
			if(block.getRelative(BlockFace.UP).getType() == Material.AIR)
				locs.add(new Location(block.getWorld(), block.getX()+BlockFace.UP.getModX()+GeneralUtil.random(0, 1), block.getY()+BlockFace.UP.getModY(), block.getZ()+BlockFace.UP.getModZ()+GeneralUtil.random(0, 1)));
		}
		for(Location loc : locs)
			effect.getParticleEffect().spawn(loc, 0.5f, effect.getParticleEffect().getIntensity());
	}, "Forms on the upward-face of every block within the radius from the target location."),
	
	POINT(Focus.BLOCK, (effect, location) -> {
		effect.getParticleEffect().spawn(location, 0f);
	}, "Forms on the point of the target location."),
	
	ENTITY(Focus.ENTITY, (effect, location) -> {
		// Location will always be 'entity.getEyeLocation()' as Focus system does not allow a non-entity location to be passed.
		effect.getParticleEffect().spawn(location, 1f);
	}, "Forms on each living entity hit.");
	
	private Focus focus;
	private Form form;
	private String desc;
	
	Formation(Focus focus, Form form, String desc) {
		this.focus = focus;
		this.form = form;
		this.desc = desc;
	}
	
	public Focus getFocus() {
		return focus;
	}
	
	public void form(BrEffect effect, Location location) {
		form.execute(effect, location);
	}
	
	public String getDesc() {
		return desc;
	}
	
	protected static interface Form {
		public void execute(BrEffect effect, Location location);
	}
}
