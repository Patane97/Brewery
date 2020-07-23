package com.Patane.Brewery.Commands.secondary.edit.item.itemstack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.attribute.AttributeModifier.Operation;
import org.bukkit.command.CommandSender;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

import com.Patane.Brewery.CustomItems.BrItem;
import com.Patane.Commands.CommandInfo;
import com.Patane.util.general.Chat;
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
			Messenger.send(sender, "&ePlease specify an item attribute.");
			return true;
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
			Messenger.send(sender, "&ePlease provide a name for this modifier. This can be any non-existing modifier name.");
			return true;
		}
		
		// Find/Save attribute name 
		String modifierName = args[1];
		
		// Check for amount
		if(args.length < 3) {
			Messenger.send(sender, "&ePlease provide an amount for this modifier. This must be a positive number or 0.");
			return true;
		}
		
		Double amount = null;
		
		// Find/save amount
		try {
			amount = Double.parseDouble(args[2]);
		} catch (NumberFormatException e) {
			Messenger.send(sender, "&7"+args[2]+" &cis not a valid amount. It must be a positive number or 0.");
			return true;
		}
		
		// Checking its positive
		if(amount < 0) {
			Messenger.send(sender, "&cAmount must be a positive number or 0.");
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
				Messenger.send(sender, "&7"+args[3]+" &cis not a valid operation.");
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
				Messenger.send(sender, "&7"+args[4]+" &cis not a valid equipment slot.");
				return true;
			}
		}
		
		// Creating the modifier
		AttributeModifier modifier = new AttributeModifier(UUID.randomUUID(), modifierName, amount, operation, slot);
		
		// Grabbing BrItem from objects
		BrItem item = (BrItem) objects[0];
		
		// Grabbing ItemStack to add attribute to
		ItemStack currentItem = item.getItemStack();
		
		String successMsg = "&aAdded attribute modifier for &7"+item.getName()+"&a. Hover for details!";
		String successHoverText = "&f&l"+item.getNameLimited(15)+"&f&l's attributes\n"
								+ StringsUtil.toChatString(0, true, s -> "&2"+s[0]+"&2: &7"+s[1], attribute, ItemsUtil.getAttributeModifiers(currentItem, attribute).toArray(new AttributeModifier[0]));

		// If modifier already existed, either check/say its the same values or show it updating values
		if(ItemsUtil.hasAttributeModifier(currentItem, attribute, modifierName)) {
			AttributeModifier oldModifier = ItemsUtil.getAttributeModifier(currentItem, attribute, modifierName);
			
			// If the old modifier and the new one are the exact same, do nothing and message appropriately
			if(oldModifier.getAmount() == modifier.getAmount() 
					&& oldModifier.getOperation() == modifier.getOperation() 
					&& oldModifier.getSlot() == modifier.getSlot()) {
				Messenger.send(sender, StringsUtil.hoverText("&eThat attribute modifier for &7"+item.getName()+"&e already has those values. Hover for details!", successHoverText));
				return true;
			}
			successMsg = "&aUpdated existing attribute modifier for &7"+item.getName()+"&a. Hover for details!";
			// If the new modifier value is different to the old, show the changed dynamically on hover
			successHoverText = "&2Attribute: &7"+attribute.toString()+"\n"
							+ StringsUtil.tableCompareFormatter(1, s -> "&2"+s[0]+"&2: &7"+s[1], s -> "&2"+s[0]+"&2: &8"+s[1]+" &7-> "+s[2]
							, StringsUtil.getFieldNames(modifier), StringsUtil.prepValueStrings(oldModifier), StringsUtil.prepValueStrings(modifier));
			
			// Remove the old Modifier
			currentItem = ItemsUtil.removeAttributeModifier(currentItem, attribute, modifierName);
		}
		else
			successHoverText += "\n"+Chat.add(StringsUtil.toChatString(0, true, s -> "&2"+s[0]+"&2: &7"+s[1], null, modifier), ChatColor.BOLD);
		
		// Allows the user to view the details of the attribute they just modified!
		TextComponent successMsgComponent = StringsUtil.hoverText(successMsg, successHoverText);
		
		// Save modifier to itemstack
		item.setItemStack(ItemsUtil.addAttributeModifier(currentItem, attribute, modifier));
		
		// Save YML
		BrItem.YML().save(item);

		// Refreshes all inventory brItems.
		BrItem.refreshAllInventories();
		
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
		}
		return Arrays.asList();
	}
}
