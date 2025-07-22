package com.ey.advisory.app.services.feedback;

import com.aspose.cells.Workbook;

/**
 * @author Shashikant.Shukla
 *
 * 
 */

public interface FeedbackReportService {
	public Workbook findReportDownload(FeedbackReqReportDto criteria);

}
