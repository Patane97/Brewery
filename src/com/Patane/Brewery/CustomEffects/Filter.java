package com.Patane.Brewery.CustomEffects;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import com.Patane.Brewery.Handlers.BrMetaDataHandler;
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
	public boolean noFilters() {
		return (target.noFilter() && ignore.noFilter() ? true : false);
	}
	
	public boolean equals(Filter other) {
		if(getTarget().equals(other.getTarget()) && getIgnore().equals(other.getIgnore()))
			return true;
		return false;
	}
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
		
		private boolean defaultReturn;
		private boolean noFilter;

		public FilterGroup(List<EntityType> entities, List<String> players, List<String> permissions, List<String> tags, boolean defaultReturn){
			this.entities = (entities == null ? new ArrayList<EntityType>() : entities);
			this.players = (players == null ? new ArrayList<String>() : players);
			this.permissions = (permissions == null ? new ArrayList<String>() : permissions);
			this.tags = (tags == null ? new ArrayList<String>() : tags);
			this.defaultReturn = defaultReturn;
			if(this.entities.isEmpty() && this.players.isEmpty() && this.permissions.isEmpty() && this.tags.isEmpty())
				noFilter = true;
		}
		
		public List<EntityType> getEntities(){
			return entities;
		}
		public List<String> getPlayers(){
			return players;
		}
		public List<String> getPermissions(){
			return permissions;
		}
		public List<String> getTags(){
			return tags;
		}
		public boolean noFilter() {
			return noFilter;
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
			if(noFilter){
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
