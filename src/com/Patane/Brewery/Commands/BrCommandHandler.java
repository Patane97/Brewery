package com.Patane.Brewery.Commands;

import com.Patane.Brewery.Commands.primary.giveCommand;
import com.Patane.Brewery.Commands.primary.guiCommand;
import com.Patane.Brewery.Commands.primary.helpCommand;
import com.Patane.Brewery.Commands.primary.infoCommand;
import com.Patane.Brewery.Commands.primary.listCommand;
import com.Patane.Brewery.Commands.primary.tagCommand;
import com.Patane.Brewery.Commands.secondary.infoEffect;
import com.Patane.Brewery.Commands.secondary.infoItem;
import com.Patane.Brewery.Commands.secondary.listEffects;
import com.Patane.Brewery.Commands.secondary.listItems;
import com.Patane.Commands.CommandHandler;
import com.Patane.util.general.Messenger;
import com.Patane.util.general.Messenger.Msg;
import com.Patane.util.general.StringsUtil;

public class BrCommandHandler extends CommandHandler{
	
	public BrCommandHandler() {
		super();
		registerAll();
		Messenger.debug(Msg.INFO, "Registered Commands: " + StringsUtil.stringJoiner(commands.keySet(), ", "));
	}
	
	private void registerAll() {
		register(giveCommand.class);
		register(helpCommand.class);
		register(listCommand.class);
		register(infoCommand.class);
		register(tagCommand.class);
		register(guiCommand.class);
		register(listEffects.class);
		register(listItems.class);
		register(infoEffect.class);
		register(infoItem.class);
	}
}
