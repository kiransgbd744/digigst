package com.ey.advisory.app.services.reports;

import com.ey.advisory.core.search.PageRequest;
import com.ey.advisory.core.search.SearchCriteria;

public interface GSTR3BTotalReportService {
	
	public void generateTotalCsv(SearchCriteria criteria,
			PageRequest pageReq, String fullPath);

}


