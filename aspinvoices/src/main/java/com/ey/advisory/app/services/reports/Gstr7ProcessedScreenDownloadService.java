/**
 * 
 */
package com.ey.advisory.app.services.reports;

import com.aspose.cells.Workbook;
import com.ey.advisory.core.dto.Gstr1ReviwSummReportsReqDto;
import com.ey.advisory.core.search.PageRequest;
import com.ey.advisory.core.search.SearchCriteria;

/**
 * @author Sujith.Nanga
 *
 */
public interface Gstr7ProcessedScreenDownloadService {
	
	public Workbook findProcessedScreenDownload(SearchCriteria criteria,
			PageRequest pageReq);


}


