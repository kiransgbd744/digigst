package com.ey.advisory.app.data.daos.client;

import java.util.List;

import com.ey.advisory.app.data.entities.client.Gstr2DataStatusEntity;

public interface Gstr2DataStatusDao {
	
	List<Gstr2DataStatusEntity> dataGstr2StatusSection(
			String sectionType,String buildQuery, List<String> sgstins,
			Object dataRecvFrom, Object dataRecvTo);

}
