package com.ey.advisory.app.services.daos.get2a;

import java.util.List;

public interface GetAnx2DetailStatusFetchDao {

	public List<Object[]> getGstinsByEntityId(String entityId);

	public List<Object[]> getDataUploadedStatusDetails(String gstin, String taxPeriod);


}
