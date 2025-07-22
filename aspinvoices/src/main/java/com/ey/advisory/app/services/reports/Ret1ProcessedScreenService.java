/**
 * 
 */
package com.ey.advisory.app.services.reports;

import com.aspose.cells.Workbook;
import com.ey.advisory.app.services.ret1.Ret1ProcessedRecordsRequestDto;
import com.ey.advisory.core.search.PageRequest;
import com.ey.advisory.core.search.SearchCriteria;

/**
 * @author Sujith.Nanga
 *
 * 
 */
public interface Ret1ProcessedScreenService {

	public Workbook findProcessedScreen(Ret1ProcessedRecordsRequestDto criteria,
			PageRequest pageReq) throws Exception;

}
