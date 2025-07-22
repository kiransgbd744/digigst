package com.ey.advisory.app.services.reports;

import com.ey.advisory.core.search.SearchCriteria;

public interface Anx1AsyncFileStatusCsvReportsService {
	
	public void generateFileStatusCsv(SearchCriteria criteria,
			Long reqId, String fullPath);

}
