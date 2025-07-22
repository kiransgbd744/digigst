package com.ey.advisory.app.services.reports;

import com.aspose.cells.Workbook;
import com.ey.advisory.core.search.PageRequest;
import com.ey.advisory.core.search.SearchCriteria;

public interface Gstr6GstnErrorReportService {
	
	public Workbook findGstnErrorReports(SearchCriteria criteria,
			PageRequest pageReq);

}

