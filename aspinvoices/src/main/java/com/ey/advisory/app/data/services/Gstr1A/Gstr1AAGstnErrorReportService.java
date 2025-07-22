/**
 * 
 */
package com.ey.advisory.app.data.services.Gstr1A;

import java.util.List;

import com.aspose.cells.Workbook;
import com.ey.advisory.app.docs.dto.GstnConsolidatedReqDto;
import com.ey.advisory.core.search.PageRequest;

/**
 * @author Sakshi.Jain
 *
 * 
 */
public interface Gstr1AAGstnErrorReportService {
	
	public Workbook findGstnErrorReports(List<GstnConsolidatedReqDto> criteria,
			PageRequest pageReq);

}

