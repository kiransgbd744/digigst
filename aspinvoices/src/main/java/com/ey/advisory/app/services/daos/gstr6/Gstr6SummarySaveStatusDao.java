package com.ey.advisory.app.services.daos.gstr6;

import java.util.List;

public interface Gstr6SummarySaveStatusDao {

	public List<Object[]> getGstinsByEntityId(String entityId);

	public List<Object[]> getDataUploadedStatusDetails(List<String> gstin,
			String taxPeriod);
}
