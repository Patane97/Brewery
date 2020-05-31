package com.Patane.Brewery.CustomEffects;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import com.Patane.Brewery.Handlers.BrMetaDataHandler;
import com.Patane.util.general.Chat;
import com.Patane.util.general.ChatStringable;
import com.Patane.util.general.StringsUtil;
import com.Patane.util.general.StringsUtil.LambdaStrings;
import com.Patane.util.ingame.LocationsUtil;

public class Filter implements ChatStringable{
	private FilterType target;
	private FilterType ignore;
	
	/* ================================================================================
	 * Constructors
	 * ================================================================================
	 */
	public Filter() {
		target = new FilterType(null, null, null, null, true);
		ignore = new FilterType(null, null, null, null, false);
	}
	public Filter(FilterType target, FilterType ignore) {
		this.target = target;
		this.ignore = ignore;
	}
	/* ================================================================================
	 * Getters, Checkers and Has...ers
	 * ================================================================================
	 */
	public FilterType getType(FilterTypes type) {
		switch(type) {
		case TARGET:
			return target;
		case IGNORE:
			return ignore;
		}
		return null;
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
	public boolean isActive() {
		return (target.noFilter() && ignore.noFilter() ? false : true);
	}
	public boolean equals(Filter other) {
		if(target.equals(other.getType(FilterTypes.TARGET)) && ignore.equals(other.getType(FilterTypes.IGNORE)))
			return true;
		return false;
	}
	public List<FilterTypes> getNonEmptyTypes() {
		List<FilterTypes> nonEmpty = new ArrayList<FilterTypes>();
		if(!target.noFilter())
			nonEmpty.add(FilterTypes.TARGET);
		if(!ignore.noFilter())
			nonEmpty.add(FilterTypes.IGNORE);
		return nonEmpty;
	}

	/* ================================================================================
	 * Contains, Add & Remove
	 * ================================================================================
	 */
	public boolean contains(FilterTypes type, FilterGroups group, String value) {
		FilterType filterType = getType(type);
		switch(group) {
			case ENTITIES:
			try {
				return filterType.getEntities().contains(StringsUtil.constructEnum(value, EntityType.class));
			} catch(IllegalArgumentException e) {
				return false;
			}
			case PLAYERS:
				return filterType.getPlayers().contains(value);
			case PERMISSIONS:
				return filterType.getPermissions().contains(value);
			case TAGS:
				return filterType.getTags().contains(value);
			}
		return false;
	}
	
	
	public boolean add(FilterTypes type, FilterGroups group, String value) {
		FilterType filterType = getType(type);
		switch(group) {
			case ENTITIES:
				try {
					filterType.addEntity(StringsUtil.constructEnum(value, EntityType.class));
					break;
				} catch(IllegalArgumentException e) {
					return false;
				}
			case PLAYERS:
				filterType.addPlayer(value);
				break;
			case PERMISSIONS:
				filterType.addPermission(value);
				break;
			case TAGS:
				filterType.addTag(value);
				break;
		}
		return true;
	}
	public boolean remove(FilterTypes type, FilterGroups group, String value) {
		FilterType filterType = getType(type);
		switch(group) {
			case ENTITIES:
				try {
					filterType.removeEntity(StringsUtil.constructEnum(value, EntityType.class));
					break;
				} catch(IllegalArgumentException e) {
					return false;
				}
			case PLAYERS:
				filterType.removePlayer(value);
				break;
			case PERMISSIONS:
				filterType.removePermission(value);
				break;
			case TAGS:
				filterType.removeTag(value);
				break;
		}
		return true;
	}
	
	/* ================================================================================
	 * ChatStringable Methods
	 * ================================================================================
	 */
	
	@Override
	public LambdaStrings layout() {
		// Example: &2Type: &7Name
		return s -> "&2"+s[0]+"&2: &7"+s[1];
	}
	
	@Override
	public String toChatString(int indentCount, boolean deep, LambdaStrings alternateLayout) {
		// USEFUL SECTION TO COPY TO OTHER TOCHATSTRINGS!
		// If the alternateLayout is null, we want to use the default layout for itself
		alternateLayout = (alternateLayout == null ? layout() : alternateLayout);
		// //////////////////////////////////////////////

		// If it is inactive, show it
		if(!isActive())
			return Chat.indent(indentCount) + alternateLayout.build("Filter", "&8Inactive");
		
		// If its not deep, we just want to show the filter and how many elements it is targetting and ignoring
		// Current format: Active (TICK5 CROSS3)
		if(!deep)
			return Chat.indent(indentCount) + alternateLayout.build("Filter", 
					"Active ("+FilterTypes.TARGET.symbol+"&7"+target.getSize()+" "+FilterTypes.IGNORE.symbol+"&7"+ignore.getSize()+")");
		
		// Adding all filters to string
		// Each method checks if the filter contains its element in target or ignore. If not, gives nothing
		String filterString = Chat.indent(indentCount) + alternateLayout.build("Filter", "")
							+ entitiesToChatString(indentCount+1, false, alternateLayout)
							+ playersToChatString(indentCount+1, false, alternateLayout)
							+ permissionsToChatString(indentCount+1, false, alternateLayout)
							+ tagsToChatString(indentCount+1, false, alternateLayout);
		// Returns the filter string OR empty if theres no filter
		return filterString;
	}
	
	@Override
	public String toChatString(int indentCount, boolean deep) {
		return toChatString(indentCount, deep, null);
	}
	public String toChatStringInsert(int indentCount, FilterTypes type, FilterGroups group, String value, LambdaStrings alternateLayout, LambdaStrings insert) {
		
		// If the alternateLayout is null, use default layout
		alternateLayout = (alternateLayout == null ? layout() : null);
		
		String selectedSymbol = (type == FilterTypes.TARGET ? FilterTypes.TARGET.symbol : FilterTypes.IGNORE.symbol);
		
		String filterString = Chat.indent(indentCount) + alternateLayout.build("Filter", "");
		
		filterString += entitiesToChatString(indentCount+1, (group == FilterGroups.ENTITIES), alternateLayout);
		if(group == FilterGroups.ENTITIES)
			filterString += "\n" + Chat.indent(indentCount) + insert.build(selectedSymbol, value);
		filterString += playersToChatString(indentCount+1, (group == FilterGroups.PLAYERS), alternateLayout);
		if(group == FilterGroups.PLAYERS)
			filterString += "\n" + Chat.indent(indentCount) + insert.build(selectedSymbol, value);
		filterString += permissionsToChatString(indentCount+1, (group == FilterGroups.PERMISSIONS), alternateLayout);
		if(group == FilterGroups.PERMISSIONS)
			filterString += "\n" + Chat.indent(indentCount) + insert.build(selectedSymbol, value);
		filterString += tagsToChatString(indentCount+1, (group == FilterGroups.TAGS), alternateLayout);
		if(group == FilterGroups.TAGS)
			filterString += "\n" + Chat.indent(indentCount) + insert.build(selectedSymbol, value);
		
		return filterString;
	}
	/* ================================================================================
	 * Private ChatStringable-related Methods
	 * ================================================================================
	 */

	private final LambdaStrings targetLayout = s -> FilterTypes.TARGET.symbol + " &7"+s[0];
	private final LambdaStrings ignoreLayout = s -> FilterTypes.IGNORE.symbol + " &7"+s[0];
	
	private String entitiesToChatString(int indentCount, boolean titleIfEmpty, LambdaStrings layout) {
		String entitiesString = "";
		// ENTITIES
		if(hasEntities() || (!hasEntities() && titleIfEmpty)) {
			// Starts with Entities title
			entitiesString += "\n" + Chat.indent(indentCount) + layout.build("Entities","");
			// Adds all targets with target layout
			for(EntityType entityType : target.getEntities())
				entitiesString += "\n" + Chat.indent(indentCount+1) + targetLayout.build(entityType.name());
			// Adds all ignores with ignore layout
			for(EntityType entityType : ignore.getEntities())
				entitiesString += "\n" + Chat.indent(indentCount+1) + ignoreLayout.build(entityType.name());
		}
		return entitiesString;
	}
	private String playersToChatString(int indentCount, boolean titleIfEmpty, LambdaStrings layout) {
		String playersString = "";
		// PLAYERS
		if(hasPlayers() || (!hasPlayers() && titleIfEmpty)) {
			// Starts with Players title
			playersString += "\n" + Chat.indent(indentCount) + layout.build("Players","");
			// Adds all targets with target layout
			for(String player : target.getPlayers())
				playersString += "\n" + Chat.indent(indentCount+1) + targetLayout.build(player);
			// Adds all ignores with ignore layout
			for(String player : ignore.getPlayers())
				playersString += "\n" + Chat.indent(indentCount+1) + ignoreLayout.build(player);
		}
		return playersString;
	}
	private String permissionsToChatString(int indentCount, boolean titleIfEmpty, LambdaStrings layout) {
		String permissionsString = "";
		// PERMISSIONS
		if(hasPermissions() || (!hasPermissions() && titleIfEmpty)) {
			// Starts with Permissions title
			permissionsString += "\n" + Chat.indent(indentCount) + layout.build("Permissions","");
			// Adds all targets with target layout
			for(String permission : target.getPermissions())
				permissionsString += "\n" + Chat.indent(indentCount+1) + targetLayout.build(permission);
			// Adds all ignores with ignore layout
			for(String permission : ignore.getPermissions())
				permissionsString += "\n" + Chat.indent(indentCount+1) + ignoreLayout.build(permission);
		}
		return permissionsString;
	}
	private String tagsToChatString(int indentCount, boolean titleIfEmpty, LambdaStrings layout) {
		String tagsString = "";
		// TAGS
		if(hasTags() || (!hasTags() && titleIfEmpty)) {
			// Starts with Tags title
			tagsString += "\n" + Chat.indent(indentCount) + layout.build("Tags","");
			// Adds all targets with target layout
			for(String tag : target.getTags())
				tagsString += "\n" + Chat.indent(indentCount+1) + targetLayout.build(tag);
			// Adds all ignores with ignore layout
			for(String tag : ignore.getTags())
				tagsString += "\n" + Chat.indent(indentCount+1) + ignoreLayout.build(tag);
		}
		return tagsString;
	}
//	public String toChatString(LambdaStrings layout, FilterGroups... groups) {
//	if(groups.length == 0)
//		groups = FilterGroups.values();
//	
//	String filterString = "";
//	List<FilterGroups> groupList = Arrays.asList(groups);
//	if(groupList.contains(FilterGroups.ENTITIES))
//		filterString += entitiesToChatString(layout, false);
//	if(groupList.contains(FilterGroups.PLAYERS))
//		filterString += playersToChatString(layout, false);
//	if(groupList.contains(FilterGroups.PERMISSIONS))
//		filterString += permissionsToChatString(layout, false);
//	if(groupList.contains(FilterGroups.TAGS))
//		filterString += tagsToChatString(layout, false);
//	return filterString;
//}

	/* ================================================================================
	 * Filtering Methods
	 * ================================================================================
	 */
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

/* ================================================================================
 * Filter Specific Classes
 * ================================================================================
 */
	/* ================================================================================
	 * Filter Enums (Types and Groups)
	 * ================================================================================
	 */
	public static enum FilterTypes {
		TARGET("&f\u2714"), IGNORE("&8\u2718");
		
		public String symbol;
		FilterTypes(String symbol){
			this.symbol = symbol;
		}
	}
	public static enum FilterGroups {
		ENTITIES, PLAYERS, PERMISSIONS, TAGS;
	}
	
	/* ================================================================================
	 * Filter Type Class
	 * ================================================================================
	 */	
	public static class FilterType {
		private List<EntityType> entities;
		private List<String> players;
		private List<String> permissions;
		private List<String> tags;
		
		private int totalSize;
		
		private boolean defaultReturn;
		
		public FilterType(List<EntityType> entities, List<String> players, List<String> permissions, List<String> tags, boolean defaultReturn){
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
		
		public List<FilterGroups> getNonEmptyGroups() {
			List<FilterGroups> nonEmpty = new ArrayList<FilterGroups>();
			if(!entities.isEmpty())
				nonEmpty.add(FilterGroups.ENTITIES);
			if(!players.isEmpty())
				nonEmpty.add(FilterGroups.PLAYERS);
			if(!permissions.isEmpty())
				nonEmpty.add(FilterGroups.PERMISSIONS);
			if(!tags.isEmpty())
				nonEmpty.add(FilterGroups.TAGS);
			return nonEmpty;
		}
		
		public List<String> getAsString(FilterGroups group) {
			switch(group) {
				case ENTITIES:
					return StringsUtil.enumValueStrings(entities);
				case PLAYERS:
					return players;
				case PERMISSIONS:
					return permissions;
				case TAGS:
					return tags;
			}
			return Arrays.asList();
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
		public boolean equals(FilterType other) {
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
				// If the group matches, they pass.
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

	/* ================================================================================
	 * *** Temporary attempt of generating FilterType using TYPEs.
	 *     Doesnt work, probably not worth looking into but will keep here just incase
	 * ================================================================================
	 */
	// This is an attempt at making filter type work as one list (filterGroups). Was not successful but could be attempted later.
//	public static class FilterType {
//	Map<FilterGroup, FilterGroupContainer<?>> filterGroups = new LinkedHashMap<FilterGroup, FilterGroupContainer<?>>();
//	
//	private boolean defaultReturn;
//	
//	private int totalSize;
//	
//	
//	public FilterType(List<EntityType> entities, List<String> players, List<String> permissions, List<String> tags, boolean defaultReturn) {
//		filterGroups.put(FilterGroup.ENTITIES, new FilterGroupContainer<EntityType>(EntityType.class, entities, 
//				new FilterPass<EntityType>() {
//					@Override
//					public boolean check(List<EntityType> groupList, LivingEntity entity) {
//						for(EntityType entityType : groupList)
//							if(entity.getType() == entityType)
//								return true;
//						return false;
//					}
//				}));
//		filterGroups.put(FilterGroup.PLAYERS, new FilterGroupContainer<String>(String.class, players,
//				new FilterPass<String>() {
//					@Override
//					public boolean check(List<String> groupList, LivingEntity entity) {
//						if(entity instanceof Player) {
//							for(String player : groupList)
//								if(entity.getName().equals(player) || entity.getUniqueId().toString().equals(player))
//									return true;
//							}
//						return false;
//					}
//				}));
//		filterGroups.put(FilterGroup.PERMISSIONS, new FilterGroupContainer<String>(String.class, permissions,
//				new FilterPass<String>() {
//					@Override
//					public boolean check(List<String> groupList, LivingEntity entity) {
//						if(entity instanceof Player) {
//							for(String permission : groupList)
//								if(entity.hasPermission(permission))
//									return true;
//						}
//						return false;
//					}
//				}));
//		filterGroups.put(FilterGroup.TAGS, new FilterGroupContainer<String>(String.class, tags,
//				new FilterPass<String>() {
//					@Override
//					public boolean check(List<String> groupList, LivingEntity entity) {
//						for(String tag : groupList)
//							if(BrMetaDataHandler.check(entity, "<TAG-"+tag+">"))
//								return true;
//						return false;
//					}
//				}));			
//		this.defaultReturn = defaultReturn;
//		totalSize = 0;
//		for(FilterGroupContainer<?> container : filterGroups.values())
//			totalSize += container.getList().size();
//		
//	}
//	
//	public boolean match(LivingEntity entity) {
//		for(FilterGroup filterGroup : filterGroups.keySet()) {
//			if(filterGroups.get(filterGroup).check(entity))
//				return true;
//		}
//		return false;
//	}
//	
//	public int getSize() {
//		
//		boolean add = add(FilterGroup.ENTITIES);
//		
//		return totalSize;
//	}
//	
//	public <T> boolean add(FilterGroup filterGroup, T t) {
//		filterGroups.get(filterGroup).getList(t)
//	}
//	
//	public List<EntityType> getEntities(){
//		return filterGroups.get(FilterGroup.ENTITIES).getList(EntityType.class);
//	}
//	
//	public List<String> getPlayers(){
//		return filterGroups.get(FilterGroup.PLAYERS).getList(String.class);
//	}
//	
//	public List<String> getPermissions(){
//		return filterGroups.get(FilterGroup.PERMISSIONS).getList(String.class);
//	}
//	
//	public List<String> getTags(){
//		return filterGroups.get(FilterGroup.TAGS).getList(String.class);
//	}
//	
//}
//public static class FilterGroupContainer<T> {
//	private List<T> groupList;
//	private FilterPass<T> filterPass;
//	private Class<T> clazz;
//	private T t;
//	
//	FilterGroupContainer(Class<T> clazz, List<T> filterGroupList, FilterPass<T> filterPass) {
//		this.groupList = (filterGroupList == null ? new ArrayList<T>() : filterGroupList);
//		this.filterPass = filterPass;
//		this.clazz = clazz;
//	}
//	
//	public boolean check(LivingEntity entity) {
//		return filterPass.check(groupList, entity);
//	}
//	
//	@SuppressWarnings("unchecked")
//	public <L> List<L> getList(T type){
//		return (List<L>) groupList;
//	}
//	
//	public List<T> getList(){
//		return groupList;
//	}
//	
//}
//public static enum FilterGroup {
//	ENTITIES(EntityType.class),
//	PLAYERS(String.class),
//	PERMISSIONS(String.class),
//	TAGS(String.class);
//	
//	public final Class<?> clazz;
//	
//	FilterGroup(Class<?> clazz){
//		this.clazz = clazz;
//	}
//}
//protected interface FilterPass<T>{
//	public boolean check(List<T> groupList, LivingEntity entity);
//}
}
