/**
 * 
 */
package com.ey.advisory.app.services.reports;

import com.aspose.cells.Workbook;
import com.ey.advisory.core.search.PageRequest;
import com.ey.advisory.core.search.SearchCriteria;

/**
 * @author Laxmi.Salukuti
 *
 */
public interface Gstr1ReviewSummReportsService {

	public Workbook findGstr1ReviewSummRecords(SearchCriteria criteria,
			PageRequest pageReq);

}
