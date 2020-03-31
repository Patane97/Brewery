package com.Patane.Brewery.commands;

import com.Patane.Brewery.commands.primary.createCommand;
import com.Patane.Brewery.commands.primary.editCommand;
import com.Patane.Brewery.commands.primary.editSessionCommand;
import com.Patane.Brewery.commands.primary.giveCommand;
import com.Patane.Brewery.commands.primary.helpCommand;
import com.Patane.Brewery.commands.primary.infoCommand;
import com.Patane.Brewery.commands.primary.listCommand;
import com.Patane.Brewery.commands.primary.reloadCommand;
import com.Patane.Brewery.commands.primary.removeCommand;
import com.Patane.Brewery.commands.primary.tagCommand;
import com.Patane.Brewery.commands.secondary.createEffect;
import com.Patane.Brewery.commands.secondary.createItem;
import com.Patane.Brewery.commands.secondary.editSessionEffect;
import com.Patane.Brewery.commands.secondary.editSessionEnd;
import com.Patane.Brewery.commands.secondary.editSessionItem;
import com.Patane.Brewery.commands.secondary.infoEffect;
import com.Patane.Brewery.commands.secondary.infoItem;
import com.Patane.Brewery.commands.secondary.listEffects;
import com.Patane.Brewery.commands.secondary.listItems;
import com.Patane.Brewery.commands.secondary.removeEffect;
import com.Patane.Brewery.commands.secondary.removeItem;
import com.Patane.Brewery.commands.secondary.editing.itemEditCooldown;
import com.Patane.Brewery.commands.secondary.editing.itemEditEffects;
import com.Patane.Brewery.commands.secondary.editing.itemEditEffectsAdd;
import com.Patane.Brewery.commands.secondary.editing.itemEditEffectsEdit;
import com.Patane.Brewery.commands.secondary.editing.itemEditEffectsEditSet;
import com.Patane.Brewery.commands.secondary.editing.itemEditEffectsEditSetFilter;
import com.Patane.Brewery.commands.secondary.editing.itemEditEffectsEditSetFilterAdd;
import com.Patane.Brewery.commands.secondary.editing.itemEditEffectsEditSetFilterRemove;
import com.Patane.Brewery.commands.secondary.editing.itemEditEffectsEditSetIgnoreUser;
import com.Patane.Brewery.commands.secondary.editing.itemEditEffectsEditSetModifier;
import com.Patane.Brewery.commands.secondary.editing.itemEditEffectsEditSetParticles;
import com.Patane.Brewery.commands.secondary.editing.itemEditEffectsEditSetPotions;
import com.Patane.Brewery.commands.secondary.editing.itemEditEffectsEditSetPotionsAdd;
import com.Patane.Brewery.commands.secondary.editing.itemEditEffectsEditSetPotionsRemove;
import com.Patane.Brewery.commands.secondary.editing.itemEditEffectsEditSetRadius;
import com.Patane.Brewery.commands.secondary.editing.itemEditEffectsEditSetSounds;
import com.Patane.Brewery.commands.secondary.editing.itemEditEffectsEditSetTag;
import com.Patane.Brewery.commands.secondary.editing.itemEditEffectsEditSetTrigger;
import com.Patane.Brewery.commands.secondary.editing.itemEditEffectsRemove;
import com.Patane.Brewery.commands.secondary.editing.itemEditItem;
import com.Patane.Brewery.commands.secondary.editing.itemEditItemAttributes;
import com.Patane.Brewery.commands.secondary.editing.itemEditItemAttributesAdd;
import com.Patane.Brewery.commands.secondary.editing.itemEditItemAttributesRemove;
import com.Patane.Brewery.commands.secondary.editing.itemEditItemEnchantment;
import com.Patane.Brewery.commands.secondary.editing.itemEditItemEnchantmentAdd;
import com.Patane.Brewery.commands.secondary.editing.itemEditItemEnchantmentRemove;
import com.Patane.Brewery.commands.secondary.editing.itemEditItemFlags;
import com.Patane.Brewery.commands.secondary.editing.itemEditItemFlagsAdd;
import com.Patane.Brewery.commands.secondary.editing.itemEditItemFlagsRemove;
import com.Patane.Brewery.commands.secondary.editing.itemEditItemLore;
import com.Patane.Brewery.commands.secondary.editing.itemEditItemLoreDelete;
import com.Patane.Brewery.commands.secondary.editing.itemEditItemLoreSet;
import com.Patane.Brewery.commands.secondary.editing.itemEditItemName;
import com.Patane.Brewery.commands.secondary.editing.itemEditType;
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
		register(itemEditEffects.class);
		register(itemEditEffectsAdd.class);
		register(itemEditEffectsRemove.class);
		register(itemEditEffectsEdit.class);
		register(itemEditEffectsEditSet.class);
		register(itemEditEffectsEditSetModifier.class);
		register(itemEditEffectsEditSetTrigger.class);
		register(itemEditEffectsEditSetRadius.class);
		register(itemEditEffectsEditSetFilter.class);
		register(itemEditEffectsEditSetFilterAdd.class);
		register(itemEditEffectsEditSetFilterRemove.class);
		register(itemEditEffectsEditSetParticles.class);
		register(itemEditEffectsEditSetSounds.class);
		register(itemEditEffectsEditSetTag.class);
		register(itemEditEffectsEditSetIgnoreUser.class);
		register(itemEditEffectsEditSetPotions.class);
		register(itemEditEffectsEditSetPotionsAdd.class);
		register(itemEditEffectsEditSetPotionsRemove.class);
	}
}
