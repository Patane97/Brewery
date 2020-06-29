package com.Patane.Brewery.Commands.secondary.edit.effect;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.List;

import org.bukkit.Particle;
import org.bukkit.command.CommandSender;

import com.Patane.Brewery.CustomEffects.BrEffect;
import com.Patane.Brewery.Handlers.ModifierHandler;
import com.Patane.Commands.CommandInfo;
import com.Patane.util.YAML.MapParsable;
import com.Patane.util.formables.Formation;
import com.Patane.util.formables.ParticleHandler;
import com.Patane.util.formables.SpecialParticle;
import com.Patane.util.formables.Particles.OTHER;
import com.Patane.util.general.GeneralUtil;
import com.Patane.util.general.Messenger;
import com.Patane.util.general.StringsUtil;
import com.Patane.util.ingame.Commands;
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
		// Find Effect
		BrEffect effect = (BrEffect) objects[0];

		// Checking/Saving particle type
		Particle particle = null;
		try {
			particle = StringsUtil.constructEnum(args[0], Particle.class);
		} catch (IllegalArgumentException e) {
			Messenger.send(sender, String.format("&7%s &cis not a valid minecraft particle.", args[0]));
			return true;
		}
		
		Class<? extends SpecialParticle> specialParticleClass = ParticleHandler.get(particle.toString());
		
		SpecialParticle specialParticle = null;
		
		
		try {
			specialParticle = GeneralUtil.createMapParsable(specialParticleClass, Commands.grabArgs(args, 1, args.length));
			if(specialParticle instanceof OTHER)
				((OTHER) specialParticle).setParticle(particle);
		}
		
		catch(IllegalArgumentException|NullPointerException e) {
			Messenger.send(sender,  e.getMessage());
			return true;
		}
		// This will catch if theres any other error with generating this mapParsable
		catch(InvocationTargetException e) {
			Messenger.send(sender, "&cParticle effect could not be added due to an uncommon error. Please check server console for error trace.");
			return true;
		}
		
		// Save the new specialParticle to the effect
		effect.addParticle(specialParticle);

		// Save the Effect to YML
		BrEffect.YML().save(effect);
		
		// Updates all items that contain references to this effect. Doing this updates any relevant changes to the items effect.
		effect.updateReferences();
		
		// Allows the user to view the details on hover
//		TextComponent successMsgComponent = StringsUtil.hoverText(successMsg, successHoverText);
		
//		Messenger.send(sender, successMsgComponent);
		return true;
		
		
//		// Checking sound type is given
//		if(args.length < 2) {
//			Messenger.send(sender, "&ePlease specify a formation.");
//			return true;
//		}
//		
//		 Checking/Saving formation
//		Formation formation = FormationHandler.get(args[1]);
//		// If formation is null, it is invalid
//		if(formation == null) {
//			Messenger.send(sender, "&7"+args[1]+" &cis not a valid Formation.");
//			return true;
//		}
//		Formation formation = null;
//		try {
//			formation = StringsUtil.constructEnum(args[1], Formation.class);
//		} catch (IllegalArgumentException e) {
//			Messenger.send(sender, String.format("&7%s &cis not a valid Formation.", args[1]));
//			return true;
//		}
//		
//		// Checking intensity is given
//		if(args.length < 3) {
//			Messenger.send(sender, "&ePlease provide an intensity. This must be a rounded, positive number or 0.");
//			return false;
//		}
//		
//		// Checking/Saving intensity
//		Integer intensity = null;
//		try {
//			intensity = Integer.parseInt(args[2]);
//		} catch (NumberFormatException e) {
//			Messenger.send(sender, String.format("&7%s &cis not a valid intensity. It must be a rounded, positive number or 0.", args[2]));
//			return true;
//		}
//		
//		// Checking its positive
//		if(intensity < 0) {
//			Messenger.send(sender, "&cIntensity must be a positive number or 0.");
//			return true;
//		}
//		
//		// Checking velocity is given
//		if(args.length < 4) {
//			Messenger.send(sender, "&ePlease provide a velocity. This must be a positive number or 0.");
//			return true;
//		}
//		
//		// Checking/Saving velocity
//		Double velocity = null;
//		try {
//			velocity = Double.parseDouble(args[3]);
//		} catch (NumberFormatException e) {
//			Messenger.send(sender, String.format("&7%s &cis not a valid velocity. It must be a positive number or 0.", args[3]));
//			return true;
//		}
//		
//		// Checking its positive
//		if(velocity < 0) {
//			Messenger.send(sender, "&cVelocity must be a positive number or 0.");
//			return true;
//		}
//		
//		// Creating the particle effect
////		BrParticleEffect particleEffect = new BrParticleEffect(particle, formation, intensity, velocity);
//		NewBrParticleEffect particleEffect = new NewBrParticleEffect();
//		
//		// Grabbing effect
//		BrEffect effect = (BrEffect) objects[0];
//
//		String successMsg = String.format("&aAdded new Particle Effect to &7%s&a. Hover to view the details!", effect.getName());
//		
//		String successHoverText = generateEditingTitle(effect);
//		
////		NewBrParticleEffect previousParticleEffect = effect.getParticles();
////
////		// If the particle effect values are the same, do nothing and message appropriately
////		if(particleEffect.equals(previousParticleEffect)) {
////			Messenger.send(sender, StringsUtil.hoverText(String.format("&7%s&e already has a Particle Effect with those values. Hover to view it!", effect.getName())
////														, successHoverText + effect.getParticleEffect().toChatString(0, true)));
////			return true;
////		}
////		if(previousParticleEffect != null) {
////			// If its different, then it is changing
////			successMsg = String.format("&aChanged the Particle Effect for &7%s&a. Hover to view the details!", effect.getName());
////			successHoverText += "&2"+particleEffect.className()+":\n"
////							  + StringsUtil.tableCompareFormatter(0,
////								s -> "&2  "+s[0]+": &7"+s[1]
////							  , s -> "&2  "+s[0]+": &8"+s[1]+" &7-> "+s[2]
////							  , StringsUtil.getFieldNames(BrParticleEffect.class) , StringsUtil.prepValueStrings(previousParticleEffect) , StringsUtil.prepValueStrings(particleEffect));
////		}
////		// There was previously none, so add it!
////		else
////			successHoverText += Chat.add(particleEffect.toChatString(0, true), ChatColor.BOLD);
////		
////		// Sets the particle effect to effect
////		effect.setParticleEffect(particleEffect);
//
//		// Save the Effect to YML
//		BrEffect.YML().save(effect);
//		
//		// Updates all items that contain references to this effect. Doing this updates any relevant changes to the items effect.
//		effect.updateReferences();
//
//		// Allows the user to view the details on hover
//		TextComponent successMsgComponent = StringsUtil.hoverText(successMsg, successHoverText);
//		
//		Messenger.send(sender, successMsgComponent);
//		return true;
	}
	
	@Override
	public List<String> tabComplete(CommandSender sender, String[] args, Object... objects) {
		BrEffect effect = (BrEffect) objects[0];
		
		if(effect == null)
			return Arrays.asList();
		
		// If its the first argument (args.length == 1), send all Particles
		if(args.length < 2)
			return Arrays.asList(StringsUtil.enumValueStrings(Particle.class));
				
		Class<? extends SpecialParticle> specialParticleClass = ParticleHandler.get(args[0]);

		if(specialParticleClass == null)
			return Arrays.asList();
		
		return MapParsable.getSuggestion(specialParticleClass, Commands.grabArgs(args, 1, args.length));
	}
}
