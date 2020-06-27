package com.Patane.Brewery.Listeners;

import com.Patane.Brewery.Brewery;
import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;

public class GenericPacketAdapter extends PacketAdapter{
	public GenericPacketAdapter(ListenerPriority priority, PacketType type) {
        super(Brewery.getInstance(), priority, type);
    }
//	@Override
//    public void onPacketSending(PacketEvent e) {
//        if (e.getPacket().getType() == PacketType.Play.Server.WORLD_EVENT) {
//            if (e.getPacket().getIntegers().read(0) == 2002) {
//                Messenger.send(e.getPlayer(), e.getPacket().getIntegers().getValues().toString());
//                Messenger.send(e.getPlayer(), e.getPacket().getBlockPositionModifier().getValues().toString());
//                BlockPosition bp = e.getPacket().getBlockPositionModifier().getValues().get(0);
//                Location particleLoc = new Location(e.getPlayer().getWorld(), bp.getX(), bp.getY(), bp.getZ());
//                if(GlobalListener.getParticleLoc().contains(particleLoc)) {
//                	GlobalListener.getParticleLoc().remove(particleLoc);
//                	e.setCancelled(true);
//                }
//            }
//        }
//    }
//	@Override
	public void onPacketReceiving(PacketEvent e) {
//		PacketContainer packet = e.getPacket();
//		Player player = e.getPlayer();
//		PataneUtil.getInstance().getServer().getScheduler().scheduleSyncDelayedTask(Brewery.getInstance(), new Runnable() {
//			@Override
//			public void run() {
//				int slot = packet.getIntegers().getValues().get(0);
//				ItemStack item = player.getInventory().getItem(slot);
//				if(item != null && item.getType() != Material.AIR)
//					Messenger.debug("Ping.");
//			}
//		});
	}
}
