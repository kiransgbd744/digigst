package com.ey.advisory.app.services.daos.get2a;

import java.util.List;

public interface GetSaveStatusFetchDao {

	public List<Object[]> getGstinsByEntityId(String entityId, String gstin);

	public List<Object[]> getSaveStatusDetailsByReturnType(String gstinObj,
			String taxPeriod, String returnType);

}
