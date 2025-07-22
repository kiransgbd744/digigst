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
public interface Anx1ASPRCSavableService {

	public Workbook findRCSavableReports(SearchCriteria criteria,
			PageRequest pageReq);

}
