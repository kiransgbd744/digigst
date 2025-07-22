package com.ey.advisory.app.services.configuremaster.reports;

import com.aspose.cells.Workbook;
import com.ey.advisory.core.search.PageRequest;
import com.ey.advisory.core.search.SearchCriteria;

public interface MasterErrorReportsService {
	public Workbook downloadMasterErrorReports(final SearchCriteria criteria,
	        final PageRequest pageReq);

}
