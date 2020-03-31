package com.Patane.Brewery.commands.secondary.editing;

import org.bukkit.command.CommandSender;
import org.bukkit.potion.PotionEffectType;

import com.Patane.Brewery.CustomEffects.BrEffect;
import com.Patane.Brewery.CustomItems.BrItem;
import com.Patane.Brewery.Editing.EditSession;
import com.Patane.Commands.CommandInfo;
import com.Patane.util.general.Messenger;
import com.Patane.util.general.StringsUtil;
@CommandInfo(
	name = "edit effects edit <effectname> set potions remove",
	aliases = {"rem", "delete", "del"},
	description = "Removes all Potion Effects of a certain type for an Effect that is attached to a Brewery Item.",
	usage = "/brewery edit effects edit <effect name> set potions remove [type]",
	maxArgs = 1
)
public class itemEditEffectsEditSetPotionsRemove extends itemEditEffectsEditSetPotions {

	@Override
	public boolean execute(CommandSender sender, String[] args, Object... objects) {
		
		PotionEffectType type = null;
		type = PotionEffectType.getByName(StringsUtil.normalize(args[0]));
		if(type == null) {
			Messenger.send(sender, "&7"+args[0]+" &c is an invalid Potion Effect Type.");
			return true;
		}
		
		BrEffect brEffect = (BrEffect) objects[0];
		
		brEffect.removePotions(type);
		
		BrItem brItem = (BrItem) EditSession.get(sender.getName());
		
		BrItem.YML().save(brItem);
		
		Messenger.send(sender, "&aRemoved all &7"+type.getName()+" &aPotion Effects from Effect.");
		return true;
	}
}
