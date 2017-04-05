package com.Patane.Brewery;

import org.bukkit.ChatColor;

public enum Chat {
	PLUGIN_PREFIX("&5[&dBrewery&5]&r ");
	
	private String value;
	
	private Chat (String value){
        set(value);
    }

    void set(String value) {
        this.value = value;
    }
    
    public String toString() {
        return ChatColor.translateAlternateColorCodes('&', value);
    }
    public String format(String s) {
        return (s == null) ? "" : toString().replace("%", s);
    }

	public static String translate(String s) {
		return ChatColor.translateAlternateColorCodes('&', s);
	}
}
