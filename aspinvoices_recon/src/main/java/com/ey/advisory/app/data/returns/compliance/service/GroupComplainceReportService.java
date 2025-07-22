/**
 * 
 */
package com.ey.advisory.app.data.returns.compliance.service;

import com.aspose.cells.Workbook;
import com.ey.advisory.core.search.PageRequest;

/**
 * @author Shashikant.Shukla
 *
 */
public interface GroupComplainceReportService {

	public Workbook findComplaince(GroupComplianceHistoryDataRecordsReqDto criteria,
			PageRequest pageReq);

}
