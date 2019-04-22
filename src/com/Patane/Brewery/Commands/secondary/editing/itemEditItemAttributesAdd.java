package com.Patane.Brewery.Commands.secondary.editing;

import java.util.UUID;

import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.attribute.AttributeModifier.Operation;
import org.bukkit.command.CommandSender;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

import com.Patane.Brewery.CustomItems.BrItem;
import com.Patane.Brewery.Editing.EditSession;
import com.Patane.Commands.CommandInfo;
import com.Patane.util.general.Messenger;
import com.Patane.util.general.StringsUtil;
import com.Patane.util.ingame.ItemsUtil;
@CommandInfo(
	name = "edit item attributes [attribute] add",
	aliases = {"set"},
	description = "Adds an Attribute Modifier an item.",
	usage = "/br edit item attributes [attribute] add <modifier name> <amount> (operation) (slot)",
	hideCommand = true
)
public class itemEditItemAttributesAdd extends itemEditItemAttributes {

	@Override
	public boolean execute(CommandSender sender, String[] args, Object... objects) {
		
		Attribute attribute = (Attribute) objects[0];
		Messenger.debug(StringsUtil.stringJoiner(args, ", "));
		if(args.length < 1) {
			Messenger.send(sender, "&cPlease provide a name for this modifier.");
			return false;
		}

		if(args.length < 2) {
			Messenger.send(sender, "&cPlease provide an amount for this modifier.");
			return false;
		}

		AttributeModifier modifier = null;
		
		
		String modifierName = args[0];
		
		Integer amount = null;
		try {
			amount = Integer.parseInt(args[1]);
		} catch (NumberFormatException e) {
			Messenger.send(sender, "&7"+args[1]+" &cis an invalid amount.");
			return true;
		}
		
		//Find a way to check if its a string too (enum ordinal?)
		Operation operation = null;
		if(args.length < 3)
			operation = Operation.ADD_NUMBER;
		else {
			try {
				operation = StringsUtil.constructEnum(args[2], Operation.class);
			} catch (IllegalArgumentException e) {
				Messenger.send(sender, "&7"+args[2]+" &c is an invalid operation. Options are "+StringsUtil.stringJoiner(StringsUtil.enumValueStrings(Operation.class), "&c, &7", "&7", "&c."));
				return true;
			}
		}
		
		EquipmentSlot slot = null;
		if(args.length < 4)
			slot = EquipmentSlot.HAND;
		else {
			try {
				slot = StringsUtil.constructEnum(args[3], EquipmentSlot.class);
			} catch (IllegalArgumentException e) {
				Messenger.send(sender, "&7"+args[3]+" &c is an invalid equipment slot. Options are "+StringsUtil.stringJoiner(StringsUtil.enumValueStrings(EquipmentSlot.class), "&c, &7", "&7", "&c."));
				return true;
			}
		}
		
		modifier = new AttributeModifier(UUID.randomUUID(), modifierName, amount, operation, slot);
		
		BrItem brItem = (BrItem) EditSession.get(sender.getName());
		ItemStack currentItem = brItem.getItemStack();
		
		String successMsg = "&aAdded &7"+modifierName+" &amodifier to &7"+attribute.name()+" &aattribute for item.";
		if(ItemsUtil.hasAttributeModifier(currentItem, attribute, modifierName)) {
			currentItem = ItemsUtil.removeAttributeModifier(currentItem, attribute, modifierName);
			successMsg = "&aChanged &7"+modifierName+" &amodifier for &7"+attribute.name()+" &aattribute.";
		}
			
		brItem.setItemStack(ItemsUtil.addAttributeModifier(currentItem, attribute, modifier));
		
		BrItem.YML().save(brItem);
		
		Messenger.send(sender, successMsg);
		return true;
	}
}
