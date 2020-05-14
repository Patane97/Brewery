package com.Patane.Brewery.NEWcommands.secondary.editing;

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
import com.Patane.Commands.CommandInfo;
import com.Patane.util.general.Messenger;
import com.Patane.util.general.StringsUtil;
import com.Patane.util.ingame.ItemsUtil;

import net.md_5.bungee.api.chat.TextComponent;

@CommandInfo(
	name = "edit item item attributes add",
	aliases = {"set"},
	description = "Adds an Attribute Modifier to a physical Minecraft Item for a Brewery Item.",
	usage = "/brewery edit item <item name> item attributes add [attribute] <modifier name> <amount> (operation) (slot)",
	maxArgs = 5,
	hideCommand = true
)
public class editItemItemstackAttributesAdd extends editItemItemstackAttributes {
	
	@Override
	public boolean execute(CommandSender sender, String[] args, Object... objects) {
		
		// Checks for attribute
		if(args.length < 1) {
			Messenger.send(sender, "&cPlease provide a valid item attribute.");
			return false;
		}
		Attribute attribute = null;

		// Find/Save attribute
		try {
			attribute = StringsUtil.constructEnum(args[0], Attribute.class);
		} catch (IllegalArgumentException e) {
			Messenger.send(sender, "&7"+args[0]+" &cis not a valid Attribute.");
			return true;
		}
		
		// Check for attribute name
		if(args.length < 2) {
			Messenger.send(sender, "&cPlease provide a name for this modifier. Any name will do!");
			return false;
		}
		
		// Find/Save attribute name 
		String modifierName = args[1];
		
		// Check for amount
		if(args.length < 3) {
			Messenger.send(sender, "&cPlease provide an amount for this modifier.");
			return false;
		}
		
		Double amount = null;
		
		// Find/save amount
		try {
			amount = Double.parseDouble(args[2]);
		} catch (NumberFormatException e) {
			Messenger.send(sender, "&7"+args[2]+" &cis an invalid amount.");
			return true;
		}
		
		// Check, Find and Save Operation (optional)
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

		// Check, Find and Save Slot (optional)
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
		
		// Creating the modifier
		AttributeModifier modifier = new AttributeModifier(UUID.randomUUID(), modifierName, amount, operation, slot);
		
		// Grabbing BrItem from objects
		BrItem brItem = (BrItem) objects[0];
		
		// Grabbing ItemStack to add attribute to
		ItemStack currentItem = brItem.getItemStack();
		
		String successMsg = "&aAdded attribute modifier for &7"+brItem.getName()+"&a. Hover for details!";
		
		String successHoverText = StringsUtil.attribModToString(attribute, modifier);
				
		// If there is a modifier with same name, remove and replace it.
		// Change successMsg to reflect so
		if(ItemsUtil.hasAttributeModifier(currentItem, attribute, modifierName)) {
			AttributeModifier oldModifier = ItemsUtil.getAttributeModifier(currentItem, attribute, modifierName);
			
			// If the old modifier and the new one are the exact same, do nothing and message appropriately
			if(oldModifier.getAmount() == modifier.getAmount() 
					&& oldModifier.getOperation() == modifier.getOperation() 
					&& oldModifier.getSlot() == modifier.getSlot()){
				Messenger.send(sender, StringsUtil.hoverText("&eThat attribute modifier for &7"+brItem.getName()+"&e already has those values. Hover for details!", successHoverText));
				return true;
			}
			// If the new modifier value is different to the old, show the changed dynamically on hover
			successHoverText = StringsUtil.attribModDifferenceToString(attribute, oldModifier, modifier);
			
			// Remove the old Modifier
			currentItem = ItemsUtil.removeAttributeModifier(currentItem, attribute, modifierName);
			successMsg = "&aUpdated existing attribute modifier for &7"+brItem.getName()+"&a. Hover for details!";
		}
		
		// Allows the user to view the details of the attribute they just modified!
		TextComponent successMsgComponent = StringsUtil.hoverText(successMsg, successHoverText);
		
		// Save modifier to itemstack
		brItem.setItemStack(ItemsUtil.addAttributeModifier(currentItem, attribute, modifier));
		
		// Save YML
		BrItem.YML().save(brItem);
		
		// Send user success message
		Messenger.send(sender, successMsgComponent);
		return true; 
		
	}
	
	@Override
	public List<String> tabComplete(CommandSender sender, String[] args, Object... objects) {
		switch(args.length) {
			case 1: return Arrays.asList(StringsUtil.enumValueStrings(Attribute.class));
			case 2: return Arrays.asList("<modifier name>");
			case 3: return Arrays.asList("<amount>");
			case 4: return Arrays.asList(StringsUtil.enumValueStrings(Operation.class));
			case 5: 
				List<String> equipSlots = Arrays.asList(StringsUtil.enumValueStrings(EquipmentSlot.class));
				equipSlots = new ArrayList<String>(equipSlots);
				equipSlots.add("ALL");
				return equipSlots;
			default:
				return new ArrayList<String>();
	}
	}
}
