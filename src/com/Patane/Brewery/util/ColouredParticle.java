package com.Patane.Brewery.util;

import java.util.List;

import org.bukkit.Location;
import org.bukkit.entity.Player;

public enum ColouredParticle {
  
    MOB_SPELL("MOB_SPELL"), MOB_SPELL_AMBIENT("MOB_SPELL_AMBIENT"), RED_DUST("RED_DUST");
  
    private ColouredParticle(String name) {
        this.name = name;
    }
    String name;
    public void send(Location location, List<Player> players, int r, int g, int b) {
        ParticleEffect.valueOf(name).display(r/255, g / 255, b / 255, 1, 0, location, players);
    }
    public void send(Location location, int Distance, int r, int g, int b) {
        ParticleEffect.valueOf(name).display(r/255, g / 255, b / 255, 1, 0, location, Distance);
    }

}