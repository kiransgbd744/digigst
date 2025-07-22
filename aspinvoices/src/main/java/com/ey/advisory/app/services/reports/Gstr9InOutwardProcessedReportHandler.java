/**
 * 
 */
package com.ey.advisory.app.services.reports;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.aspose.cells.Workbook;
import com.ey.advisory.app.docs.dto.Gstr1VerticalDownloadReportsReqDto;

/**
 * 
 * @author Mahesh.Golla
 *
 */
@Component("Gstr9InOutwardProcessedReportHandler")
public class Gstr9InOutwardProcessedReportHandler {
	
	
	@Autowired
	@Qualifier("Gstr9InOutwardProcessReportsServiceImpl")
	private Gstr1VerticalReportsService gstr1VerticalReportsService;

	public Workbook downloadGstr9InOutwardProcessedReport(
			Gstr1VerticalDownloadReportsReqDto criteria) {

		return gstr1VerticalReportsService.downloadReports(criteria, null);

	}

}