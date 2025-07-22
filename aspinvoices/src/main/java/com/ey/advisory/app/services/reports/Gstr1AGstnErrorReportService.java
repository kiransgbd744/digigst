/**
 * 
 */
package com.ey.advisory.app.services.reports;

import java.util.List;

import com.aspose.cells.Workbook;
import com.ey.advisory.app.docs.dto.GstnConsolidatedReqDto;
import com.ey.advisory.core.search.PageRequest;

/**
 * @author Sujith.Nanga
 *
 * 
 */
public interface Gstr1AGstnErrorReportService {
	
	public Workbook findGstnErrorReports(List<GstnConsolidatedReqDto> criteria,
			PageRequest pageReq);

}

