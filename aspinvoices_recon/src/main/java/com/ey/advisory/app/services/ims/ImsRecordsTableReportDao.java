package com.ey.advisory.app.services.ims;

import java.util.List;

import com.ey.advisory.app.service.ims.ImsEntitySummaryReqDto;

public interface ImsRecordsTableReportDao {
	
	List<Object> getEntityLevelSummary(ImsEntitySummaryReqDto criteria);


}
