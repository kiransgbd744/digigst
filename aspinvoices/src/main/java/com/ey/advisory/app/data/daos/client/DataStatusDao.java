package com.ey.advisory.app.data.daos.client;

import java.util.List;

import com.ey.advisory.app.data.entities.client.DataStatusEntity;
/**
 * 
 * @author Balakrishna.S
 *
 */

public interface DataStatusDao {
	
	List<DataStatusEntity> dataStatusSection(
			String sectionType,String buildQuery, List<String> sgstins,
			Object dataRecvFrom, Object dataRecvTo);


}
