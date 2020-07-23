package com.Patane.Brewery.Commands.secondary.edit.item.effects;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;

import org.bukkit.Particle;
import org.bukkit.command.CommandSender;

import com.Patane.Brewery.CustomEffects.BrEffect;
import com.Patane.Brewery.CustomItems.BrItem;
import com.Patane.Commands.CommandInfo;
import com.Patane.util.formables.SpecialParticle;
import com.Patane.util.general.Chat;
import com.Patane.util.general.GeneralUtil;
import com.Patane.util.general.Messenger;
import com.Patane.util.general.StringsUtil;

import net.md_5.bungee.api.chat.TextComponent;
@CommandInfo(
	name = "edit item effects modify set particles remove",
	aliases = {"rem", "delete", "del"},
	description = "Removes a Particle Effect from an Effect for a Brewery Item. These changes are seperate from the original Effect.",
	usage = "/brewery edit item <item name> effects modify <effect name> set particles remove [particle] (increment)",
	maxArgs = 2
)
public class editItemEffectsModifySetParticlesRemove extends editItemEffectsModifySetParticles {

	@Override
	public boolean execute(CommandSender sender, String[] args, Object... objects) {
		
		// Checking particle type is given
		if(args.length < 1) {
			Messenger.send(sender, "&ePlease specify an existing minecraft particle.");
			return true;
		}
		
		// Checking/Saving particle type
		Particle particle;
		try {
			particle = StringsUtil.constructEnum(args[0], Particle.class);
		} catch (IllegalArgumentException e) {
			Messenger.send(sender, String.format("&7%s &cis not a valid minecraft particle.", args[0]));
			return true;
		}
		
		Integer increment = null;
		// Checking if increment is given
		if(args.length > 1) {
			try {
				increment = Integer.parseInt(args[1]);
				
				// If an increment is provided, it must not be 0 or below.
				if(increment < 0) {
					Messenger.send(sender, "&cIncrement must be a rounded, positive number or 0.");
					return true;
				}
			} catch (NumberFormatException e) {
				// If amount is not recognised as a number (eg. "5/")
				Messenger.send(sender, String.format("&7%s &cis not a valid increment.", args[1]));
				return true;
			}
		}
		
		// Find Item
		BrItem item = (BrItem) objects[0]; 
		
		// Find Effect
		BrEffect effect = (BrEffect) objects[1];
		
		String successMsg = String.format("&aRemoved a Particle Effect from &7%s&a's instance of &7%s&a. Hover to view the details!", item.getName(), effect.getName());
		
		String successHoverText = generateEditingTitle(effect)
								+ "&2Particle Effects:\n";
		
		SpecialParticle particleEffect = null;
		
		try {
			particleEffect = GeneralUtil.getIncremented(effect.getParticles(), increment, new Predicate<SpecialParticle>() {
				@Override
				public boolean test(SpecialParticle p) {
					return p.getParticle() == particle;
				}
			});
		} 
		// If the particle effect type could not be found
		catch(NullPointerException e) {
			Messenger.send(sender, StringsUtil.hoverText(String.format("&7%s&e's instance of &7%s&e does not contain a particle effect of that type. Hover to view all particle effects on this effect!", item.getName(), effect.getName())
					, successHoverText + (effect.hasParticles() ? StringsUtil.manyToChatString(1, 0, true, null, null, effect.getParticles().toArray(new SpecialParticle[0])) : "&8No Particle Effects!")));
			return true;
		} 
		// If the increment value SHOULD be provided but isnt
		catch(IllegalStateException e) {
			Messenger.send(sender, StringsUtil.hoverText(String.format("&ePlease provide an increment value as &7%s&e's instance of &7%s&e has multiple particle effects of the given type. Hover to view all particle effects on this effect!", item.getName(), effect.getName())
					, successHoverText + (effect.hasParticles() ? StringsUtil.manyToChatString(1, 0, true, null, null, effect.getParticles().toArray(new SpecialParticle[0])) : "&8No Particle Effects!")));
			return true;
		} 
		// If the increment value is higher than the amount of particles of the found type
		catch(ArrayIndexOutOfBoundsException e) {
			Messenger.send(sender, StringsUtil.hoverText(String.format("&eIncrement is too high to identify which particle of this type you would like to remove for &7%s&e's instance of &7%s&e. Hover to view all particle effects on this effect!", item.getName(), effect.getName())
					, successHoverText + (effect.hasParticles() ? StringsUtil.manyToChatString(1, 0, true, null, null, effect.getParticles().toArray(new SpecialParticle[0])) : "&8No Particle Effects!")));
			return true;
		}
		
		// Remove particle effect from effect
		effect.removeParticle(particleEffect);
		
		// Add the removed particle effect to the end of the new particle effect list
		successHoverText += (effect.hasParticles() ? StringsUtil.manyToChatString(1, 0, true, null, null, effect.getParticles().toArray(new SpecialParticle[0]))+"\n" : "")
				  		  + String.format("%s", particleEffect.toChatString(1, true, s -> "&c"+Chat.replace(s[0], "&7", "&8&m")+"&c: &8&m"+Chat.replace(s[1], "&8&m")+"&r"));
		
		// Save the Item to the YML. This will also save the instance of the effect to the item
		BrItem.YML().save(item);
		
		// Allows the user to view the details on hover
		TextComponent successMsgComponent = StringsUtil.hoverText(successMsg, successHoverText);
		
		Messenger.send(sender, successMsgComponent);
		return true;
	}
	@Override
	public List<String> tabComplete(CommandSender sender, String[] args, Object... objects) {
		// Grabbing Effect
		BrEffect effect = (BrEffect) objects[1];
		
		switch(args.length) {
		case 1:
			List<String> uniqueParticles = new ArrayList<String>();
			for(SpecialParticle particleEffect : effect.getParticles()) {
				if(!uniqueParticles.contains(particleEffect.getParticle().toString()))
					uniqueParticles.add(particleEffect.getParticle().toString());
			}
			return uniqueParticles;
		case 2:
			Particle particle;
			try {
				particle = StringsUtil.constructEnum(args[0], Particle.class);
			} catch(IllegalArgumentException e) {
				return Arrays.asList();
			}
			List<SpecialParticle> particlesOfType = new ArrayList<SpecialParticle>(effect.getParticles());
			particlesOfType.removeIf(new Predicate<SpecialParticle>() {
				@Override
				public boolean test(SpecialParticle p) {
					return p.getParticle() != particle;
}
			});
			return StringsUtil.listCount(particlesOfType, 0);
		}
		
		return Arrays.asList();
	}
}
