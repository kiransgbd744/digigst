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
public interface Gstr1AReviewSummReportsService {

	public Workbook findGstr1ReviewSummRecords(SearchCriteria criteria,
			PageRequest pageReq);

}
