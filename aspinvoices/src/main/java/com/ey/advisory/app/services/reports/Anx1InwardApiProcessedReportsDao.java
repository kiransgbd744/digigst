package com.ey.advisory.app.services.reports;

import java.util.List;

import com.ey.advisory.app.data.views.client.Anx1InwardApiProcessedRecordsDto;
import com.ey.advisory.core.search.SearchCriteria;

public interface Anx1InwardApiProcessedReportsDao {
	
	List<Anx1InwardApiProcessedRecordsDto> getInwardApiProcessedReports(
			SearchCriteria criteria);


}


