package com.Patane.Brewery.CustomEffects;

import org.bukkit.Sound;
import org.bukkit.potion.PotionEffect;

public class CustomEffect{
	String name;
	
	PotionEffect instantEffect;
//	PotionParticleEffect instantParticleEffect; // MAKE THIS CLASS
	Sound instantSound;
	
	PotionEffect lingeringEffect;
	int interval;
//	PotionParticleEffect lingeringParticleEffect; // MAKE THIS CLASS (shound include specific particle information)
	Sound lingeringSound;
}
