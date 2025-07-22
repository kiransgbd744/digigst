package com.ey.advisory.app.services.daos.get2a;

import java.util.List;

/**
 * 
 * @author Sri bhavya
 *
 */
public interface GetItc04DetailStatusFetchDao {
	
	public List<Object[]> getGstinsByEntityId(String entityId);

	public List<Object[]> getDataUploadedStatusDetails(List<String> gstin,
			String taxPeriod);
}
