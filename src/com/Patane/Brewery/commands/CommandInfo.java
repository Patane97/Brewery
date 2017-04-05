package com.Patane.Brewery.commands;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface CommandInfo {
	public String name();
	public String description();
	public String usage();
	public String permission();
}
