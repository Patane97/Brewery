package com.Patane.Brewery.NEWcommands.secondary;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.potion.PotionEffect;

import com.Patane.Brewery.Brewery;
import com.Patane.Brewery.Commands.primary.infoCommand;
import com.Patane.Brewery.CustomEffects.BrEffect;
import com.Patane.Brewery.CustomEffects.Filter;
import com.Patane.Brewery.CustomEffects.Filter.FilterType;
import com.Patane.Brewery.CustomEffects.Filter.FilterTypes;
import com.Patane.Commands.CommandHandler.CommandPackage;
import com.Patane.Commands.CommandInfo;
import com.Patane.util.general.Chat;
import com.Patane.util.general.Messenger;
import com.Patane.util.general.StringsUtil;
import com.Patane.util.ingame.Commands;

import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
@CommandInfo(
	name = "info effect",
	description = "Gives detailed information on a specific Effect.",
	usage = "/brewery info effect <effect name>",
	maxArgs = 1
)
public class infoEffect extends infoCommand{

	@Override
	public boolean execute(CommandSender sender, String[] args, Object... objects) {
		String effectName = Commands.combineArgs(args);
		BrEffect effect = Brewery.getEffectCollection().getItem(effectName);
		if(effect == null) {
			Messenger.send(sender, "&7"+StringsUtil.stringJoiner(args, " ")+" &cis not a registered Brewery effect.");
			return false;
		}
		Messenger.send(sender, StringsUtil.generateChatTitle(StringsUtil.formaliseString(effect.getName())+" effect"));
		mapParsableInfo(sender, "Modifier", effect.getModifier());
		mapParsableInfo(sender, "Trigger", effect.getTrigger());
		if(effect.hasRadius())
			Messenger.sendRaw(sender, "&2Radius: &7"+effect.getRadius());
		if(effect.hasTag())
			Messenger.sendRaw(sender, "&2Tag: &7"+effect.getTag().getName());
		if(effect.hasPotions()) {
			Messenger.sendRaw(sender, "&2Potion Effects: &7");
			potionEffectsInfo(sender, effect.getPotions());
		}
		if(effect.hasParticle())
			mapParsableInfo(sender, "Particles", effect.getParticleEffect());
		if(effect.hasSound())
			mapParsableInfo(sender, "Sounds", effect.getParticleEffect());
		if(effect.hasFilter()) {
			Messenger.sendRaw(sender, "&2Filter: &7");
			filterInfo(sender, effect.getFilter());
		}
		return true;
	}
	
	@Override
	public List<String> tabComplete(CommandSender sender, String[] args, CommandPackage thisPackage) {
		return Brewery.getEffectCollection().getAllIDs();
	}
	
	private void potionEffectsInfo(CommandSender sender, List<PotionEffect> potionEffects) {
		String hoverText;
		TextComponent infoText;
		// Loops through each potionEffect given.
		for(PotionEffect potionEffect : potionEffects) {
			// Resets the hoverText (from previous loops)
			hoverText = "";
			
			// Construct a TextComponent with the following format: " &a> &7Potion Type"
			infoText = new TextComponent(Chat.translate(" &a> &7"+potionEffect.getType().getName()));
			
			// Creating hoverText to display relevant information in clearly shown format below.
			hoverText += "&2Type: &7"+potionEffect.getType().getName()
					  	+"\n&2Duration: &a"+potionEffect.getDuration()
						+"\n&2Intensity: &a"+potionEffect.getAmplifier()
						+"\n&2Ambient: &a"+potionEffect.isAmbient()
						+"\n&2Particles: &a"+potionEffect.hasParticles();
			
			// Saving the HoverEvent to the hoverText
			infoText.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(Chat.translate(hoverText)).create()));
			
			// Sending the potionEffects to the sender.
			Messenger.sendRaw(sender, infoText);
		}
	}
	
	private void filterInfo(CommandSender sender, Filter filter) {
		if(!filter.getType(FilterTypes.TARGET).noFilter()) {
			Messenger.sendRaw(sender, " &2Target: &7");
			filterTypeInfo(sender, filter.getType(FilterTypes.TARGET));
		}
		if(!filter.getType(FilterTypes.IGNORE).noFilter()) {
			Messenger.sendRaw(sender, " &2Ignore: &7");
			filterTypeInfo(sender, filter.getType(FilterTypes.IGNORE));
		}
	}
	
	private void filterTypeInfo(CommandSender sender, FilterType filterType) {
		TextComponent infoText;
		String hoverText;
		if(!filterType.getEntities().isEmpty()) {
			infoText = new TextComponent(Chat.translate("  &aEntities: &7"+filterType.getEntities().size()));
			List<String> entityTypeStrings = new ArrayList<String>();
			for(EntityType entityType : filterType.getEntities())
				entityTypeStrings.add(entityType.toString());
			hoverText = StringsUtil.stringJoiner(entityTypeStrings, "\n&a> &7", "&a> &7", "");
			infoText.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(Chat.translate(hoverText)).create()));
			Messenger.sendRaw(sender, infoText);
		}
		if(!filterType.getPlayers().isEmpty()) {
			infoText = new TextComponent(Chat.translate("  &aPlayers: &7"+filterType.getPlayers().size()));
			hoverText = StringsUtil.stringJoiner(filterType.getPlayers(), "\n&a> &7", "&a> &7", "");
			infoText.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(Chat.translate(hoverText)).create()));
			Messenger.sendRaw(sender, infoText);
		}
		if(!filterType.getPermissions().isEmpty()) {
			infoText = new TextComponent(Chat.translate("  &aPermissions: &7"+filterType.getPermissions().size()));
			hoverText = StringsUtil.stringJoiner(filterType.getPermissions(), "\n&a> &7", "&a> &7", "");
			infoText.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(Chat.translate(hoverText)).create()));
			Messenger.sendRaw(sender, infoText);
		}
		if(!filterType.getTags().isEmpty()) {
			infoText = new TextComponent(Chat.translate("  &aTags: &7"+filterType.getTags().size()));
			hoverText = StringsUtil.stringJoiner(filterType.getTags(), "\n&a> &7", "&a> &7", "");
			infoText.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(Chat.translate(hoverText)).create()));
			Messenger.sendRaw(sender, infoText);
		}
	}
}
