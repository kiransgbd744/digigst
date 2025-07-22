/**
 * 
 */
package com.ey.advisory.app.services.reports.gstr1a;

import com.aspose.cells.Workbook;
import com.ey.advisory.core.search.PageRequest;
import com.ey.advisory.core.search.SearchCriteria;

/**
 * @author Shashikant.Shukla
 *
 * 
 */
public interface Gstr1AASPInvSavableService {
	
	public Workbook findGstr1InvSavableReports(SearchCriteria criteria,
			PageRequest pageReq);

	
}


