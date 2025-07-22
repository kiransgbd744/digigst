/**
 * 
 */
package com.ey.advisory.app.services.reports.gstr1a;

import com.aspose.cells.Workbook;
import com.ey.advisory.core.search.PageRequest;
import com.ey.advisory.core.search.SearchCriteria;

/**
 * @author Laxmi.Salukuti
 *
 */
public interface Gstr1AAspNilRatedReportsService {

	public Workbook findNilReport(SearchCriteria criteria, PageRequest pageReq);
}
