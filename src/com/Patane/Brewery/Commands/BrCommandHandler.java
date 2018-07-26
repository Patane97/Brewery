package com.Patane.Brewery.Commands;

import com.Patane.Brewery.Commands.all.giveCommand;
import com.Patane.Brewery.Commands.all.helpCommand;
import com.Patane.Brewery.Commands.all.listCommand;
import com.Patane.Commands.CommandHandler;

public class BrCommandHandler extends CommandHandler{
	
	public BrCommandHandler() {
		super();
		registerAll();
	}
	
	private void registerAll() {
		register(giveCommand.class);
		register(helpCommand.class);
		register(listCommand.class);
	}
}
