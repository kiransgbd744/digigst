/**
 * 
 */
package com.ey.advisory.app.services.reports;

import java.util.List;

import com.aspose.cells.Workbook;
import com.ey.advisory.app.docs.dto.GstnConsolidatedReqDto;
import com.ey.advisory.core.search.PageRequest;
import com.ey.advisory.core.search.SearchCriteria;

/**
 * @author Laxmi.Salukuti
 *
 */
public interface Gstr1GstnErrorReportService {

	public Workbook findGstnErrorReports(SearchCriteria criteria,
			PageRequest pageReq);

}
