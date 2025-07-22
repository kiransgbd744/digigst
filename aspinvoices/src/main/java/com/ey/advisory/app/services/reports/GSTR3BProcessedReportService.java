package com.ey.advisory.app.services.reports;

import com.ey.advisory.core.search.PageRequest;
import com.ey.advisory.core.search.SearchCriteria;

public interface GSTR3BProcessedReportService {
	
	public void generateProcessedCsv(SearchCriteria criteria,
			PageRequest pageReq, String fullPath);

}



