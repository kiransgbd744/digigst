/**
 * 
 */
package com.ey.advisory.app.services.reports;

import com.aspose.cells.Workbook;
import com.ey.advisory.core.search.PageRequest;
import com.ey.advisory.core.search.SearchCriteria;

/**
 * @author Sujith.Nanga
 *
 * 
 */
public interface Anx2PrSummaryService {
	
	public Workbook findAnx2PRsummaryReports(SearchCriteria criteria,
			PageRequest pageReq);

	
}


