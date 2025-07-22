package com.ey.advisory.app.services.reports;

import java.util.List;

import com.ey.advisory.app.data.views.client.Anx1ApiErrorRecordsDto;
import com.ey.advisory.app.docs.dto.Anx1ReportSearchReqDto;
import com.ey.advisory.core.search.SearchCriteria;

public interface Anx1ApiErrorReportsDao {

	List<Anx1ApiErrorRecordsDto> getApiErrorReports(
			SearchCriteria criteria);

}
