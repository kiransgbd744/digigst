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
 */
public interface Gstr1AASPAdvRecSavableService {
	
	public Workbook findGstr1AdvRecSavableReports(SearchCriteria criteria,
			PageRequest pageReq);

}
