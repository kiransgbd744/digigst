package com.ey.advisory.app.services.reports.gstr1;

import com.aspose.cells.Workbook;
import com.ey.advisory.core.search.PageRequest;
import com.ey.advisory.core.search.SearchCriteria;

public interface Gstr1ProcessedReportsService {

	public Workbook find(SearchCriteria criteria, PageRequest pageReq);

	public Workbook findError(SearchCriteria criteria, PageRequest pageReq);

	public Workbook findTotRec(SearchCriteria criteria, PageRequest pageReq);
	
	public Workbook findInfoRec(SearchCriteria criteria, PageRequest pageReq);

}
