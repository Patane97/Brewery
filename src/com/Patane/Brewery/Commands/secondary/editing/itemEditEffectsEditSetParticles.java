package com.Patane.Brewery.Commands.secondary.editing;

import org.bukkit.Particle;
import org.bukkit.command.CommandSender;

import com.Patane.Brewery.CustomEffects.BrEffect;
import com.Patane.Brewery.CustomEffects.BrEffect.BrParticleEffect;
import com.Patane.Brewery.CustomEffects.Formation;
import com.Patane.Brewery.CustomItems.BrItem;
import com.Patane.Brewery.Editing.EditSession;
import com.Patane.Commands.CommandInfo;
import com.Patane.util.general.Messenger;
import com.Patane.util.general.StringsUtil;
@CommandInfo(
	name = "edit effects edit <effectname> set particles",
	description = "Sets the Particle Effects for an Effect that is attached to a Brewery Item.",
	usage = "/brewery edit effects edit <effect name> set particles [type] [formation] <intensity> <velocity>",
	maxArgs = 4
)
public class itemEditEffectsEditSetParticles extends itemEditEffectsEditSet {

	@Override
	public boolean execute(CommandSender sender, String[] args, Object... objects) {
		
		if(args.length < 1) {
			Messenger.send(sender, "&cPlease provide a Particle Type.");
			return false;
		}
		
		Particle particle = null;
		
		try {
			particle = StringsUtil.constructEnum(args[0], Particle.class);
		} catch (IllegalArgumentException e) {
			Messenger.send(sender, "&7"+args[0]+" &cis not a valid Particle Type.");
			return true;
		}

		if(args.length < 2) {
			Messenger.send(sender, "&cPlease provide a Formation.");
			return false;
		}
		
		Formation formation = null;
		try {
			formation = StringsUtil.constructEnum(args[1], Formation.class);
		} catch (IllegalArgumentException e) {
			Messenger.send(sender, "&7"+args[0]+" &cis not a valid formation.");
			return true;
		}

		if(args.length < 3) {
			Messenger.send(sender, "&cPlease provide an intensity.");
			return false;
		}
		
		Integer intensity = null;
		try {
			intensity = Integer.parseInt(args[2]);
		} catch (NumberFormatException e) {
			Messenger.send(sender, "&7"+args[2]+" &cis an invalid intensity.");
			return true;
		}

		if(args.length < 4) {
			Messenger.send(sender, "&cPlease provide a velocity.");
			return false;
		}

		Double velocity = null;
		try {
			velocity = Double.parseDouble(args[3]);
		} catch (NumberFormatException e) {
			Messenger.send(sender, "&7"+args[3]+" &cis an invalid velocity.");
			return true;
		}
		
		BrParticleEffect particleEffect = new BrParticleEffect(particle, formation, intensity, velocity);
		
		BrEffect brEffect = (BrEffect) objects[0];
		
		String successMsg = "&aAdded &7"+particle.name()+" &aParticle Effect to this effect.";
		if(brEffect.hasParticle())
			successMsg = "&aChanged to &7"+particle.name()+" &aParticle Effect.";
		
		brEffect.setParticleEffect(particleEffect);
		
		BrItem brItem = (BrItem) EditSession.get(sender.getName());
		
		BrItem.YML().save(brItem);
		
		Messenger.send(sender, successMsg);
		return true;
	}
}
