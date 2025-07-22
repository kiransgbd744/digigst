package com.ey.advisory.app.services.daos.get2a;

import java.util.List;

import com.ey.advisory.app.docs.dto.GetAnx2DetailStatusReqDto;

public interface GetGstr2aStatusFetchDao {

	List<Object[]> getDataUploadedStatusDetails(String gstin, String taxPeriod);

	List<Object[]> getGstinsByEntityId(GetAnx2DetailStatusReqDto criteria);

}
