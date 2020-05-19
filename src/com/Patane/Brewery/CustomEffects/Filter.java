package com.Patane.Brewery.CustomEffects;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import com.Patane.Brewery.Handlers.BrMetaDataHandler;
import com.Patane.util.general.StringsUtil;
import com.Patane.util.general.StringsUtil.LambdaStrings;
import com.Patane.util.ingame.LocationsUtil;

public class Filter {
	private FilterGroup target;
	private FilterGroup ignore;

	public Filter() {
		target = new FilterGroup(null, null, null, null, true);
		ignore = new FilterGroup(null, null, null, null, false);
	}
	public Filter(FilterGroup target, FilterGroup ignore) {
		this.target = target;
		this.ignore = ignore;
	}
	
	public FilterGroup getTarget() {
		return target;
	}
	public FilterGroup getIgnore() {
		return ignore;
	}
	
	public int getSize() {
		return target.getSize() + ignore.getSize();
	}
	public boolean hasEntities() {
		return (target.getEntities().isEmpty() && target.getEntities().isEmpty() ? false : true);
	}
	public boolean hasPlayers() {
		return (target.getPlayers().isEmpty() && target.getPlayers().isEmpty() ? false : true);
	}
	public boolean hasPermissions() {
		return (target.getPermissions().isEmpty() && target.getPermissions().isEmpty() ? false : true);
	}
	public boolean hasTags() {
		return (target.getTags().isEmpty() && target.getTags().isEmpty() ? false : true);
	}
	
	public void add(String group, String type, String value) throws IllegalArgumentException {
		 FilterGroup filterGroup = null;
		 switch(group.toLowerCase()) {
		 case "target":
			 filterGroup = target;
			 break;
		 case "ignore":
			 filterGroup = ignore;
			 break;
		 }
		 switch(type.toLowerCase()) {
		 case "entities":
			 try {
				 filterGroup.addEntity(StringsUtil.constructEnum(value, EntityType.class));
				 break;
			 } catch(IllegalArgumentException e) {
				 throw new IllegalArgumentException("&7"+value+"&c is not a valid Entity Type.");
			 }
		 case "players":
			 filterGroup.addPlayer(value);
			 break;
		 case "permissions":
			 filterGroup.addPermission(value);
			 break;
		 case "tags":
			 filterGroup.addTag(value);
			 break;
		 }
	}
	public void remove(String group, String type, String value) throws IllegalArgumentException {
		 FilterGroup filterGroup = null;
		 switch(group.toLowerCase()) {
		 case "target":
			 filterGroup = target;
			 break;
		 case "ignore":
			 filterGroup = ignore;
			 break;
		 }
		 switch(type.toLowerCase()) {
		 case "entities":
			 try {
				 filterGroup.removeEntity(StringsUtil.constructEnum(value, EntityType.class));
				 break;
			 } catch(IllegalArgumentException e) {
				 throw new IllegalArgumentException("&7"+value+"&c is not a valid Entity Type.");
			 }
		 case "players":
			 filterGroup.removePlayer(value);
			 break;
		 case "permissions":
			 filterGroup.removePermission(value);
			 break;
		 case "tags":
			 filterGroup.removeTag(value);
			 break;
		 }
	}
	public boolean noFilters() {
		return (target.noFilter() && ignore.noFilter() ? true : false);
	}
	
	public boolean equals(Filter other) {
		if(getTarget().equals(other.getTarget()) && getIgnore().equals(other.getIgnore()))
			return true;
		return false;
	}
	
