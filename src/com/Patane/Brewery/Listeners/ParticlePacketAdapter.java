package com.Patane.Brewery.Listeners;

import org.bukkit.Location;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.BlockPosition;

public class ParticlePacketAdapter extends PacketAdapter{
	public ParticlePacketAdapter(AdapterParameteters params) {
        super(params);
    }
	@Override
    public void onPacketSending(PacketEvent e) {
        if (e.getPacket().getType() == PacketType.Play.Server.WORLD_EVENT) {
            if (e.getPacket().getIntegers().read(0) == 2002){
//                Messenger.send(e.getPlayer(), e.getPacket().getIntegers().getValues().toString());
//                Messenger.send(e.getPlayer(), e.getPacket().getBlockPositionModifier().getValues().toString());
                BlockPosition bp = e.getPacket().getBlockPositionModifier().getValues().get(0);
                Location particleLoc = new Location(e.getPlayer().getWorld(), bp.getX(), bp.getY(), bp.getZ());
                if(GlobalListener.getParticleLoc().contains(particleLoc)){
                	GlobalListener.getParticleLoc().remove(particleLoc);
                	e.setCancelled(true);
                }
            }
        }
    }
}
