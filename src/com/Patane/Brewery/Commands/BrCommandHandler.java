package com.Patane.Brewery.commands;

import com.Patane.Brewery.NEWcommands.primary.createCommand;
import com.Patane.Brewery.NEWcommands.primary.editCommand;
import com.Patane.Brewery.NEWcommands.primary.editSessionCommand;
import com.Patane.Brewery.NEWcommands.primary.giveCommand;
import com.Patane.Brewery.NEWcommands.primary.helpCommand;
import com.Patane.Brewery.NEWcommands.primary.infoCommand;
import com.Patane.Brewery.NEWcommands.primary.listCommand;
import com.Patane.Brewery.NEWcommands.primary.reloadCommand;
import com.Patane.Brewery.NEWcommands.primary.removeCommand;
import com.Patane.Brewery.NEWcommands.secondary.editing.editItem;
import com.Patane.Brewery.NEWcommands.secondary.editing.editItemCooldown;
import com.Patane.Brewery.NEWcommands.secondary.editing.editItemItemstack;
import com.Patane.Brewery.NEWcommands.secondary.editing.editItemItemstackAttributes;
import com.Patane.Brewery.NEWcommands.secondary.editing.editItemItemstackAttributesAdd;
import com.Patane.Brewery.NEWcommands.secondary.editing.editItemItemstackAttributesRemove;
import com.Patane.Brewery.NEWcommands.secondary.editing.editItemType;
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
import com.Patane.Commands.CommandHandler;

public class BrCommandHandler extends CommandHandler{
	
	protected void registerAll() {
		// Primary commands
		register(reloadCommand.class);
		register(giveCommand.class);
		register(helpCommand.class);
		register(listCommand.class);
		register(infoCommand.class);
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
		
		register(editItem.class);
		register(editItemCooldown.class);
		register(editItemType.class);
		register(editItemItemstack.class);
		register(editItemItemstackAttributes.class);
		register(editItemItemstackAttributesAdd.class);
		register(editItemItemstackAttributesRemove.class);
		// Edit Item commands
		/*
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
		*/
	}
}
