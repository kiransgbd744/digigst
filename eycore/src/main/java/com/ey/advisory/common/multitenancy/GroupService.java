package com.ey.advisory.common.multitenancy;

import java.util.List;
import java.util.Map;

import com.ey.advisory.core.async.domain.master.Group;
import com.ey.advisory.core.async.domain.master.GroupConfig;




public interface GroupService {

	/**
	 * Get all groups in the DB.
	 * 
	 * @return
	 */
	public List<Group> getAllGroups();

	/**
	 * Get a map with GroupCode/Group Object mapping with all groups in the DB
	 * 
	 * @return
	 */
	public Map<String, Group> getGroupMap();

	/**
	 * Load the list of GroupConfig objects for the specified Group Code.
	 * 
	 * @param groupCode
	 * @return
	 */
	public List<GroupConfig> getGroupConfigs(String groupCode);
	
	
	public Group getGroupInfo(String groupCode);
	
	/**
	 * Add a new Group to our system and update the global Group configuration
	 * data structures with the new information.
	 * 
	 * @return
	 */
	public Group addNewGroup(Group group, List<GroupConfig> configs);
	
	/**
	 * 
	 * @param groupCode
	 * @return
	 */
	public Group syncGroupFromDB(String groupCode);

}
