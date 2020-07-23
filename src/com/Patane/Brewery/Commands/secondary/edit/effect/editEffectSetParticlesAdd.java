package com.Patane.Brewery.Commands.secondary.edit.effect;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Particle;
import org.bukkit.command.CommandSender;

import com.Patane.Brewery.CustomEffects.BrEffect;
import com.Patane.Commands.CommandInfo;
import com.Patane.util.YAML.MapParsable;
import com.Patane.util.formables.ParticleHandler;
import com.Patane.util.formables.SpecialParticle;
import com.Patane.util.formables.Particles.STANDARD;
import com.Patane.util.general.Chat;
import com.Patane.util.general.Messenger;
import com.Patane.util.general.StringsUtil;
import com.Patane.util.ingame.Commands;

import net.md_5.bungee.api.chat.TextComponent;
@CommandInfo(
	name = "edit effect set particles add",
	description = "Adds a Particle Effect to an original Effect.",
	usage = "/brewery edit effect <effect name> set particles add [particle] <values...>"
)
public class editEffectSetParticlesAdd extends editEffectSetParticles {

	@Override
	public boolean execute(CommandSender sender, String[] args, Object... objects) {
		
		// Checking particle type is given
		if(args.length < 1) {
			Messenger.send(sender, "&ePlease specify a minecraft particle.");
			return true;
		}

		// Checking/Saving particle type
		Particle particle = null;
		try {
			particle = StringsUtil.constructEnum(args[0], Particle.class);
		} catch (IllegalArgumentException e) {
			Messenger.send(sender, String.format("&7%s &cis not a valid minecraft particle.", args[0]));
			return true;
		}
		
		Class<? extends SpecialParticle> specialParticleClass = ParticleHandler.get(particle.toString());
		
		SpecialParticle particleEffect = null;
		
		
		try {
			particleEffect = MapParsable.create(specialParticleClass, Commands.grabArgs(args, 1, args.length));
			if(particleEffect instanceof STANDARD)
				((STANDARD) particleEffect).setParticle(particle);
		}
		
		catch(IllegalArgumentException|NullPointerException e) {
			Messenger.send(sender, e.getMessage());
			return true;
		}
		// This will catch if theres any other error with generating this mapParsable
		catch(InvocationTargetException e) {
			Messenger.printStackTrace(e);
			Messenger.send(sender, "&cParticle effect could not be added due to an uncommon error. Please check server console for error trace.");
			return true;
		}

		// Grabbing Effect
		BrEffect effect = (BrEffect) objects[0];
		
		String successMsg = String.format("&aAdded new Particle Effect to &7%s&a. Hover to view the details!", effect.getName());
		
		String successHoverText = generateEditingTitle(effect)
								+ "&2Particle Effects:\n"
								+ (effect.hasParticles() ? StringsUtil.manyToChatString(1, 0, true, null, null, effect.getParticles().toArray(new SpecialParticle[0]))+"\n" : "");
		
		for(SpecialParticle currentParticleEffect : effect.getParticles()) {
			if(currentParticleEffect.getParticle() == particleEffect.getParticle() && currentParticleEffect.equalFieldMap(particleEffect)) {
				Messenger.send(sender, StringsUtil.hoverText(String.format("&7%s&e already contains a particle effect with those values. Hover to view all particle effects on this effect!", effect.getName())
						, successHoverText));
				return true;
			}
		}
		
		// Add the particle effect to the end of the particle effect list
		successHoverText += String.format("%s", Chat.add(particleEffect.toChatString(1, true), ChatColor.BOLD));
		
		// Save the new specialParticle to the effect
		effect.addParticle(particleEffect);

		// Save the Effect to YML
		BrEffect.YML().save(effect);
		
		// Updates all items that contain references to this effect. Doing this updates any relevant changes to the items effect.
		effect.updateReferences();
		
		// Allows the user to view the details on hover
		TextComponent successMsgComponent = StringsUtil.hoverText(successMsg, successHoverText);
		
		Messenger.send(sender, successMsgComponent);
		return true;
	}
	
	@Override
	public List<String> tabComplete(CommandSender sender, String[] args, Object... objects) {
		// If its the first argument (args.length == 1), send all Particles
		if(args.length < 2)
			return Arrays.asList(StringsUtil.enumValueStrings(Particle.class));
				
		Class<? extends SpecialParticle> specialParticleClass = ParticleHandler.get(args[0]);

		if(specialParticleClass == null)
			return Arrays.asList();
		
		return MapParsable.getSuggestion(specialParticleClass, Commands.grabArgs(args, 1, args.length));
	}
}
