package com.ey.advisory.common.multitenancy;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import jakarta.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ey.advisory.common.AppException;
import com.ey.advisory.core.async.domain.master.Group;
import com.ey.advisory.core.async.domain.master.GroupConfig;
import com.ey.advisory.core.async.repositories.master.GroupConfigRepository;
import com.ey.advisory.core.async.repositories.master.GroupRepository;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

@Transactional(value = "masterTransactionManager", readOnly = true)
@Service("groupService")
public class GroupServiceImpl implements GroupService {
	
	private static final Logger LOGGER = 
			LoggerFactory.getLogger(GroupServiceImpl.class);
	    
    @Autowired
    private GroupRepository repository;
    
    @Autowired
    private GroupConfigRepository groupConfigRepository;
	
    private Map<String, Group> groupMap = new ConcurrentHashMap<>();
    
    @Autowired
    @Qualifier("DefaultTenantDBConfigBuilder")    
    private TenantDBConfigBuilder dbCfgBuilder;
    
    @Autowired
    @Qualifier("DefaultDBConfigsModifier")
    private GroupConfigsModifier groupConfsModifier;
    
    @PostConstruct
    public void init() {
    	loadAllGroups();
    }
    
    @Override
	public List<Group> getAllGroups() {
		List<Group> groups = new ArrayList<>();
		groupMap.forEach((groupName, group) -> groups.add(group));
		LOGGER.debug("groupMap {} ", groupMap);
		return groups;
	}

	@Override		
	public List<GroupConfig> getGroupConfigs(String groupCode) {
		
		List<GroupConfig> configs =  groupConfigRepository.
					getGroupConfigvalues(groupCode);
		
		if(configs == null) { return Collections.emptyList(); }
		
		return configs;
	}

	@Override
	public Map<String, Group> getGroupMap() {
    	return Collections.unmodifiableMap(groupMap);
	}
	
	

	private void loadAllGroups() {
		if(LOGGER.isDebugEnabled()) {
    		LOGGER.debug("Building the Group Map from the "
    				+ "list of Groups Available in MasterDB");
    	}    	
    	
		List<Group> groups = repository.findByIsActive(true);
		
		if(groups == null) { 
			if(LOGGER.isDebugEnabled()) {
	    		LOGGER.debug("No groups loaded form MasterDB. "
	    				+ "Returning Empty Map");
	    	}					
			return;
		}
		
		// After loading all the groups, load the Group Configurations.
		Map<String, List<GroupConfig>> groupDBConfigMap = 
				loadAllGroupDBConfigs();
		
		// For each group, find the DBConfig for the tenant and set the 
		// DBConfig object to the group. Note that some of the groups may not
		// have any DBConfig objects.
		groups.forEach(grp -> {
			List<GroupConfig> groupConfigs = groupDBConfigMap.get(grp
					.getGroupCode());
			TenantDBConfig dbConfig = dbCfgBuilder.buildDBConfig(
					grp.getGroupCode(), groupConfigs);
			
			// If the tenant DBConfig object is null, then it implies that 
			// a separate database does not exist for the client.
			grp.setDBConfigIfNotAlreadySet(dbConfig);
			groupMap.put(grp.getGroupCode(), grp);
		});	
		
		if(LOGGER.isDebugEnabled()) {
    		LOGGER.debug("Built the Group Map from the "
    				+ "list of Groups Available in MasterDB");
    	}		
		
	}
	
	
	/**
	 * This method is responsible for loading the Map of GroupCode/GroupConfig
	 * objects for all the active groups in the system.
	 */
	private synchronized Map<String, List<GroupConfig>> 
					loadAllGroupDBConfigs() {
    	
		if(LOGGER.isDebugEnabled()) {
    		LOGGER.debug("Building the GroupDBConfig Map from the "
    				+ "list of GroupConfig details available in MasterDB");
    	}    	
    	    	
    	
    	// Create a list of Configuration names for DB Properties. We need to 
    	// load these properties from the DB for each group.
    	List<String> dbCfgNameList = ImmutableList.of(
    			TenantConstant.DB_URL, TenantConstant.DB_USER_NAME, 
    			TenantConstant.DB_PASSWORD);
    	
    	// Load the list of GroupConfig objects from the repository matching the
    	// configuraiton names mentioned in the list. Load only the properties
    	// for 'Active' Groups. Also load only the 'Active' properties for 
    	// these groups. If a config is marked as Inactive in the DB, it will
    	// not appear in this list.
		List<GroupConfig> groupConfigs = groupConfigRepository.
					getGroupConfigsMatching(dbCfgNameList);
		
		// If there are no Configurations in the DB, then return without doing
		// anything futher.
		if(groupConfigs == null) { 
			if(LOGGER.isDebugEnabled()) {
	    		LOGGER.debug("No group Configs loaded form MasterDB. "
	    				+ "Returning Empty Map");
	    	}		
			return new HashMap<>();
		}
    
		// Using the list of GroupConfig objects obtained for DB config 
		// properties, Group these config objects by GroupCode. (i.e., arrange
		// these objects in a Map, with GroupCode as the key and the list of
		// GroupConfig objects for that group as the value).
		Map<String, List<GroupConfig>> map = groupConfigs.stream()
					.collect(Collectors.groupingBy(
							GroupConfig::getGroupCode));
		
		// Make the necessary changes to the group configs if necessary.
		Map<String, List<GroupConfig>> alteredMap = 
				groupConfsModifier.alterGroupConfigs(map);
		
		
		if(LOGGER.isDebugEnabled()) {
    		LOGGER.debug("Built the Group DB ConfigMap from the list of "
    				+ "Groups Available in MasterDB");
    	}
		return ImmutableMap.copyOf(alteredMap);
	}

