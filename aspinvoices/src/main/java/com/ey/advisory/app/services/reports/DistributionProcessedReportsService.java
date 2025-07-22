package com.ey.advisory.app.services.reports;

import com.ey.advisory.core.search.PageRequest;
import com.ey.advisory.core.search.SearchCriteria;

public interface DistributionProcessedReportsService {
	
		public void generateProcessedCsv(SearchCriteria criteria,
				PageRequest pageReq, String fullPath);

	}


