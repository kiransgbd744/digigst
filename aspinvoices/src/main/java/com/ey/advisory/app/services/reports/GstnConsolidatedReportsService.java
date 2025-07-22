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
public interface GstnConsolidatedReportsService {
	

		public Workbook generateGstnReports(SearchCriteria criteria,
				PageRequest pageReq);

}
