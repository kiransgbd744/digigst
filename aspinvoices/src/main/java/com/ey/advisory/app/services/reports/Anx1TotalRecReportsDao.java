package com.ey.advisory.app.services.reports;

import java.util.List;

import com.ey.advisory.app.data.views.client.Anx1TotalRecordsView;
import com.ey.advisory.app.docs.dto.Anx1FileStatusReportsReqDto;
import com.ey.advisory.app.docs.dto.Anx1ReportSearchReqDto;

public interface Anx1TotalRecReportsDao {
	
	List<Anx1TotalRecordsView> getTotalRecReports(
			Anx1FileStatusReportsReqDto request);


}
