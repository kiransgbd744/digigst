/**
 * 
 */
package com.ey.advisory.app.data.returns.compliance.service;

import com.aspose.cells.Workbook;
import com.ey.advisory.core.dto.Gstr2aProcessedDataRecordsReqDto;
import com.ey.advisory.core.search.PageRequest;

/**
 * @author Sujith.Nanga
 *
 */
public interface ComplainceReportService {

	public Workbook findComplaince(Gstr2aProcessedDataRecordsReqDto criteria,
			PageRequest pageReq);

}
