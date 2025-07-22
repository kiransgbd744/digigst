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
public interface Gstr1ASPInvSavableService {
	
	public Workbook findGstr1InvSavableReports(SearchCriteria criteria,
			PageRequest pageReq);

	
}