	@Override
	public Group getGroupInfo(String groupCode) {
		return groupMap.get(groupCode);
	}
	
	@Override
	public Group syncGroupFromDB(String groupCode) {
				
		// Check to prevent a duplicate insertion to the DB.
		if (groupMap.containsKey(groupCode)) {
			String msg = String.format("The Group with Group Code: '%s' is "
					+ "already configured in the system. Ignoring the "
					+ "duplicate Group Sync request!!", groupCode);
			LOGGER.error(msg);
			return null;
		}
				
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(String.format("Synchronizing new Group with group "
					+ "code: '%s'...", groupCode));
		}
		
		// Load the specified group from DB
		Group group = repository.findByGroupCodeAndIsActiveTrue(groupCode);
		
		if (group == null) {
			String msg = String.format("Unable to find the specified Active group "
					+ "with groupcode '%s'", groupCode);
			LOGGER.error(msg);
			throw new AppException(msg);
		}
		
		// Add the new Group configs to the DB.
		List<GroupConfig> configs = groupConfigRepository
				.findByGroupCodeAndIsActiveTrue(groupCode);
		if (configs == null || configs.isEmpty()) {
			String msg = String.format("Unable to find the Active GroupConfigs "
					+ "with groupcode '%s'", groupCode);
			LOGGER.error(msg);
			throw new AppException(msg);
		}
		
		List<GroupConfig> alteredConfigs =
				groupConfsModifier.alterGroupConfigs(configs);
	
		// Build the new TenantDataSource object for the group and set it
		// to the Group.
		TenantDBConfig dbConfig = dbCfgBuilder.buildDBConfig(groupCode,
				alteredConfigs);				
		group.setDBConfigIfNotAlreadySet(dbConfig);
		
		// After successful creation of the group into the DB and the
		// building of the TenantDBConfig for the group, insert the 
		// Group object into the shared concurrent hash map.
		groupMap.putIfAbsent(groupCode, group);
		
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(String.format(
					"Synchronized and configured the new Group "
							+ "with group code: '%s'!!", group.getGroupCode()));
		}
		
		return group;
	}
	
	@Override
	public synchronized Group addNewGroup(
				Group group, List<GroupConfig> configs) {
				
		// Check to prevent a duplicate insertion to the DB.
		if (groupMap.containsKey(group.getGroupCode())) {
			String msg = String.format("The Group with Group Code: '%s' is "
					+ "already configured in the system. Ignoring the "
					+ "duplicate Group Add request!!", group.getGroupCode());
			LOGGER.error(msg);
			return groupMap.get(group.getGroupCode());
		}
				
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(String.format("Inserting new Group with group "
					+ "code: '%s'...", group.getGroupCode()));
		}
		
		// Add the new group to the DB
		Group newGroup = repository.save(group);
		
		// Attach the group configs with the new group id.
		configs.forEach(conf -> conf.setGroupId(newGroup.getGroupId()));
		
		// Add the new Group configs to the DB.
		List<GroupConfig> newConfigs = groupConfigRepository.saveAll(configs);
	
		// Build the new TenantDataSource object for the group and set it
		// to the Group.
		TenantDBConfig dbConfig = dbCfgBuilder.buildDBConfig(
				group.getGroupCode(), newConfigs);				
		newGroup.setDBConfigIfNotAlreadySet(dbConfig);
		
		// After successful creation of the group into the DB and the
		// building of the TenantDBConfig for the group, insert the 
		// Group object into the shared concurrent hash map.
		groupMap.putIfAbsent(group.getGroupCode(), newGroup);
		
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(String.format("Inserted and configured the new Group "
					+ "with group code: '%s'!!", group.getGroupCode()));
		}
		
		return newGroup;
	}

}
