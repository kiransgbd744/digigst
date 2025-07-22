package com.ey.advisory.app.services.reports;

import com.aspose.cells.Workbook;
import com.ey.advisory.app.docs.dto.gstr6a.Gstr6AProcessedDataRequestDto;

public interface Gstr6aDownloadReportService {
	public Workbook findGstr6aDownloadReport(
			Gstr6AProcessedDataRequestDto criteria);
	
	public Workbook findGstr6aDownloadReportDashboard(
			Gstr6AProcessedDataRequestDto criteria);


}
