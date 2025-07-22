package com.ey.advisory.app.services.reports;

import com.aspose.cells.Workbook;
import com.ey.advisory.core.search.PageRequest;
import com.ey.advisory.core.search.SearchCriteria;

public interface Anx1ProcessedInfoReportsService {

	public Workbook findProcessedInfoRec(SearchCriteria criteria,
			PageRequest pageReq);

}
