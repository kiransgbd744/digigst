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
public interface Anx1ASPIMPSSavableService {
	
	public Workbook findAnx1IMPSSavableReports(SearchCriteria criteria,
			PageRequest pageReq);

	
}


