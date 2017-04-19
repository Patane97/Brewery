package com.Patane.Brewery.commands.all;

import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import com.Patane.Brewery.Messenger;
import com.Patane.Brewery.CustomPotions.CustomPotion;
import com.Patane.Brewery.collections.CustomPotions;
import com.Patane.Brewery.commands.BrCommand;
import com.Patane.Brewery.commands.CommandHandler;
import com.Patane.Brewery.commands.CommandInfo;

@CommandInfo(
	name = "potion",
	description = "Gives player a potion",
	usage = "/br potion",
	permission = ""
)
public class potionCommand implements BrCommand{
	@Override
	public boolean execute(Plugin plugin, Player sender, String[] args) {
		String potionName = (args.length > 1 ? CommandHandler.argPotionNameToString(args) : null);
		CustomPotion potion = CustomPotions.grab(potionName);
		if(potionName == null || potionName.trim().isEmpty()){
			Messenger.send(sender, "&cPlease specify a potion name! &7/br potion [name]");
			return false;
		}
		if(potion == null){
			Messenger.send(sender, "&cThere is no potion with the name &7"+potionName+"&c.");
			return false;
		}
		sender.getInventory().addItem(potion.getItemStack());
		Messenger.send(sender, "&aGiving &7"+sender.getDisplayName()+"&a a &7"+potion.getName()+"&a.");
			
		return true;
	}
}
