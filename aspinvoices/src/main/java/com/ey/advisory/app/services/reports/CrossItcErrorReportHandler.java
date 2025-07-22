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
@Component("CrossItcErrorReportHandler")
public class CrossItcErrorReportHandler {

	@Autowired
	@Qualifier("CrossItcErrorReportsServiceImpl")
	private Gstr1VerticalReportsService crossItcErrorReportsServiceImpl;

	public Workbook downloadCrossItcErrorReport(
			Gstr1VerticalDownloadReportsReqDto criteria) {

		return crossItcErrorReportsServiceImpl.downloadReports(criteria, null);

	}

}