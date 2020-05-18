package com.Patane.Brewery.Commands.secondary.editing;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.attribute.AttributeModifier.Operation;
import org.bukkit.command.CommandSender;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

import com.Patane.Brewery.CustomItems.BrItem;
import com.Patane.Brewery.Editing.EditSession;
import com.Patane.Commands.CommandHandler.CommandPackage;
import com.Patane.Commands.CommandInfo;
import com.Patane.util.general.Messenger;
import com.Patane.util.general.StringsUtil;
import com.Patane.util.ingame.ItemsUtil;
@CommandInfo(
	name = "edit item attributes add",
	aliases = {"set"},
	description = "Adds an Attribute Modifier an item.",
	usage = "/brewery edit item attributes add [attribute] <modifier name> <amount> (operation) (slot)",
	maxArgs = 5,
	hideCommand = true
)
public class itemEditItemAttributesAdd extends itemEditItemAttributes {

	@Override
	public boolean execute(CommandSender sender, String[] args, Object... objects) {

		if(args.length < 1) {
			Messenger.send(sender, "&cPlease provide an item attribute to modify.");
			return false;
		}
		Attribute attribute = null;

		try {
			// Attempting to find the Attribute enum from given string name.
			attribute = StringsUtil.constructEnum(args[0], Attribute.class);
		} catch (IllegalArgumentException e) {
			Messenger.send(sender, "&7"+args[0]+" &cis not a valid Attribute.");
			return true;
		}
		
		if(args.length < 2) {
			Messenger.send(sender, "&cPlease provide a name for this modifier.");
			return false;
		}
		String modifierName = args[1];

		if(args.length < 3) {
			Messenger.send(sender, "&cPlease provide an amount for this modifier.");
			return false;
		}
		Double amount = null;
		try {
			amount = Double.parseDouble(args[2]);
		} catch (NumberFormatException e) {
			Messenger.send(sender, "&7"+args[2]+" &cis an invalid amount.");
			return true;
		}
		
		// *** Find a way to check if its a string too (enum ordinal?)
		Operation operation = null;
		if(args.length < 4)
			operation = Operation.ADD_NUMBER;
		else {
			try {
				operation = StringsUtil.constructEnum(args[3], Operation.class);
			} catch (IllegalArgumentException e) {
				Messenger.send(sender, "&7"+args[3]+" &c is an invalid operation.");
				return true;
			}
		}
		
		EquipmentSlot slot = null;
		if(args.length < 5)
			slot = EquipmentSlot.HAND;
		else {
			try {
				if(!args[4].equalsIgnoreCase("ALL"))
					slot = StringsUtil.constructEnum(args[4], EquipmentSlot.class);
			} catch (IllegalArgumentException e) {
				Messenger.send(sender, "&7"+args[4]+" &c is an invalid equipment slot.");
				return true;
			}
		}
		
		AttributeModifier modifier = new AttributeModifier(UUID.randomUUID(), modifierName, amount, operation, slot);
		
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
	
	@Override
	public List<String> tabComplete(CommandSender sender, String[] args, CommandPackage thisPackage) {
		switch(args.length) {
			case 5: return Arrays.asList(StringsUtil.enumValueStrings(Attribute.class));
			case 6: return Arrays.asList("<modifier name>");
			case 7: return Arrays.asList("<amount>");
			case 8: return Arrays.asList(StringsUtil.enumValueStrings(Operation.class));
			default: 
				List<String> equipSlots = Arrays.asList(StringsUtil.enumValueStrings(EquipmentSlot.class));
				equipSlots = new ArrayList<String>(equipSlots);
				equipSlots.add("ALL");
				return equipSlots;
		}
	}
}
