package com.ey.advisory.app.services.reports;

import java.util.List;

import com.ey.advisory.app.data.views.client.Anx1ApiDataStatusSummaryDto;
import com.ey.advisory.app.docs.dto.Anx1ReportSearchReqDto;


public interface Anx1OutwardApiDatastatusSummaryDao {
	
	
	List<Anx1ApiDataStatusSummaryDto> getOutwardDataStatusSummary(
			Anx1ReportSearchReqDto request);

}




