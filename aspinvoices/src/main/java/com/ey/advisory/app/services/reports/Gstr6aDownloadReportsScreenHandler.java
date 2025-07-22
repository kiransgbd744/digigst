package com.ey.advisory.app.services.reports;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.aspose.cells.Workbook;
import com.ey.advisory.app.docs.dto.gstr6a.Gstr6AProcessedDataRequestDto;

@Component("Gstr6aDownloadReportsScreenHandler")
public class Gstr6aDownloadReportsScreenHandler {

	@Autowired
	@Qualifier("Gstr6aDownloadReportServiceImpl")
	private Gstr6aDownloadReportService gstr6aDownloadReportService;

	public Workbook downloadGStr6aReport(
			Gstr6AProcessedDataRequestDto criteria) {

		return gstr6aDownloadReportService.findGstr6aDownloadReport(criteria);

	}
	
	public Workbook downloadGStr6aReportDashboard(
			Gstr6AProcessedDataRequestDto criteria) {

		return gstr6aDownloadReportService.findGstr6aDownloadReportDashboard(criteria);

	}

}
