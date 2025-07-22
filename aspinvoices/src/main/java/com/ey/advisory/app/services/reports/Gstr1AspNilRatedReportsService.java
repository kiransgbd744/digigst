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
public interface Gstr1AspNilRatedReportsService {

	public Workbook findNilReport(SearchCriteria criteria, PageRequest pageReq);
}
