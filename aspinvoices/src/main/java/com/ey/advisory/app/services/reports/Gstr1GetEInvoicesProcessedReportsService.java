package com.ey.advisory.app.services.reports;

import com.aspose.cells.Workbook;
import com.ey.advisory.core.search.PageRequest;
import com.ey.advisory.core.search.SearchCriteria;

/**
 * 
 * @author Anand3.M
 *
 */
public interface Gstr1GetEInvoicesProcessedReportsService {
	public Workbook findGstr1GetEInvoicesRecords(SearchCriteria criteria,
			PageRequest pageReq);

}
