package com.ey.advisory.app.services.daos.get2a;

import java.util.List;

public interface GetGstr1DetailStatusFetchDao {

	public List<Object[]> getGstinsByEntityId(String entityId);

	public List<Object[]> getDataUploadedStatusDetails(List<String> gstin,
			String taxPeriod, String origin);

	public List<Object[]> getGstr8SaveStatusDetails(List<String> gstin,
			String taxPeriod);
	
	public List<Object[]> getGstr1ADataUploadedStatusDetails(List<String> gstin,
			String taxPeriod, String origin);

}