	/**
	 * Returns the filter in a formatted state appropriate for Chat or HoverText (using \n for new lines)
	 * @param layout Layout to stick to
	 * @return Single string containing all information of this Filter
	 */
	public String toChatString(LambdaStrings layout) {
		String filterString = "";
		// Uses Unicode symbols for a BOLD TICK and a BOLD CROSS for Targets and Ignores.
		LambdaStrings targetLayout = s -> "&f\u2714 &7"+s[0];
		LambdaStrings ignoreLayout = s -> "&8\u2718 &7"+s[0];
		
		// ENTITIES
		if(hasEntities()) {
			// Starts with Entities title
			filterString += "\n  " + layout.build("Entities","");
			// Adds all targets with target layout
			for(EntityType entityType : getTarget().getEntities())
				filterString += "\n    " + targetLayout.build(entityType.name());
			// Adds all ignores with ignore layout
			for(EntityType entityType : getIgnore().getEntities())
				filterString += "\n    " + ignoreLayout.build(entityType.name());
		}
		// PLAYERS
		if(hasPlayers()) {
			// Starts with Players title
			filterString += "\n  " + layout.build("Players","");
			// Adds all targets with target layout
			for(String player : getTarget().getPlayers())
				filterString += "\n    " + targetLayout.build(player);
			// Adds all ignores with ignore layout
			for(String player : getIgnore().getPlayers())
				filterString += "\n    " + ignoreLayout.build(player);
		}
		// PERMISSIONS
		if(hasPermissions()) {
			// Starts with Permissions title
			filterString += "\n  " + layout.build("Permissions","");
			// Adds all targets with target layout
			for(String permission : getTarget().getPermissions())
				filterString += "\n    " + targetLayout.build(permission);
			// Adds all ignores with ignore layout
			for(String permission : getIgnore().getPermissions())
				filterString += "\n    " + ignoreLayout.build(permission);
		}
		// TAGS
		if(hasTags()) {
			// Starts with Tags title
			filterString += "\n  " + layout.build("Tags","");
			// Adds all targets with target layout
			for(String tag : getTarget().getTags())
				filterString += "\n    " + targetLayout.build(tag);
			// Adds all ignores with ignore layout
			for(String tag : getIgnore().getTags())
				filterString += "\n    " + ignoreLayout.build(tag);
		}
		
		// Return filter string
		return filterString;
	}
	/**
	 * OLD METHOD OF LAYING OUT FILTER INFO (now toChatString)
	 */
//		public String filterInfo(LambdaStrings layout, Filter filter) {
//		String filterString = "";
//		LambdaStrings listLayout = s -> "&2> &7"+s[0];
//		if(!filter.getTarget().noFilter()) {
//			filterString += "\n  " + layout.build("Target", "") + filterGroupInfo(layout, listLayout, filter.getTarget());
//		}
//		if(!filter.getIgnore().noFilter()) {
//			filterString += "\n  " + layout.build("Ignore", "") + filterGroupInfo(layout, listLayout, filter.getIgnore());
//		}
//		return filterString;
//	}
//	
//	private String filterGroupInfo(LambdaStrings layout, LambdaStrings listLayout, FilterGroup filterGroup) {
//		String filterGroupString = "";
//		// If entities exist, add them to string
//		if(!filterGroup.getEntities().isEmpty()) {
//			filterGroupString += "\n    " + layout.build("Entities","");
//			// First converting EntityType list into a String list of entity names
//			for(String entity : StringsUtil.enumValueStrings(filterGroup.getEntities()))
//				// Adding each to string with layout
//				filterGroupString += "\n      " + listLayout.build(entity);
//		}
//		
//		// If players exist, add them to string
//		if(!filterGroup.getPlayers().isEmpty()) {
//			filterGroupString += "\n    " + layout.build("Players","");
//			for(String player : filterGroup.getPlayers())
//				// Adding each to string with layout
//				filterGroupString += "\n      " + listLayout.build(player);
//		}
//		
//		// If permissions exist, add them to string
//		if(!filterGroup.getPermissions().isEmpty()) {
//			filterGroupString += "\n    " + layout.build("Permissions","");
//			for(String permission : filterGroup.getPermissions())
//				// Adding each to string with layout
//				filterGroupString += "\n      " + listLayout.build(permission);
//		}
//		
//		// If tags exist, add them to string
//		if(!filterGroup.getTags().isEmpty()) {
//			filterGroupString += "\n    " + layout.build("Tags","");
//			for(String tag : filterGroup.getTags())
//				// Adding each to string with layout
//				filterGroupString += "\n      " + listLayout.build(tag);
//		}
//		
//		// Return the full string
//		return filterGroupString;
//	}
	/**
	 * Extracts each entity that fits the filter.
	 * If given, the entity must not be on the ignore List.
	 * If given, the entity must be on the target List.
	 * @param entities
	 * @return
	 */
	public List<LivingEntity> filter(List<LivingEntity> entities){
		List<LivingEntity> filtered = new ArrayList<LivingEntity>();
		// Loops through each entity given.
		for(LivingEntity entity : entities){
			// Checks if the entity must be ignored (Ignore takes priority over Target)
			// If 'ignore' doesnt match and 'target' DOES match.
			if(!ignore.match(entity) && target.match(entity)){
				filtered.add(entity);
			}
		}
		return filtered;
	}
	
