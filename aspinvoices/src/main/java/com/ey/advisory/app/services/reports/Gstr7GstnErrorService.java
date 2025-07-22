package com.ey.advisory.app.services.reports;

import com.aspose.cells.Workbook;
import com.ey.advisory.core.search.PageRequest;
import com.ey.advisory.core.search.SearchCriteria;

public interface Gstr7GstnErrorService {
	
	public Workbook findGstr7GstnSummRecords(SearchCriteria criteria,
			PageRequest pageReq);

}
