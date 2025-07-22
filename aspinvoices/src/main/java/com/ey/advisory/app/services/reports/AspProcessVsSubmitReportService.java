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
public interface AspProcessVsSubmitReportService {

	public Workbook AspProcessVsSubmitReports(SearchCriteria criteria,
			PageRequest pageReq);

}
