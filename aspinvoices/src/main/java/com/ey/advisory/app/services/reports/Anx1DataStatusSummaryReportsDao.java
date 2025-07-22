package com.ey.advisory.app.services.reports;

import java.util.List;

import com.ey.advisory.app.data.views.client.Anx1DataStatusSummaryReportView;
import com.ey.advisory.app.docs.dto.Anx1ReportSearchReqDto;

public interface Anx1DataStatusSummaryReportsDao {
	
	List<Anx1DataStatusSummaryReportView> getDataStatusSummaryReports(
			Anx1ReportSearchReqDto request);


}
