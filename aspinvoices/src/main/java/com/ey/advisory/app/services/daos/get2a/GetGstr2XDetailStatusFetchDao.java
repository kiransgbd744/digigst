package com.ey.advisory.app.services.daos.get2a;

import java.util.List;

/**
 * 
 * @author SriBhavya
 *
 */
public interface GetGstr2XDetailStatusFetchDao {

	public List<Object[]> getDataUploadedStatusDetails(List<String> gstin, String taxPeriod, String type);
	
}
