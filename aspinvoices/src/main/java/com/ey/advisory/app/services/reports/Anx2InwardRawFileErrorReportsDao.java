package com.ey.advisory.app.services.reports;

import java.util.List;

import com.ey.advisory.app.data.views.client.Anx1ApiErrorRecordsDto;
import com.ey.advisory.app.data.views.client.Anx2InwardErrorRecordsDto;
import com.ey.advisory.core.search.SearchCriteria;

public interface Anx2InwardRawFileErrorReportsDao {
	
	List<Anx2InwardErrorRecordsDto> getErrorReports(
			SearchCriteria criteria);

}


