package com.ey.advisory.app.services.reports;

import java.util.List;

import com.ey.advisory.app.data.views.client.Anx1InwardProcessedInfoRecordsDto;
import com.ey.advisory.app.docs.dto.Anx1FileStatusReportsReqDto;

public interface Anx1InwardProcessedInfoReportsDao {
	List<Anx1InwardProcessedInfoRecordsDto> getInwardProcessedInfoReports(
			Anx1FileStatusReportsReqDto request);

}


