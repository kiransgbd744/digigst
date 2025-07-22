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
 * @author Mahesh.Golla
 *
 */
@Component("Gstr9InOutwardErrorReportHandler")
public class Gstr9InOutwardErrorReportHandler {

	@Autowired
	@Qualifier("Gstr9InOutwardErrorReportsServiceImpl")
	private Gstr1VerticalReportsService gstr9InOutwardErrorReportsServiceImpl;

	public Workbook downloadGstr9InOutwardErrorReport(
			Gstr1VerticalDownloadReportsReqDto criteria) {

		return gstr9InOutwardErrorReportsServiceImpl.downloadReports(criteria,
				null);

	}

}