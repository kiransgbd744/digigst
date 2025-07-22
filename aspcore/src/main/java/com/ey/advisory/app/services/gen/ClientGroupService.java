package com.ey.advisory.app.services.gen;

import java.util.List;
import java.util.Map;

import com.ey.advisory.admin.data.entities.client.GroupInfoEntity;
import com.ey.advisory.gstr2.userdetails.GstinDto;

/**
 * This interface is responsible for getting GROUP related information like
 * Group Details, List of GSTINs associated with a group etc.
 * 
 *
 */
public interface ClientGroupService {
	
	public abstract List<GroupInfoEntity> getAllGroups();
	
	public List<GstinDto> getGstinsForEntity(List<Long> entityIds,
			String groupCode);
	
	public abstract List<EntityDto> getEntitiesForGroup(String groupCode);	
	
	public List<Long> findEntityDetailsForGroupCode();
	
	public Map<String, Long> getGstinAndEntityMapForGroupCode(
			List<Long> entityIds); 
	
	public Map<Long, String> getEntityAndReturnPeriodMapForGroupCode(
			List<Long> entityIdList);

}
