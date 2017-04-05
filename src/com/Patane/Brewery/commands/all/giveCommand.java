package com.Patane.Brewery.commands.all;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionType;

import com.Patane.Brewery.Chat;
import com.Patane.Brewery.commands.BrCommand;
import com.Patane.Brewery.commands.CommandInfo;
import com.Patane.Brewery.util.BrItem;

@CommandInfo(
	name = "give",
	description = "Gives player test potion",
	usage = "/br give",
	permission = ""
)
public class giveCommand implements BrCommand{

	@Override
	public boolean execute(Plugin plugin, Player sender, String[] args) {
		ItemStack item = new ItemStack(Material.SPLASH_POTION, 1);
		PotionMeta pm = (PotionMeta) item.getItemMeta();
		pm.setDisplayName(Chat.translate("&6Holy Binding") + BrItem.encodeItemData("Br-HOLYBINDING"));
        pm.setBasePotionData(new PotionData(PotionType.SPEED));
		pm.addItemFlags(ItemFlag.HIDE_POTION_EFFECTS);
		item.setItemMeta(pm);
		
		sender.getInventory().addItem(item);
		return true;
	}
}
