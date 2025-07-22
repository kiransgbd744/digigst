package com.ey.advisory.app.services.reports;

import com.aspose.cells.Workbook;
import com.ey.advisory.core.search.PageRequest;
import com.ey.advisory.core.search.SearchCriteria;

/**
 * 
 * @author Balakrishna.S
 *
 */
public interface Gstr2PREntityLevelSummaryService {

	public Workbook findEntityLevelSummary(SearchCriteria searchParams,
			PageRequest pageReq);
	
	
}
