package com.Patane.Brewery.commands.secondary;

import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.inventory.ItemStack;

import com.Patane.Brewery.Brewery;
import com.Patane.Brewery.CustomEffects.BrEffect;
import com.Patane.Brewery.CustomEffects.BrEffectYML;
import com.Patane.Brewery.CustomItems.BrItem;
import com.Patane.Brewery.commands.primary.giveCommand;
import com.Patane.Brewery.commands.primary.infoCommand;
import com.Patane.Commands.CommandHandler;
import com.Patane.Commands.CommandHandler.CommandPackage;
import com.Patane.Commands.CommandInfo;
import com.Patane.util.general.Chat;
import com.Patane.util.general.Messenger;
import com.Patane.util.general.StringsUtil;
import com.Patane.util.ingame.Commands;
import com.Patane.util.ingame.ItemsUtil;

import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
@CommandInfo(
	name = "info item",
	description = "Gives detailed information on a specific Item.",
	usage = "/brewery info item <item name>",
	maxArgs = 1
)
public class infoItem extends infoCommand{

	@Override
	public boolean execute(CommandSender sender, String[] args, Object... objects) {
		String itemName = Commands.combineArgs(args);
		BrItem item = Brewery.getItemCollection().getItem(itemName);
		if(item == null) {
			Messenger.send(sender, "&7"+StringsUtil.stringJoiner(args, " ")+" &cis not a registered Brewery item.");
			return false;
		}
		Messenger.send(sender, StringsUtil.generateChatTitle(StringsUtil.formaliseString(item.getName())+" info"));
		
		TextComponent text = StringsUtil.hoverText("&9&lSpawn Item", "&7Click here to get this item");
		text.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, CommandHandler.getPackage(giveCommand.class).buildString(sender.getName(), item.getName())));
		Messenger.sendRaw(sender, text);
		// Type
		Messenger.sendRaw(sender, StringsUtil.hoverText("&2Type: &7"+item.getType().name(), "&7"+item.getType().getDescription()));
		
		// Item
		text = StringsUtil.hoverText("&2Item: &7"+item.getItemStack().getType().toString(), itemText(item.getItemStack()));
		text.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, CommandHandler.getPackage(giveCommand.class).buildString(sender.getName(), item.getName())));
		Messenger.sendRaw(sender, text);
		
		// Cooldown
		if(item.hasCooldown())
			Messenger.sendRaw(sender, "&2Cooldown: &7"+item.getCooldown()+" seconds");
		
		// Effects
		if(item.hasEffects()) {
			Messenger.sendRaw(sender, "&2Effects:\n");
			for(BrEffect effect : item.getEffects()) {
				BrEffect completeEffect = BrEffectYML.retrieve(BrItem.YML().getSection(item.getName(), "effects", effect.getName()), BrEffect.YML().getSection(effect.getName()));
				TextComponent commandText = new TextComponent(Chat.translate(" &a> &7"+effect.getName()));
				commandText.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(completeEffect.hoverDetails()).create()));
				commandText.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, CommandHandler.getPackage(infoEffect.class).buildString(effect.getName())));
				Messenger.sendRaw(sender, commandText);
			}
		}
		return true;
	}
	
	@Override
	public List<String> tabComplete(CommandSender sender, String[] args, CommandPackage thisPackage) {
		return Brewery.getItemCollection().getAllIDs();
	}
	
	private static String itemText(ItemStack item) {
		// Type, Name, Lore, maxStacks, ignored flags
		String hoverText = (ItemsUtil.hasDisplayName(item) ? "&2Name: &7"+ItemsUtil.getDisplayName(item) : "")
						 + (ItemsUtil.hasLore(item) ? "\n&2Lore: \n &7"+StringsUtil.stringJoiner(ItemsUtil.getLore(item), "\n &7") : "");
		// ADD MAXSTACKS
		// ADD IGNORED FLAGS
		if(hoverText.equals(""))
			hoverText = "&7No Information";
		return hoverText;
	}
}
