package com.Patane.Brewery.Commands;

import com.Patane.Brewery.Commands.primary.*;
import com.Patane.Brewery.Commands.secondary.*;
import com.Patane.Brewery.Commands.secondary.editing.*;
import com.Patane.Commands.CommandHandler;
import com.Patane.util.general.Messenger;
import com.Patane.util.general.StringsUtil;

public class BrCommandHandler extends CommandHandler{
	
	public BrCommandHandler() {
		super();
		registerAll();
		Messenger.debug("Registered Commands: " + StringsUtil.stringJoiner(commands.keySet(), ", "));
	}
	
	private void registerAll() {
		// Primary commands
		register(reloadCommand.class);
		register(giveCommand.class);
		register(helpCommand.class);
		register(listCommand.class);
		register(infoCommand.class);
		register(tagCommand.class);
		register(createCommand.class);
		register(removeCommand.class);
		register(editSessionCommand.class);
		register(editCommand.class);
//		register(guiCommand.class);
		
		// Secondary commands
		register(listEffects.class);
		register(listItems.class);
		register(infoEffect.class);
		register(infoItem.class);
		register(createItem.class);
		register(removeItem.class);
		register(createEffect.class);
		register(removeEffect.class);
		register(editSessionEnd.class);
		register(editSessionItem.class);
		register(editSessionEffect.class);
		
		// Edit Item commands
		register(itemEditItem.class);
		register(itemEditItemName.class);
		register(itemEditItemLore.class);
		register(itemEditItemLoreSet.class);
		register(itemEditItemLoreDelete.class);
		register(itemEditItemFlags.class);
		register(itemEditItemFlagsAdd.class);
		register(itemEditItemFlagsRemove.class);
		register(itemEditItemEnchantment.class);
		register(itemEditItemEnchantmentAdd.class);
		register(itemEditItemEnchantmentRemove.class);
		register(itemEditItemAttributes.class);
		register(itemEditItemAttributesAdd.class);
		register(itemEditItemAttributesRemove.class);
		register(itemEditType.class);
		register(itemEditCooldown.class);
	}
}
