package com.ey.advisory.app.services.reports;

import java.util.List;

import com.ey.advisory.app.data.views.client.Anx1ProcessedRecordsView;
import com.ey.advisory.app.docs.dto.Anx1FileStatusReportsReqDto;

public interface Anx1ProcessedReportsDao {

	List<Anx1ProcessedRecordsView> getProcessedReports(
			Anx1FileStatusReportsReqDto request);

}
