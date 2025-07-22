/**
 * 
 */
package com.ey.advisory.app.gstr1a.einv;

import com.aspose.cells.Workbook;
import com.ey.advisory.core.search.PageRequest;
import com.ey.advisory.core.search.SearchCriteria;

/**
 * @author Shashikant.Shukla
 *
 * 
 */
public interface Gstr1AAspProcessVsSubmitReportService {

	public Workbook AspProcessVsSubmitReports(SearchCriteria criteria,
			PageRequest pageReq);

}
