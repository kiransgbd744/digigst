package com.ey.advisory.app.services.reports;

import java.util.List;

import com.ey.advisory.app.data.views.client.Anx1InwardProcessedRecordsDto;
import com.ey.advisory.app.docs.dto.Anx1FileStatusReportsReqDto;

public interface Anx1InwardProcessedReportsDao {
	List<Anx1InwardProcessedRecordsDto> getInwardProcessedReports(
			Anx1FileStatusReportsReqDto request);

}


