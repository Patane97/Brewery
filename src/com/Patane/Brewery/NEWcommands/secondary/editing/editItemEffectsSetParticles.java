package com.Patane.Brewery.NEWcommands.secondary.editing;

import java.util.Arrays;
import java.util.List;

import org.bukkit.Particle;
import org.bukkit.command.CommandSender;

import com.Patane.Brewery.CustomEffects.BrEffect;
import com.Patane.Brewery.CustomEffects.BrEffect.BrParticleEffect;
import com.Patane.Brewery.CustomEffects.Formation;
import com.Patane.Brewery.CustomItems.BrItem;
import com.Patane.Brewery.Handlers.FormationHandler;
import com.Patane.Commands.CommandInfo;
import com.Patane.util.general.Messenger;
import com.Patane.util.general.StringsUtil;

import net.md_5.bungee.api.chat.TextComponent;
@CommandInfo(
	name = "edit item effects set particles",
	description = "Sets or Changes the Particle Effects of an Effect for a Brewery Item. These changes are seperate from the original Effect.",
	usage = "/brewery edit item <item name> effects set <effect name> particles [type] [formation] <intensity> <velocity>",
	maxArgs = 4
)
public class editItemEffectsSetParticles extends editItemEffectsSet {

	@Override
	public boolean execute(CommandSender sender, String[] args, Object... objects) {
		
		// Checking particle type is given
		if(args.length < 1) {
			Messenger.send(sender, "&ePlease specify a particle type.");
			return true;
		}

		// Checking/Saving particle type
		Particle particle = null;
		try {
			particle = StringsUtil.constructEnum(args[0], Particle.class);
		} catch (IllegalArgumentException e) {
			Messenger.send(sender, "&7"+args[0]+" &cis not a valid Particle Type.");
			return true;
		}
		
		// Checking sound type is given
		if(args.length < 2) {
			Messenger.send(sender, "&ePlease specify a formation.");
			return true;
		}
		
		// Checking/Saving formation
		Formation formation = FormationHandler.get(args[1]);
		// If formation is null, it is invalid
		if(formation == null) {
			Messenger.send(sender, "&7"+args[1]+" &cis not a valid Formation.");
			return true;
		}
		
		// Checking intensity is given
		if(args.length < 3) {
			Messenger.send(sender, "&ePlease provide an intensity. This must be a positive number or 0.");
			return false;
		}
		
		// Checking/Saving intensity
		Integer intensity = null;
		try {
			intensity = Integer.parseInt(args[2]);
		} catch (NumberFormatException e) {
			Messenger.send(sender, "&7"+args[2]+" &cis not a valid intensity. It must be a positive number or 0.");
			return true;
		}
		
		// Checking its positive
		if(intensity < 0) {
			Messenger.send(sender, "&cIntensity must be a positive number or 0.");
			return true;
		}
		
		// Checking velocity is given
		if(args.length < 4) {
			Messenger.send(sender, "&ePlease provide a velocity. This must be a positive number or 0.");
			return true;
		}
		
		// Checking/Saving velocity
		Double velocity = null;
		try {
			velocity = Double.parseDouble(args[3]);
		} catch (NumberFormatException e) {
			Messenger.send(sender, "&7"+args[3]+" &cis not a valid velocity. It must be a positive number or 0.");
			return true;
		}
		
		// Checking its positive
		if(velocity < 0) {
			Messenger.send(sender, "&cVelocity must be a positive number or 0.");
			return true;
		}
		
		// Creating the particle effect
		BrParticleEffect particleEffect = new BrParticleEffect(particle, formation, intensity, velocity);
		
		// Grabbing item
		BrItem item = (BrItem) objects[0];
		
		// Grabbing effect
		BrEffect effect = (BrEffect) objects[1];

		String successMsg = "&aAdded new Particle Effect to &7"+item.getName()+"&a's instance of &7"+effect.getName()+"&a. Hover to view the details!";
		
		String successHoverText = generateEditingTitle(item, effect);
		
		BrParticleEffect previousParticleEffect = effect.getParticleEffect();

		// If the particle effect values are the same, do nothing and message appropriately
		if(particleEffect.equals(previousParticleEffect)) {
			Messenger.send(sender, StringsUtil.hoverText("&7"+item.getName()+"&e's instance of &7"+effect.getName()+"&e already has a Particle Effect with those values. Hover to view it!"
														, successHoverText + effect.getParticleEffect().toChatString(0, true)));
			return true;
		}
		if(previousParticleEffect != null) {
			// If its different, then it is changing
			successMsg = "&aChanged the Particle Effect for &7"+item.getName()+"&a's instance of &7"+effect.getName()+"&a. Hover to view the details!";
			successHoverText += "&2"+particleEffect.className()+":"
							  + StringsUtil.compareFormatter(
								s -> "\n&2  "+s[0]+": &7"+s[1]
							  , s -> "\n&2  "+s[0]+": &8"+s[1]+" &7-> "+s[2]
							  , StringsUtil.getFieldNames(BrParticleEffect.class) , StringsUtil.prepValueStrings(previousParticleEffect) , StringsUtil.prepValueStrings(particleEffect));
		}
		else
			successHoverText += particleEffect.toChatString(0, true);
		
		// Sets the particle effect to effect
		effect.setParticle(particleEffect);

		// Save the Item to the YML. This will also save the instance of the effect to the item
		BrItem.YML().save(item);

		// Allows the user to view the details on hover
		TextComponent successMsgComponent = StringsUtil.hoverText(successMsg, successHoverText);
		
		Messenger.send(sender, successMsgComponent);
		return true;
	}
	
	@Override
	public List<String> tabComplete(CommandSender sender, String[] args, Object... objects) {
		switch(args.length) {
			case 1: return Arrays.asList(StringsUtil.enumValueStrings(Particle.class));
			case 2: return FormationHandler.getKeys();
			case 3: return Arrays.asList("<intensity>");
			case 4: return Arrays.asList("<velocity>");
		}
		return Arrays.asList();
	}
}
