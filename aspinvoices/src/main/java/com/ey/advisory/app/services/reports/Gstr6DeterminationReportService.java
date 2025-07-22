/**
 * 
 */
package com.ey.advisory.app.services.reports;

import com.aspose.cells.Workbook;
import com.ey.advisory.core.search.PageRequest;
import com.ey.advisory.core.search.SearchCriteria;

/**
 * @author Mahesh.Golla
 *
 * 
 */
public interface Gstr6DeterminationReportService {

	public Workbook gstr6DeterminationReports(SearchCriteria criteria,
			PageRequest pageReq);

}