	public List<LivingEntity> filter(LivingEntity entity){
		List<LivingEntity> filtered = new ArrayList<LivingEntity>();
		// Checks if the entity must be ignored (Ignore takes priority over Target)
		// If 'ignore' doesnt match and 'target' DOES match.
		if(!ignore.match(entity) && target.match(entity))
			filtered.add(entity);
		return filtered;
	}
	
	public List<LivingEntity> filter(Location impact, float radius){
//		return filter(LocationsUtil.getEntities(impact, radius));
		return filter(LocationsUtil.getRadius(impact, radius));
	}
	
	
	public static class FilterGroup {
		private List<EntityType> entities;
		private List<String> players;
		private List<String> permissions;
		private List<String> tags;
		
		private int totalSize;
		
		public static String[] types = {"entities", "players", "permissions", "tags"};
		
		private boolean defaultReturn;
		
		public FilterGroup(List<EntityType> entities, List<String> players, List<String> permissions, List<String> tags, boolean defaultReturn){
			this.entities = (entities == null ? new ArrayList<EntityType>() : entities);
			this.players = (players == null ? new ArrayList<String>() : players);
			this.permissions = (permissions == null ? new ArrayList<String>() : permissions);
			this.tags = (tags == null ? new ArrayList<String>() : tags);
			this.defaultReturn = defaultReturn;
			
			totalSize = this.entities.size()
				  + this.players.size()
				  + this.permissions.size()
				  + this.tags.size();
			
		}
		
		public int getSize() {
			return totalSize;
		}
		
		public List<EntityType> getEntities(){
			return entities;
		}
		private void addEntity(EntityType entityType) {
			if(entities.add(entityType))
				totalSize ++;
		}
		private void removeEntity(EntityType entityType) {
			if(entities.remove(entityType))
				totalSize --;
		}
		
		public List<String> getPlayers(){
			return players;
		}
		private void addPlayer(String player) {
			if(players.add(player))
				totalSize ++;
		}
		private void removePlayer(String player) {
			if(players.remove(player))
				totalSize --;
		}
		
		public List<String> getPermissions(){
			return permissions;
		}
		private void addPermission(String permission) {
			if(permissions.add(permission))
				totalSize ++;
		}
		private void removePermission(String permission) {
			if(permissions.remove(permission))
				totalSize --;
		}
		
		public List<String> getTags(){
			return tags;
		}
		private void addTag(String tag) {
			if(tags.add(tag))
				totalSize ++;
		}
		private void removeTag(String tag) {
			if(tags.remove(tag))
				totalSize --;
		}
		
		
		public boolean noFilter() {
			return this.entities.isEmpty() && this.players.isEmpty() && this.permissions.isEmpty() && this.tags.isEmpty();
		}
		public boolean equals(FilterGroup other) {
			for(String tag : getTags())
				if(!other.getTags().contains(tag))
					return false;

			for(String permission : getPermissions())
				if(!other.getPermissions().contains(permission))
					return false;
			
			for(String player : getPlayers())
				if(!other.getPlayers().contains(player))
					return false;
			
			for(EntityType entityType : getEntities())
				if(!other.getEntities().contains(entityType))
					return false;
			return true;
		}
		public boolean match(LivingEntity entity){
			// If there is no filter, then the entity automatically passes.
			if(noFilter()){
				return defaultReturn;
			}
			// Looping through each EntityType in entities
			for(EntityType entityType : entities){
				// If the type matches, they pass.
				if(entity.getType() == entityType)
					return true;
			}
			// Looping through each Tag.
			for(String tag : tags){
				// Checks if the regex with the tag matches the entity.
				if(BrMetaDataHandler.check(entity, "<TAG-"+tag+">"))
					return true;
			}
			// If the entity is a player
			if(entity instanceof Player){
				// Loop through player strings (can be name OR UUID)
				for(String player : players){
					// Checks if the name OR UUID matches. If so, they pass.
					if(entity.getName().equals(player) || entity.getUniqueId().toString().equals(player))
						return true;
				}
				// Loop through permission strings
				for(String permission : permissions){
					// Checks if the player entity has given permission.
					if(entity.hasPermission(permission))
						return true;
				}
			}
			return false;
		}
	}
}
