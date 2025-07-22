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
@Component("CewbErrorReportHandler")
public class CewbErrorReportHandler {

	@Autowired
	@Qualifier("CewbErrorReportsServiceImpl") 
	private CewbErrorReportsServiceImpl cewbErrorReportsServiceImpl;

	public Workbook downloadCewbErrorReport(
			Gstr1VerticalDownloadReportsReqDto criteria) {

		return cewbErrorReportsServiceImpl.downloadReports(criteria, null);

	}

}