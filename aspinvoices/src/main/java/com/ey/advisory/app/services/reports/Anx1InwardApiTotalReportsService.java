package com.ey.advisory.app.services.reports;

import com.aspose.cells.Workbook;
import com.ey.advisory.core.search.PageRequest;
import com.ey.advisory.core.search.SearchCriteria;

public interface Anx1InwardApiTotalReportsService {
	public Workbook findInwardApiTotalRec(SearchCriteria criteria,
			PageRequest pageReq);

}



