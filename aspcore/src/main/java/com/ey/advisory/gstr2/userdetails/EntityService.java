/**
 * 
 */
package com.ey.advisory.gstr2.userdetails;

import java.util.List;
import java.util.Map;

import com.ey.advisory.admin.data.entities.client.EntityInfoEntity;

/**
 * @author Arun KA
 *
 */
public interface EntityService {
	
	public List<EntityInfoEntity> getAllEntities(String groupCode);
	
	public List<String> getGSTINsForEntity(Long entityId);
	
	public Map<String, String> getStateNames(List<String> gstins);
	
	public Map<String, String> getRegType(List<String> gstins);

}
