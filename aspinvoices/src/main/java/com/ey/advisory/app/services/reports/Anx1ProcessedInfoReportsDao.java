package com.ey.advisory.app.services.reports;

import java.util.List;

import com.ey.advisory.app.data.views.client.Anx1ProcesssedInfo;
import com.ey.advisory.app.docs.dto.Anx1FileStatusReportsReqDto;
import com.ey.advisory.app.docs.dto.Anx1ReportSearchReqDto;

public interface Anx1ProcessedInfoReportsDao {

	List<Anx1ProcesssedInfo> getProcessedInfoReports(
			Anx1FileStatusReportsReqDto request);
}
